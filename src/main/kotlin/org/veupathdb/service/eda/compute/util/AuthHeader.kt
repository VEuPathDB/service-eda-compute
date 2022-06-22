package org.veupathdb.service.eda.compute.util

import com.fasterxml.jackson.annotation.JsonProperty

@Deprecated(message = "Stand in class until jaxrs-core gets updated to v7")
internal data class AuthHeader(
  @JsonProperty("header")
  val header: String,
  @JsonProperty("secret")
  val secret: String,
) {
  constructor(entry: Map.Entry<String, String>) : this(entry.key, entry.value)

  fun asMapEntry(): Map.Entry<String, String> = Entry(header, secret)
}

private class Entry(override val key: String, override val value: String) : Map.Entry<String, String>
