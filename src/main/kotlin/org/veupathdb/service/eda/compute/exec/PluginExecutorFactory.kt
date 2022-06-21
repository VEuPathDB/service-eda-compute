package org.veupathdb.service.eda.compute.exec

import org.veupathdb.lib.compute.platform.job.JobExecutor
import org.veupathdb.lib.compute.platform.job.JobExecutorContext
import org.veupathdb.lib.compute.platform.job.JobExecutorFactory

class PluginExecutorFactory : JobExecutorFactory {

  override fun newJobExecutor(ctx: JobExecutorContext): JobExecutor {
    return PluginExecutor()
  }

}