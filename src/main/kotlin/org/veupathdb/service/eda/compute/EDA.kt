package org.veupathdb.service.eda.compute

import org.gusdb.fgputil.Tuples.TwoTuple
import org.veupathdb.lib.compute.platform.AsyncPlatform
import org.veupathdb.lib.hash_id.HashID
import org.veupathdb.lib.jackson.Json
import org.veupathdb.service.eda.common.client.EdaMergingClient
import org.veupathdb.service.eda.common.client.EdaSubsettingClient
import org.veupathdb.service.eda.compute.exec.PluginJobPayload
import org.veupathdb.service.eda.compute.plugins.PluginProvider
import org.veupathdb.service.eda.compute.service.ServiceOptions
import org.veupathdb.service.eda.compute.util.toJobResponse
import org.veupathdb.service.eda.generated.model.APIStudyDetail
import org.veupathdb.service.eda.generated.model.ComputeRequestBase
import org.veupathdb.service.eda.generated.model.JobResponse
import java.io.InputStream
import java.util.*

object EDA {

  @JvmStatic
  fun getAPIStudyDetail(studyID: String, auth: TwoTuple<String, String>): Optional<APIStudyDetail> =
    EdaSubsettingClient(ServiceOptions.edaSubsettingHost, auth).getStudy(studyID)

  @JvmStatic
  fun requireAPIStudyDetail(
    studyID: String,
    auth: TwoTuple<String, String>,
    err: (studyID: String) -> Exception = this::noStudyDetail
  ): APIStudyDetail = getAPIStudyDetail(studyID, auth).orElseThrow { err(studyID) }

  @JvmStatic
  fun getMergeData(auth: TwoTuple<String, String>): InputStream =
    EdaMergingClient(ServiceOptions.edaMergeHost, auth)
      .getTabularDataStream().inputStream


  @JvmStatic
  fun <R : ComputeRequestBase, C> submitComputeJob(
    plugin: PluginProvider<R, C>,
    payload: R,
    auth: TwoTuple<String, String>,
  ): JobResponse {
    val serial = Json.convert(payload)
    val jobID  = HashID.ofMD5(serial.toString())
    val jobPay = PluginJobPayload(plugin.)

    AsyncPlatform.submitJob(plugin.targetQueue.queueName) {
      this.jobID  = jobID
      this.config = serial
    }

    return AsyncPlatform.getJob(jobID)!!.toJobResponse()
  }

  @JvmStatic
  private fun noStudyDetail(studyID: String) =
    Exception("Could not get APIStudyDetail for study $studyID")
}