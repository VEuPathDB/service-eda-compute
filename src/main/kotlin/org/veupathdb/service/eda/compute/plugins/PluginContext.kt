package org.veupathdb.service.eda.compute.plugins

import org.veupathdb.service.eda.generated.model.APIStudyDetail

interface PluginContext {

  // TODO: should this be a separate type or should the workspace methods be part of the context class?
  val workspace: Any

  // TODO: how will the plugin execute arbitrary processes?
  fun executeProcess(): ProcessBuilder

  // TODO: this should be loaded ahead of time and put into the local job
  //       scratch space
  val apiStudyDetail: APIStudyDetail


}