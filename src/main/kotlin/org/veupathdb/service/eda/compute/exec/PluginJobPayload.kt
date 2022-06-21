package org.veupathdb.service.eda.compute.exec

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.JsonNode

data class PluginJobPayload(
  @JsonProperty("plugin")
  val pluginClass: String,

  @JsonProperty("config")
  val rawConfig: JsonNode
)