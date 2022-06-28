package org.veupathdb.service.eda.compute.exec

import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.ThreadContext
import org.veupathdb.lib.compute.platform.job.JobContext
import org.veupathdb.lib.compute.platform.job.JobExecutor
import org.veupathdb.lib.compute.platform.job.JobResult
import org.veupathdb.lib.jackson.Json
import org.veupathdb.service.eda.compute.EDA
import org.veupathdb.service.eda.compute.jobs.ReservedFiles
import org.veupathdb.service.eda.compute.plugins.AbstractPlugin
import org.veupathdb.service.eda.compute.plugins.PluginRegistry
import org.veupathdb.service.eda.compute.plugins.PluginWorkspace

private val OutputFiles = arrayOf(
  ReservedFiles.OutputMeta,
  ReservedFiles.OutputStats,
  ReservedFiles.OutputTabular,
  ReservedFiles.OutputErrors,
  ReservedFiles.OutputException
)

// These MUST match the %X variables defined in resources/log4j2.yml
private const val ThreadContextJobKey    = "JOB_ID"
private const val ThreadContextPluginKey = "PLUGIN"

class PluginExecutor : JobExecutor {

  private val Log = LogManager.getLogger(javaClass)

  override fun execute(ctx: JobContext): JobResult {
    // Add the job id to the logger thread context so log messages in this
    // worker thread will be marked as part of this job.
    ThreadContext.put(ThreadContextJobKey, ctx.jobID.string)
    Log.info("Executing job {}", ctx.jobID)

    // Parse the raw job payload from the queue message
    val jobPayload = Json.parse<PluginJobPayload>(ctx.config!!)

    // Get the plugin provider for the job
    val provider = PluginRegistry.get(jobPayload.plugin)!!

    // Add the plugin name to the logging context
    ThreadContext.put(ThreadContextPluginKey, provider.urlSegment)
    Log.debug("loaded plugin provider")

    // Deserialize the
    val request = Json.parse(jobPayload.request, provider.requestClass)

    // Convert the auth header to a Map.Entry for use with eda-common code.
    val authHeader = jobPayload.authHeader.toFgpTuple()

    // Fetch the study metadata and write it out to the local workspace
    Log.debug("retrieving api study details")
    val studyDetail = EDA.requireAPIStudyDetail(request.studyId, authHeader)
    ctx.workspace.write(ReservedFiles.InputMeta, Json.convert(studyDetail))

    // Build the plugin context
    val context = provider.getContextBuilder().also {
      it.studyDetail = studyDetail
      it.request = request
      it.workspace = PluginWorkspace(ctx.workspace)
      it.jobContext = ComputeJobContext(ctx.jobID)
      it.pluginMeta = provider
    }.build()

    // Run the plugin.
    val plugin = provider.createPlugin(context)

    // Validate the stream specs
    if (!validateStreamSpecs(plugin))
      // If the stream specs were not valid, exit here with a failed status.
      return JobResult.failure(*OutputFiles)

    // Fetch the tabular data and write it out to the local workspace
    plugin.streamSpecs.forEach { spec ->
      Log.debug("retrieving tabular study data: {}", spec.streamName)
      ctx.workspace.write(spec.streamName, EDA.getMergeData(
        context.referenceMetadata,
        request.filters,
        spec,
        authHeader
      ))
    }

    // Execute the plugin
    Log.debug("running plugin")
    plugin.run()

    return JobResult.success(*OutputFiles)
  }

  private fun validateStreamSpecs(plugin: AbstractPlugin<*, *>): Boolean {
    Log.debug("validating plugin StreamSpecs")

    var valid = true

    plugin.streamSpecs.forEach {
      if (ReservedFiles.isReservedFileName(it.streamName)) {
        Log.error("Plugin is attempting to download merge data into reserved file \"{}\"", it.streamName)
        valid = false
      }
    }

    return valid
  }
}