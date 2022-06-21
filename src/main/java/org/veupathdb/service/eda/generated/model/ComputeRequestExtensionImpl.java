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
public class ComputeRequestExtensionImpl implements ComputeRequestExtension {
  @JsonProperty("studyId")
  private String studyId;

  @JsonProperty("config")
  private ExampleComputeConfig config;

  @JsonProperty("derivedVariables")
  private List<DerivedVariable> derivedVariables;

  @JsonIgnore
  private Map<String, Object> additionalProperties = new ExcludingMap();

  @JsonProperty("studyId")
  public String getStudyId() {
    return this.studyId;
  }

  @JsonProperty("studyId")
  public void setStudyId(String studyId) {
    this.studyId = studyId;
  }

  @JsonProperty("config")
  public ExampleComputeConfig getConfig() {
    return this.config;
  }

  @JsonProperty("config")
  public void setConfig(ExampleComputeConfig config) {
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
  public Map<String, Object> getAdditionalProperties() {
    return additionalProperties;
  }

  @JsonAnySetter
  public void setAdditionalProperties(String key, Object value) {
    this.additionalProperties.put(key, value);
  }
}
