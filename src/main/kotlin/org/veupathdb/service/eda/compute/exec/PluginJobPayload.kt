package org.veupathdb.service.eda.compute.exec

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.JsonNode
import org.gusdb.fgputil.Tuples.TwoTuple

internal data class PluginJobPayload(
  /**
   * Name of the plugin that this job should be executed by.
   */
  @JsonProperty("plugin")
  val plugin: String,

  /**
   * Serialized HTTP request that triggered this job.
   */
  @JsonProperty("request")
  val request: JsonNode,

  /**
   * Auth Header passed in with the HTTP request.
   */
  @JsonProperty("authHeader")
  val authHeader: TwoTuple<String, String>
)