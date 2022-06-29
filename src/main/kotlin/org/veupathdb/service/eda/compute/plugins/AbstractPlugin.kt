package org.veupathdb.service.eda.compute.plugins

import org.apache.logging.log4j.LogManager
import org.veupathdb.service.eda.common.client.spec.StreamSpec
import org.veupathdb.service.eda.compute.jobs.ReservedFiles
import org.veupathdb.service.eda.compute.metrics.PluginMetrics
import org.veupathdb.service.eda.generated.model.APIStudyDetail
import org.veupathdb.service.eda.generated.model.ComputeRequestBase

abstract class AbstractPlugin<R : ComputeRequestBase, C>(
  /**
   * Context in/for which this plugin is being executed.
   */
  protected val context: PluginContext<R, C>
) : Plugin {

  private val Log = LogManager.getLogger(javaClass)

  // ╔═════════════════════════════════════════════════════════════════════╗//
  // ║                                                                     ║//
  // ║  Abstract Extension Points                                          ║//
  // ║                                                                     ║//
  // ║  Methods and values which must be implemented by the extending      ║//
  // ║  plugin.                                                            ║//
  // ║                                                                     ║//
  // ╚═════════════════════════════════════════════════════════════════════╝//

  /**
   * Executes this plugin's tasks.
   *
   * Plugins can indicate an execution failure or bad status by throwing an
   * exception.  Any exception thrown by this method will result in the job
   * being marked as "failed".
   */
  protected abstract fun execute()

  // ╔═════════════════════════════════════════════════════════════════════╗//
  // ║                                                                     ║//
  // ║  Internal API                                                       ║//
  // ║                                                                     ║//
  // ║  Methods and values provided for convenience of implementation for  ║//
  // ║  extending plugins.                                                 ║//
  // ║                                                                     ║//
  // ╚═════════════════════════════════════════════════════════════════════╝//

  /**
   * Input configuration.
   */
  protected val config
    get() = context.config

  /**
   * Raw input request.
   */
  protected val rawRequest
    get() = context.request

  /**
   * Study details.
   */
  protected val studyDetail
    get() = context.studyDetail

  /**
   * Plugin workspace.
   */
  protected val workspace
    get() = context.workspace

  // ╔═════════════════════════════════════════════════════════════════════╗//
  // ║                                                                     ║//
  // ║  Final Public API                                                   ║//
  // ║                                                                     ║//
  // ║  Methods and values which must be implemented by the extending      ║//
  // ║  plugin.                                                            ║//
  // ║                                                                     ║//
  // ╚═════════════════════════════════════════════════════════════════════╝//

  override fun run() {
    Log.info("Executing plugin {}", { context.pluginMeta.urlSegment })

    try {
      // Start a timer to time the plugin execution.
      val tim = PluginMetrics.execTime
        .labels(context.pluginMeta.urlSegment)
        .startTimer()

      // execute the plugin
      execute()

      // Record the plugin execution time in the metrics
      tim.observeDuration()

      // Record the job success in the metrics
      PluginMetrics.successes.labels(context.pluginMeta.urlSegment).inc()
    } catch (e: Throwable) {
      Log.warn("Plugin execution failed", e)

      // Record the job failure in the metrics
      PluginMetrics.failures.labels(context.pluginMeta.urlSegment).inc()

      // Write the stacktrace to file to be persisted in S3
      e.printStackTrace(context.workspace.touch(ReservedFiles.OutputException).toFile().printWriter())
    }
  }
}