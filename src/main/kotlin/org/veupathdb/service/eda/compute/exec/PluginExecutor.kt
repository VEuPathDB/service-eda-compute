package org.veupathdb.service.eda.compute.exec

import org.veupathdb.lib.compute.platform.job.JobContext
import org.veupathdb.lib.compute.platform.job.JobExecutor
import org.veupathdb.lib.compute.platform.job.JobResult
import org.veupathdb.lib.jackson.Json
import org.veupathdb.service.eda.compute.plugins.AbstractPlugin

class PluginExecutor : JobExecutor {

  override fun execute(ctx: JobContext): JobResult {
    val jobPayload     = Json.parse<PluginJobPayload>(ctx.config!!)
    val pluginClass    = Class.forName(jobPayload.pluginClass) as Class<AbstractPlugin<Any>>
    val pluginInstance = pluginClass.getConstructor().newInstance()
    val config         = pluginInstance.parseConfig(jobPayload.rawConfig)

    // TODO: setup workspace
    // TODO: get metadata
    // TODO: get tabular data

    pluginInstance.run(config)

    TODO("Not yet implemented")

  }

}