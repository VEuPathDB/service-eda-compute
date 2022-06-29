package org.veupathdb.service.eda.compute.plugins

import org.veupathdb.lib.compute.platform.job.JobWorkspace
import org.veupathdb.service.eda.compute.jobs.ReservedFiles
import java.io.InputStream
import java.io.Reader

class PluginWorkspace(val internalWorkspace: JobWorkspace) : JobWorkspace by internalWorkspace {

  fun writeTabularResult(data: InputStream) {
    write(ReservedFiles.OutputTabular, data)
  }

  fun writeTabularResult(data: Reader) {
    write(ReservedFiles.OutputTabular, data)
  }

  fun writeTabularResult(data: String) {
    write(ReservedFiles.OutputTabular, data)
  }

  fun writeMetaResult(data: InputStream) {
    write(ReservedFiles.OutputMeta, data)
  }

  fun writeMetaResult(data: Reader)  {
    write(ReservedFiles.OutputMeta, data)
  }

  fun writeMetaResult(data: String)  {
    write(ReservedFiles.OutputMeta, data)
  }

  fun writeStatisticsResult(data: InputStream) {
    write(ReservedFiles.OutputStats, data)
  }

  fun writeStatisticsResult(data: Reader) {
    write(ReservedFiles.OutputStats, data)
  }

  fun writeStatisticsResult(data: String) {
    write(ReservedFiles.OutputStats, data)
  }
}