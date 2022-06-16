package org.veupathdb.service.eda.compute.plugin

object PluginUtils {

  @JvmStatic
  fun listPlugins() {
    println(javaClass.classLoader.getDefinedPackage(javaClass.packageName))
  }
}