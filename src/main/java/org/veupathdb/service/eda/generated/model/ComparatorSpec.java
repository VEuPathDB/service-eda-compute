package org.veupathdb.service.eda.generated.model;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.List;
import java.util.Map;

@JsonDeserialize(
    as = ComparatorSpecImpl.class
)
public interface ComparatorSpec {
  @JsonProperty("variable")
  VariableSpec getVariable();

  @JsonProperty("variable")
  void setVariable(VariableSpec variable);

  @JsonProperty("groupA")
  List<LabeledRange> getGroupA();

  @JsonProperty("groupA")
  void setGroupA(List<LabeledRange> groupA);

  @JsonProperty("groupB")
  List<LabeledRange> getGroupB();

  @JsonProperty("groupB")
  void setGroupB(List<LabeledRange> groupB);

  @JsonAnyGetter
  Map<String, Object> getAdditionalProperties();

  @JsonAnySetter
  void setAdditionalProperties(String key, Object value);
}
