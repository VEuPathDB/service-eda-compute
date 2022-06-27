package org.veupathdb.service.eda.compute.plugins

interface PluginConfigValidator<C> {
  fun validate(request: C) // FIXME: return something
}