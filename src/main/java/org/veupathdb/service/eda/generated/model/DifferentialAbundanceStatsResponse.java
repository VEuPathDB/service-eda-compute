package org.veupathdb.service.eda.generated.model;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.Map;

@JsonDeserialize(
    as = DifferentialAbundanceStatsResponseImpl.class
)
public interface DifferentialAbundanceStatsResponse {
  @JsonProperty("someStats")
  Object getSomeStats();

  @JsonProperty("someStats")
  void setSomeStats(Object someStats);

  @JsonAnyGetter
  Map<String, java.lang.Object> getAdditionalProperties();

  @JsonAnySetter
  void setAdditionalProperties(String key, java.lang.Object value);
}
