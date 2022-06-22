package org.veupathdb.service.eda.compute.exec

import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.ThreadContext
import org.veupathdb.lib.compute.platform.job.JobContext
import org.veupathdb.lib.compute.platform.job.JobExecutor
import org.veupathdb.lib.compute.platform.job.JobResult
import org.veupathdb.lib.jackson.Json
import org.veupathdb.service.eda.common.client.EdaSubsettingClient
import org.veupathdb.service.eda.compute.jobs.Const
import org.veupathdb.service.eda.compute.plugins.PluginProvider
import org.veupathdb.service.eda.compute.plugins.PluginRegistry
import org.veupathdb.service.eda.compute.service.ServiceOptions
import org.veupathdb.service.eda.compute.util.AuthHeader
import org.veupathdb.service.eda.generated.model.APIStudyDetail
import org.veupathdb.service.eda.generated.model.ComputeRequestBase

private val OutputFiles = arrayOf(
  Const.OutputFileMeta,
  Const.OutputFileStats,
  Const.OutputFileTabular,
)

class PluginExecutor : JobExecutor {

  private val Log = LogManager.getLogger(javaClass)

  /**
   *
   *
   * At this point:
   *
   * * we have a local workspace for our job
   */
  override fun execute(ctx: JobContext): JobResult {
    ThreadContext.put("JOB_ID", ctx.jobID.string)
    // TODO: Add log4j config with job_id in logging

    Log.info("Executing job {}", ctx.jobID)

    Log.trace("parsing payload from queue message")
    val jobPayload = Json.parse<PluginJobPayload>(ctx.config!!)

    Log.trace("getting plugin provider")
    val provider = PluginRegistry.get(jobPayload.plugin)

    Log.trace()
    val request = Json.parse(jobPayload.request, provider.requestClass)

    val studyDetail = fetchStudyDetails(jobPayload.authHeader, request)
    ctx.workspace.write(Const.InputFileMeta, Json.convert(studyDetail))



    // TODO: fetch the tabular data
    // TODO: write the tabular data out to file
    // TODO: construct the context

    val context =

    (provider as PluginProvider<ComputeRequestBase, Any>).createPlugin()

    TODO("Not yet implemented")

    return JobResult.success(*OutputFiles)
  }

  // TODO: Move me
  private fun fetchStudyDetails(auth: AuthHeader, request: ComputeRequestBase): APIStudyDetail {
    return EdaSubsettingClient(ServiceOptions.edaSubsettingHost, auth.asMapEntry())
      .getStudy(request.studyId)
      .orElseThrow { IllegalStateException() }
  }

  // TODO: Move me
  private fun

}