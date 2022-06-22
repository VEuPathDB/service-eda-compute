package org.veupathdb.service.eda.compute.plugins

import org.veupathdb.lib.compute.platform.job.JobWorkspace
import org.veupathdb.lib.jackson.Json
import org.veupathdb.service.eda.common.model.ReferenceMetadata
import org.veupathdb.service.eda.compute.exec.ComputeJobContext
import org.veupathdb.service.eda.compute.jobs.Const
import org.veupathdb.service.eda.compute.process.ComputeProcessBuilder
import org.veupathdb.service.eda.generated.model.APIStudyDetail
import org.veupathdb.service.eda.generated.model.ComputeRequestBase
import java.io.InputStream

class PluginContext<R: ComputeRequestBase, C>(
  /**
   * Original HTTP request that was sent to the compute service to kick off the
   * current job.
   */
  val request: R,

  /**
   * A handle on the workspace the plugin is being executed in.
   */
  val workspace: JobWorkspace,

  val jobContext: ComputeJobContext
) {

  /**
   * Job configuration.
   *
   * Alias for `request.config` or `getRequest().getConfig()`
   */
  @Suppress("UNCHECKED_CAST")
  val config: C
    get() = request.config as C

  /**
   * API Study Details
   *
   * Loaded lazily on request.
   */
  val studyDetail: APIStudyDetail by lazy {
    Json.parse(workspace.openStream(Const.InputFileMeta).buffered())
  }

  /**
   * Reference Metadata
   *
   * Loaded lazily on request.
   */
  val referenceMetadata: ReferenceMetadata by lazy {
    ReferenceMetadata(studyDetail, request.derivedVariables)
  }

  /**
   * Opens a new stream over the tabular data retrieved from the EDA Merge
   * Service.
   *
   * The returned stream is not buffered.
   */
  fun getTabularDataStream(): InputStream {
    return workspace.openStream(Const.InputFileTabular)
  }

  // TODO: how will the plugin execute arbitrary processes?
  fun executeProcess(command: String): ComputeProcessBuilder

  // TODO: this should be loaded ahead of time and put into the local job
  //       scratch space
  val apiStudyDetail: APIStudyDetail


}