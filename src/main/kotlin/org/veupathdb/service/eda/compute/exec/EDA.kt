package org.veupathdb.service.eda.compute.exec

import org.gusdb.fgputil.Tuples.TwoTuple
import org.veupathdb.service.eda.common.client.EdaMergingClient
import org.veupathdb.service.eda.common.client.EdaSubsettingClient
import org.veupathdb.service.eda.common.client.spec.StreamSpec
import org.veupathdb.service.eda.compute.plugins.PluginProvider
import org.veupathdb.service.eda.compute.service.ServiceOptions
import org.veupathdb.service.eda.generated.model.APIStudyDetail
import org.veupathdb.service.eda.generated.model.ComputeRequestBase
import org.veupathdb.service.eda.generated.model.JobResponse
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

  fun getFoo(auth: TwoTuple<String, String>) =
    EdaMergingClient(ServiceOptions.edaMergeHost, auth)
      .getTabularDataStream()

  @JvmStatic
  fun <R : ComputeRequestBase, C> submitComputeJob(
    plugin: PluginProvider<R, C>,
    payload: R,
    auth: TwoTuple<String, String>,
  ): JobResponse {
  }

  @JvmStatic
  private fun noStudyDetail(studyID: String) =
    Exception("Could not get APIStudyDetail for study $studyID")
}