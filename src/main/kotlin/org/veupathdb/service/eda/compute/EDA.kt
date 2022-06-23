package org.veupathdb.service.eda.compute

import org.gusdb.fgputil.Tuples.TwoTuple
import org.veupathdb.lib.compute.platform.AsyncPlatform
import org.veupathdb.lib.hash_id.HashID
import org.veupathdb.lib.jackson.Json
import org.veupathdb.service.eda.common.client.EdaMergingClient
import org.veupathdb.service.eda.common.client.EdaSubsettingClient
import org.veupathdb.service.eda.common.client.spec.StreamSpec
import org.veupathdb.service.eda.common.model.ReferenceMetadata
import org.veupathdb.service.eda.compute.exec.PluginJobPayload
import org.veupathdb.service.eda.compute.plugins.PluginProvider
import org.veupathdb.service.eda.compute.service.ServiceOptions
import org.veupathdb.service.eda.compute.util.toJobResponse
import org.veupathdb.service.eda.generated.model.APIFilter
import org.veupathdb.service.eda.generated.model.APIStudyDetail
import org.veupathdb.service.eda.generated.model.ComputeRequestBase
import org.veupathdb.service.eda.generated.model.JobResponse
import java.io.InputStream
import java.util.*

object EDA {

  /**
   * Fetches the [APIStudyDetail] information from the EDA Subsetting Service
   * for the given [studyID] if such a study exists.
   *
   * @param studyID ID of the study whose metadata should be retrieved.
   *
   * @param auth Auth header sent in with the job HTTP request.
   *
   * @return An [Optional] that will wrap the `APIStudyDetail` returned from the
   * EDA Subsetting Service, if such a study exists.  If the target study was
   * not found, the returned `Optional` will be empty.
   */
  @JvmStatic
  fun getAPIStudyDetail(studyID: String, auth: TwoTuple<String, String>): Optional<APIStudyDetail> =
    EdaSubsettingClient(ServiceOptions.edaSubsettingHost, auth).getStudy(studyID)

  /**
   * Fetches the [APIStudyDetail] information from the EDA Subsetting Service
   * for the given [studyID], throwing an exception if no such study exists.
   *
   * @param studyID ID of the study whose metadata should be retrieved.
   *
   * @param auth Auth header sent in with the job HTTP request.
   *
   * @param err Optional exception provider that will be used to get the
   * exception that will be thrown if the target study does not exist.
   *
   * @return The `APIStudyDetail` information returned from the EDA Subsetting
   * Service.
   *
   * @throws Exception Throws the exception returned by the given exception
   * provider ([err]).
   */
  @JvmStatic
  @JvmOverloads
  fun requireAPIStudyDetail(
    studyID: String,
    auth: TwoTuple<String, String>,
    err: (studyID: String) -> Exception = this::noStudyDetail
  ): APIStudyDetail = getAPIStudyDetail(studyID, auth).orElseThrow { err(studyID) }

  /**
   * Fetches tabular study data from the EDA Merge Service for the given params.
   *
   * @param refMeta TODO: What is this?
   *
   * @param filters TODO: What is this?
   *
   * @param spec TODO: What is this?
   *
   * @param auth Auth header sent in with the job HTTP request.
   *
   * @return An [InputStream] over the tabular data returned from the EDA Merge
   * Service.
   */
  @JvmStatic
  fun getMergeData(
    refMeta: ReferenceMetadata,
    filters: List<APIFilter>,
    spec: StreamSpec,
    auth: TwoTuple<String, String>
  ): InputStream =
    EdaMergingClient(ServiceOptions.edaMergeHost, auth)
      .getTabularDataStream(refMeta, filters, spec).inputStream


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