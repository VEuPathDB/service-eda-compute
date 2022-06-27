package org.veupathdb.service.eda.compute.plugins

import org.veupathdb.lib.compute.platform.job.JobWorkspace
import org.veupathdb.service.eda.compute.jobs.Const
import java.io.InputStream
import java.io.Reader

class PluginWorkspace(val jobWorkspace: JobWorkspace) : JobWorkspace by jobWorkspace {

  fun writeTabularResult(data: InputStream) {
    write(Const.OutputFileTabular, data)
  }

  fun writeTabularResult(data: Reader) {
    write(Const.OutputFileTabular, data)
  }

  fun writeTabularResult(data: String) {
    write(Const.OutputFileTabular, data)
  }

  fun writeMetaResult(data: InputStream) {
    write(Const.OutputFileMeta, data)
  }

  fun writeMetaResult(data: Reader)  {
    write(Const.OutputFileMeta, data)
  }

  fun writeMetaResult(data: String)  {
    write(Const.OutputFileMeta, data)
  }

  fun writeStatisticsResult(data: InputStream) {
    write(Const.OutputFileStats, data)
  }

  fun writeStatisticsResult(data: Reader) {
    write(Const.OutputFileStats, data)
  }

  fun writeStatisticsResult(data: String) {
    write(Const.OutputFileStats, data)
  }
}