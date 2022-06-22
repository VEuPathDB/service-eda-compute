package org.veupathdb.service.eda.compute.exec

import org.veupathdb.service.eda.compute.plugins.PluginProvider
import org.veupathdb.service.eda.generated.model.ComputeRequestBase
import org.veupathdb.service.eda.generated.model.JobResponse

object EDACompute {
  @JvmStatic
  fun <R : ComputeRequestBase, C> submitComputeJob(
    plugin: PluginProvider<R, C>,
    payload: R,
    auth: Map.Entry<String, String>
  ): JobResponse {}
}