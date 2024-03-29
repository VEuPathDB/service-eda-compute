package org.veupathdb.service.eda.generated.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "name",
    "displayName",
    "description",
    "dataElementConstraints"
})
public class PluginOverviewImpl implements PluginOverview {
  @JsonProperty("name")
  private String name;

  @JsonProperty("displayName")
  private String displayName;

  @JsonProperty("description")
  private String description;

  @JsonProperty("dataElementConstraints")
  private List<DataElementConstraintPattern> dataElementConstraints;

  @JsonProperty("name")
  public String getName() {
    return this.name;
  }

  @JsonProperty("name")
  public void setName(String name) {
    this.name = name;
  }

  @JsonProperty("displayName")
  public String getDisplayName() {
    return this.displayName;
  }

  @JsonProperty("displayName")
  public void setDisplayName(String displayName) {
    this.displayName = displayName;
  }

  @JsonProperty("description")
  public String getDescription() {
    return this.description;
  }

  @JsonProperty("description")
  public void setDescription(String description) {
    this.description = description;
  }

  @JsonProperty("dataElementConstraints")
  public List<DataElementConstraintPattern> getDataElementConstraints() {
    return this.dataElementConstraints;
  }

  @JsonProperty("dataElementConstraints")
  public void setDataElementConstraints(List<DataElementConstraintPattern> dataElementConstraints) {
    this.dataElementConstraints = dataElementConstraints;
  }
}
