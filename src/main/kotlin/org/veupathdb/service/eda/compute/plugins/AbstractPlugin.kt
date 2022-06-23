package org.veupathdb.service.eda.compute.plugins

import org.apache.logging.log4j.LogManager
import org.veupathdb.service.eda.compute.jobs.Const
import org.veupathdb.service.eda.compute.metrics.PluginMetrics
import org.veupathdb.service.eda.generated.model.ComputeRequestBase

abstract class AbstractPlugin<R : ComputeRequestBase, C> {

  private val Log = LogManager.getLogger(javaClass)

  // ╔═════════════════════════════════════════════════════════════════════╗//
  // ║                                                                     ║//
  // ║  Abstract Extension Points                                          ║//
  // ║                                                                     ║//
  // ║  Methods and values which must be implemented by the extending      ║//
  // ║  plugin.                                                            ║//
  // ║                                                                     ║//
  // ╚═════════════════════════════════════════════════════════════════════╝//

  protected abstract fun execute(context: PluginContext<R, C>)


  // ╔═════════════════════════════════════════════════════════════════════╗//
  // ║                                                                     ║//
  // ║  Final Public API                                                   ║//
  // ║                                                                     ║//
  // ║  Methods and values which must be implemented by the extending      ║//
  // ║  plugin.                                                            ║//
  // ║                                                                     ║//
  // ╚═════════════════════════════════════════════════════════════════════╝//

  fun run(context: PluginContext<R, C>) {
    Log.info("Executing plugin {}", { context.pluginMeta.urlSegment })

    try {
      // Start a timer to time the plugin execution.
      val tim = PluginMetrics.execTime
        .labels(context.pluginMeta.urlSegment)
        .startTimer()

      // execute the plugin
      execute(context)

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