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
  @JsonProperty("effectSizeLabel")
  String getEffectSizeLabel();

  @JsonProperty("effectSizeLabel")
  void setEffectSizeLabel(String effectSizeLabel);

  @JsonProperty("statistics")
  DifferentialAbundanceStats getStatistics();

  @JsonProperty("statistics")
  void setStatistics(DifferentialAbundanceStats statistics);

  @JsonProperty("pvalueFloor")
  String getPvalueFloor();

  @JsonProperty("pvalueFloor")
  void setPvalueFloor(String pvalueFloor);

  @JsonProperty("adjustedPValueFloor")
  String getAdjustedPValueFloor();

  @JsonProperty("adjustedPValueFloor")
  void setAdjustedPValueFloor(String adjustedPValueFloor);

  @JsonAnyGetter
  Map<String, Object> getAdditionalProperties();

  @JsonAnySetter
  void setAdditionalProperties(String key, Object value);
}
