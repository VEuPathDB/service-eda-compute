package org.veupathdb.service.eda.compute.plugins

import org.slf4j.LoggerFactory
import org.veupathdb.service.eda.generated.model.ComputeRequestBase

abstract class AbstractPlugin<R : ComputeRequestBase, C>(val context: PluginContext<R, C>) {

  private val Log = LoggerFactory.getLogger(javaClass)

  // ╔═════════════════════════════════════════════════════════════════════╗//
  // ║                                                                     ║//
  // ║  Abstract Extension Points                                          ║//
  // ║                                                                     ║//
  // ║  Methods and values which must be implemented by the extending      ║//
  // ║  plugin.                                                            ║//
  // ║                                                                     ║//
  // ╚═════════════════════════════════════════════════════════════════════╝//


  protected abstract fun execute(config: R)

  // ╔═════════════════════════════════════════════════════════════════════╗//
  // ║                                                                     ║//
  // ║  Final Public API                                                   ║//
  // ║                                                                     ║//
  // ║  Methods and values which must be implemented by the extending      ║//
  // ║  plugin.                                                            ║//
  // ║                                                                     ║//
  // ╚═════════════════════════════════════════════════════════════════════╝//

  final fun run(config: R) {
    Log.info("Executing plugin {}", javaClass.simpleName)

  }
}