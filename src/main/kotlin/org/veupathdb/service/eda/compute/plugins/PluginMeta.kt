package org.veupathdb.service.eda.compute.plugins

import org.veupathdb.service.eda.generated.model.ComputeRequestBase

/**
 * Plugin Metadata
 *
 * Defines basic information about a plugin.
 *
 * @param R The type of the HTTP request model object designed for this plugin.
 *
 * @author Elizabeth Paige Harper - https://github.com/foxcapades
 * @since 1.0.0
 */
interface PluginMeta<R : ComputeRequestBase> {
  /**
   * URL Segment that identifies the specific plugin generated by this provider.
   */
  val urlSegment: String

  /**
   * Human friendly display name for the plugin generated by this provider.
   */
  val displayName: String

  /**
   * Optional human friendly description of the plugin generated by this
   * provider.
   */
  val description: String?
    get() = null

  /**
   * Queue that jobs for this plugin should be submitted to.
   */
  val targetQueue: PluginQueue

  /**
   * The class for the type of the HTTP request payload.
   *
   * This value is used to deserialize the request payload for use by the plugin
   * execution.
   */
  val requestClass: Class<out R>
}