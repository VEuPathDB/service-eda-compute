package org.veupathdb.service.eda.compute.jobs

import com.fasterxml.jackson.annotation.JsonValue
import org.veupathdb.lib.hash_id.HashID
import org.veupathdb.lib.jackson.Json
import org.veupathdb.service.eda.generated.model.ComputeRequestBase

data class JobIDHashContainer(
  val pluginName: String,
  val payload:    ComputeRequestBase
) {
  @JsonValue
  fun toJson() =
    Json.newObject {
      put("pluginName", pluginName)
      put("payload", Json.convert(payload))
    }

  fun toJobID() =
    HashID.ofMD5(toJson().toPrettyString())
}
