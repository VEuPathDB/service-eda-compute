package org.veupathdb.service.eda.generated.model;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import java.util.List;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "studyId",
    "config",
    "derivedVariables"
})
public class ComputeRequestBaseImpl implements ComputeRequestBase {
  @JsonProperty("studyId")
  private String studyId;

  @JsonProperty("config")
  private Object config;

  @JsonProperty("derivedVariables")
  private List<DerivedVariable> derivedVariables;

  @JsonIgnore
  private Map<String, java.lang.Object> additionalProperties = new ExcludingMap();

  @JsonProperty("studyId")
  public String getStudyId() {
    return this.studyId;
  }

  @JsonProperty("studyId")
  public void setStudyId(String studyId) {
    this.studyId = studyId;
  }

  @JsonProperty("config")
  public Object getConfig() {
    return this.config;
  }

  @JsonProperty("config")
  public void setConfig(Object config) {
    this.config = config;
  }

  @JsonProperty("derivedVariables")
  public List<DerivedVariable> getDerivedVariables() {
    return this.derivedVariables;
  }

  @JsonProperty("derivedVariables")
  public void setDerivedVariables(List<DerivedVariable> derivedVariables) {
    this.derivedVariables = derivedVariables;
  }

  @JsonAnyGetter
  public Map<String, java.lang.Object> getAdditionalProperties() {
    return additionalProperties;
  }

  @JsonAnySetter
  public void setAdditionalProperties(String key, java.lang.Object value) {
    this.additionalProperties.put(key, value);
  }
}
