package org.veupathdb.service.eda.compute.exec

import org.veupathdb.service.eda.compute.plugins.AbstractPlugin

object EDACompute {
  @JvmStatic
  fun submitComputeJob(plugin: AbstractPlugin<*, C>, config: C)
}