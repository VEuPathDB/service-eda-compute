package org.veupathdb.service.eda.compute.plugins

import org.veupathdb.service.eda.generated.model.ComputeRequestBase

interface PluginConfigValidator<C> {
  fun validate(request: C) // FIXME: return something
}