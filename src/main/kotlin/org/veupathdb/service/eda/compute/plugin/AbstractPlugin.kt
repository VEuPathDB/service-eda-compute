package org.veupathdb.service.eda.compute.plugin

import com.fasterxml.jackson.databind.JsonNode
import org.slf4j.LoggerFactory
import org.veupathdb.service.eda.common.model.ReferenceMetadata
import org.veupathdb.service.eda.generated.model.DerivedVariable
import java.util.Collections


abstract class AbstractPlugin<T> {

  private val Log = LoggerFactory.getLogger(javaClass)


  /**
   * TODO: is this passed in or lazily loaded on call?
   *       If it's lazily loaded we will need a handle on the derived variables
   *       at least...
   *       ...
   *       At some point we need to hit the metadata service with the plugin
   *       spec to get the [APIStudyDetail] data, then using that along with
   *       the derived variables we can construct a [ReferenceMetadata]
   *       instance.  The question is, how do we get the DerivedVariables here?
   *       ...
   *       They will be included in the original request, so perhaps they should
   *       be stored in Postgres or something or something?  The plugin configs
   *       are opaque to us so we can't just pop out the values ourselves...
   *       ...
   *       Maybe there should be a method on this class that gets them from the
   *       config?
   */
  fun getReferenceMetadata(): ReferenceMetadata {
    TODO()
  }


  // ╔═════════════════════════════════════════════════════════════════════╗//
  // ║                                                                     ║//
  // ║  Optional Extension Points                                          ║//
  // ║                                                                     ║//
  // ║  Methods and values which may optionally be overridden by the       ║//
  // ║  implementing plugin.                                               ║//
  // ║                                                                     ║//
  // ╚═════════════════════════════════════════════════════════════════════╝//


  /**
   * Display name for this plugin.
   *
   * Defaults to [Class.getSimpleName].
   */
  open val displayName = javaClass.simpleName

  /**
   * Optional description of this plugin.
   */
  open val description = ""

  /**
   * Parses any [DerivedVariable]s out of the given input configuration.
   *
   * If there are no derived variables, return an empty list via either Kotlin's
   * [emptyList] method or Java's [Collections.emptyList] method.
   *
   * @param config Plugin configuration.
   *
   * @return A list of derived variables.
   */
  open fun getDerivedVariables(config: T): List<DerivedVariable> {
    return emptyList()
  }


  // ╔═════════════════════════════════════════════════════════════════════╗//
  // ║                                                                     ║//
  // ║  Abstract Extension Points                                          ║//
  // ║                                                                     ║//
  // ║  Methods and values which must be implemented by the extending      ║//
  // ║  plugin.                                                            ║//
  // ║                                                                     ║//
  // ╚═════════════════════════════════════════════════════════════════════╝//


  protected abstract fun execute(config: T)

  abstract fun parseConfig(configJson: JsonNode): T


  // ╔═════════════════════════════════════════════════════════════════════╗//
  // ║                                                                     ║//
  // ║  Final Public API                                                   ║//
  // ║                                                                     ║//
  // ║  Methods and values which must be implemented by the extending      ║//
  // ║  plugin.                                                            ║//
  // ║                                                                     ║//
  // ╚═════════════════════════════════════════════════════════════════════╝//

  final fun run(config: T) {
    Log.info("Executing plugin {}", displayName)
  }
}