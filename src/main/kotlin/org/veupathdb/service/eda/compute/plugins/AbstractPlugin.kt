package org.veupathdb.service.eda.compute.plugins

import org.apache.logging.log4j.LogManager
import org.veupathdb.service.eda.common.client.spec.StreamSpec
import org.veupathdb.service.eda.compute.jobs.Const
import org.veupathdb.service.eda.compute.metrics.PluginMetrics
import org.veupathdb.service.eda.generated.model.ComputeRequestBase

abstract class AbstractPlugin<R : ComputeRequestBase, C>(
  /**
   * Context in/for which this plugin is being executed.
   */
  protected val context: PluginContext<R, C>
) {

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
   * Returns a [StreamSpec] that will be used to fetch the tabular data for this
   * plugin.
   */
  abstract val streamSpec: StreamSpec

  /**
   * Executes this plugin's tasks.
   */
  protected abstract fun execute()

  // ╔═════════════════════════════════════════════════════════════════════╗//
  // ║                                                                     ║//
  // ║  Final Public API                                                   ║//
  // ║                                                                     ║//
  // ║  Methods and values which must be implemented by the extending      ║//
  // ║  plugin.                                                            ║//
  // ║                                                                     ║//
  // ╚═════════════════════════════════════════════════════════════════════╝//

  fun run() {
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
      e.printStackTrace(context.workspace.touch(Const.OutputFileException).toFile().printWriter())
    }
  }
}