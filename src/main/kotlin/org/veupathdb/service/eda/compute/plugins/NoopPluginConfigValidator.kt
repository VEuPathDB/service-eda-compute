package org.veupathdb.service.eda.compute.plugins

import org.veupathdb.service.eda.generated.model.ComputeRequestBase

/**
 * Default implementation of [PluginConfigValidator] that performs no
 * validation.
 *
 * This should only be used as a fallback for plugins that do not require any
 * specific data validation on input configs.
 */
class NoopPluginConfigValidator : PluginConfigValidator<ComputeRequestBase> {
  override fun validate(request: ComputeRequestBase) {
    // This
  }
}