package org.veupathdb.service.eda.compute.exec

import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.ThreadContext
import org.veupathdb.lib.compute.platform.job.JobContext
import org.veupathdb.lib.compute.platform.job.JobExecutor
import org.veupathdb.lib.compute.platform.job.JobResult
import org.veupathdb.lib.jackson.Json
import org.veupathdb.service.eda.compute.EDA
import org.veupathdb.service.eda.compute.jobs.Const
import org.veupathdb.service.eda.compute.plugins.PluginRegistry
import org.veupathdb.service.eda.compute.plugins.PluginWorkspace

private val OutputFiles = arrayOf(
  Const.OutputFileMeta,
  Const.OutputFileStats,
  Const.OutputFileTabular,
  Const.OutputFileErrors,
  Const.OutputFileException
)

private const val ThreadContextKey = "JOB_ID"

class PluginExecutor : JobExecutor {

  private val Log = LogManager.getLogger(javaClass)

  override fun execute(ctx: JobContext): JobResult {
    // Add the job id to the logger thread context so log messages in this
    // worker thread will be marked as part of this job.

    ThreadContext.put(ThreadContextKey, ctx.jobID.string)
    Log.info("Executing job {}", ctx.jobID)

    // Parse the raw job payload from the queue message
    val jobPayload = Json.parse<PluginJobPayload>(ctx.config!!)

    // Get the plugin provider for the job
    val provider = PluginRegistry.get(jobPayload.plugin)!!

    // Deserialize the
    val request = Json.parse(jobPayload.request, provider.requestClass)

    // Fetch the study metadata and write it out to the local workspace
    Log.debug("retrieving api study details")
    val studyDetail = EDA.requireAPIStudyDetail(request.studyId, jobPayload.authHeader)
    ctx.workspace.write(Const.InputFileMeta, Json.convert(studyDetail))

    // Build the plugin context
    val context = provider.getContextBuilder().also {
      it.request = request
      it.workspace = PluginWorkspace(ctx.workspace)
      it.jobContext = ComputeJobContext(ctx.jobID)
      it.pluginMeta = provider
    }.build()

    // Run the plugin.
    Log.debug("running plugin")
    val plugin = provider.createPlugin(context)

    // Fetch the tabular data and write it out to the local workspace
    plugin.streamSpecs.forEach { spec ->
      Log.debug("retrieving tabular study data: {}", spec.streamName)
      ctx.workspace.write(spec.streamName, EDA.getMergeData(
        context.referenceMetadata,
        request.filters,
        spec,
        jobPayload.authHeader
      ))
    }

    ThreadContext.remove(ThreadContextKey)
    return JobResult.success(*OutputFiles)
  }
}