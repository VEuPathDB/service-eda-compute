package org.veupathdb.service.eda.compute.plugins

import org.veupathdb.service.eda.generated.model.ComputeRequestBase

/**
 * Plugin Provider.
 *
 * Helper class that describes and assists in instantiating an instance of an
 * [AbstractPlugin] implementation.
 *
 * @param R HTTP request payload type.
 *
 * @param C Plugin configuration type.
 */
interface PluginProvider<R : ComputeRequestBase, C> : PluginMeta<R> {

  /**
   * Creates a new instance of this plugin
   */
  fun createPlugin(): AbstractPlugin<R, C>

  @Suppress("UNCHECKED_CAST")
  fun createValidator(): PluginConfigValidator<R> =
    NoopPluginConfigValidator() as PluginConfigValidator<R>

  fun buildContext(): PluginContextBuilder<R, C> =
    PluginContextBuilder()
}