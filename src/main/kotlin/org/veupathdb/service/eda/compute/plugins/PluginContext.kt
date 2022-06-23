package org.veupathdb.service.eda.compute.plugins

import org.veupathdb.lib.compute.platform.job.JobWorkspace
import org.veupathdb.lib.jackson.Json
import org.veupathdb.service.eda.common.model.ReferenceMetadata
import org.veupathdb.service.eda.compute.exec.ComputeJobContext
import org.veupathdb.service.eda.compute.jobs.Const
import org.veupathdb.service.eda.compute.process.ComputeProcessBuilder
import org.veupathdb.service.eda.generated.model.APIStudyDetail
import org.veupathdb.service.eda.generated.model.ComputeRequestBase
import java.io.InputStream

/**
 * Plugin Execution Context
 *
 * Provides the context for a plugin execution.
 *
 * The execution context includes:
 *
 * * The original HTTP request that trigger the job to be queued.
 * * A handle on the local scratch workspace where files can be read/written.
 * * Additional context from the compute service about the current job.
 * * Accessors for:
 *     * The API study details
 *     * The Tabular merge data for the job
 *
 * @author Elizabeth Paige Harper [https://github.com/foxcapades]
 * @since 1.0.0
 */
interface PluginContext<R: ComputeRequestBase, C> {

  /**
   * Meta information about the current plugin.
   */
  val pluginMeta: PluginMeta<R>

  /**
   * Original HTTP request that was sent to the compute service to kick off the
   * current job.
   */
  val request: R

  /**
   * Job configuration.
   *
   * Alias for `request.config` or `getRequest().getConfig()`
   */
  @Suppress("UNCHECKED_CAST")
  val config: C
    get() = request.config as C

  /**
   * A handle on the workspace the plugin is being executed in.
   */
  val workspace: JobWorkspace

  /**
   * Additional compute service context for the job being executed.
   */
  val jobContext: ComputeJobContext

  /**
   * API Study Details
   */
  val studyDetail: APIStudyDetail

  /**
   * Reference Metadata
   *
   * Loaded lazily on request.
   */
  val referenceMetadata: ReferenceMetadata

  /**
   * Opens a new stream over the tabular data retrieved from the EDA Merge
   * Service.
   *
   * The returned stream is not buffered.
   */
  fun getTabularDataStream(): InputStream

  // TODO: how will the plugin execute arbitrary processes?
  fun executeProcess(command: String): ComputeProcessBuilder
}


/**
 * Plugin Execution Context Builder
 *
 * Builder for constructing a new [PluginContext] instance.
 *
 * This type is mostly here to work around the fact that the generic types are
 * completely unknown to the plugin executor and this allows the safe
 * construction of a [PluginContext] instance without needing to rely on
 * unsafe casts.
 *
 * @author Elizabeth Paige Harper
 * @since 1.0.0
 */
class PluginContextBuilder<R : ComputeRequestBase, C> {
  var request: R? = null

  var workspace: JobWorkspace? = null

  var jobContext: ComputeJobContext? = null

  var pluginMeta: PluginMeta<R>? = null

  fun request(request: R): PluginContextBuilder<R, C> {
    this.request = request
    return this
  }

  fun workspace(workspace: JobWorkspace): PluginContextBuilder<R, C> {
    this.workspace = workspace
    return this
  }

  fun jobContext(jobContext: ComputeJobContext): PluginContextBuilder<R, C> {
    this.jobContext = jobContext
    return this
  }

  fun pluginMeta(pluginMeta: PluginMeta<R>): PluginContextBuilder<R, C> {
    this.pluginMeta = pluginMeta
    return this
  }

  fun build(): PluginContext<R, C> =
    PluginContextImpl(
      request ?: throw IllegalStateException("request must not be null"),
      workspace ?: throw IllegalStateException("workspace must not be null"),
      jobContext ?: throw IllegalStateException("jobContext must not be null"),
      pluginMeta ?: throw IllegalStateException("pluginMeta must not be null")
    )
}


private class PluginContextImpl<R : ComputeRequestBase, C>(
  override val request: R,
  override val workspace: JobWorkspace,
  override val jobContext: ComputeJobContext,
  override val pluginMeta: PluginMeta<R>,
) : PluginContext<R, C> {

  override val studyDetail: APIStudyDetail by lazy {
    Json.parse(workspace.openStream(Const.InputFileMeta).buffered())
  }

  override val referenceMetadata: ReferenceMetadata by lazy {
    ReferenceMetadata(studyDetail, request.derivedVariables)
  }

  override fun getTabularDataStream(): InputStream {
    return workspace.openStream(Const.InputFileTabular)
  }

  override fun executeProcess(command: String): ComputeProcessBuilder {
    TODO("Not yet implemented")
  }
}