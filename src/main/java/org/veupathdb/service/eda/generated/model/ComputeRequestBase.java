package org.veupathdb.service.eda.generated.model;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.List;
import java.util.Map;

@JsonDeserialize(
    as = ComputeRequestBaseImpl.class
)
public interface ComputeRequestBase {
  @JsonProperty("studyId")
  String getStudyId();

  @JsonProperty("studyId")
  void setStudyId(String studyId);

  @JsonProperty("config")
  Object getConfig();

  @JsonProperty("config")
  void setConfig(Object config);

  @JsonProperty("derivedVariables")
  List<DerivedVariable> getDerivedVariables();

  @JsonProperty("derivedVariables")
  void setDerivedVariables(List<DerivedVariable> derivedVariables);

  @JsonAnyGetter
  Map<String, java.lang.Object> getAdditionalProperties();

  @JsonAnySetter
  void setAdditionalProperties(String key, java.lang.Object value);
}
