package org.veupathdb.service.eda.compute.plugins

/**
 * Default implementation of [PluginConfigValidator] that performs no
 * validation.
 *
 * This should only be used as a fallback for plugins that do not require any
 * specific data validation on input configs.
 */
class NoopPluginConfigValidator : PluginConfigValidator<Any> {

  override fun validate(request: Any) {
    // This method does nothing.
  }
}