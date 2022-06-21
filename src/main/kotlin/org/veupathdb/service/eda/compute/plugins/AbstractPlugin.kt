package org.veupathdb.service.eda.compute.plugins

import com.fasterxml.jackson.databind.JsonNode
import org.slf4j.LoggerFactory
import org.veupathdb.service.eda.common.model.ReferenceMetadata
import org.veupathdb.service.eda.generated.model.APIStudyDetail
import org.veupathdb.service.eda.generated.model.ComputeRequestBase
import org.veupathdb.service.eda.generated.model.DerivedVariable
import java.util.Collections


abstract class AbstractPlugin<R : ComputeRequestBase, C> {

  private val Log = LoggerFactory.getLogger(javaClass)

  fun getStudyDetail(config: R): APIStudyDetail {
    TODO("Should this be here?  Should this be provided some other way?")
  }


  /**
   * TODO: Maybe this should be part of the context?
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
  fun getReferenceMetadata(config: R): ReferenceMetadata {
    TODO("")
  }


  // ╔═════════════════════════════════════════════════════════════════════╗//
  // ║                                                                     ║//
  // ║  Optional Extension Points                                          ║//
  // ║                                                                     ║//
  // ║  Methods and values which may optionally be overridden by the       ║//
  // ║  implementing plugin.                                               ║//
  // ║                                                                     ║//
  // ╚═════════════════════════════════════════════════════════════════════╝//

  @Suppress("UNCHECKED_CAST")
  open fun getConfig(request: R): C {
    return request.config as C
  }

  /**
   * Display name for this plugin.
   *
   * Defaults to [Class.getSimpleName].
   */
  open val displayName = javaClass.simpleName

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
  open fun getDerivedVariables(config: R): List<DerivedVariable> {
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


  protected abstract fun execute(config: R)

  abstract fun parseConfig(configJson: JsonNode): R


  // ╔═════════════════════════════════════════════════════════════════════╗//
  // ║                                                                     ║//
  // ║  Final Public API                                                   ║//
  // ║                                                                     ║//
  // ║  Methods and values which must be implemented by the extending      ║//
  // ║  plugin.                                                            ║//
  // ║                                                                     ║//
  // ╚═════════════════════════════════════════════════════════════════════╝//

  final fun run(config: R) {
    Log.info("Executing plugin {}", displayName)

  }
}