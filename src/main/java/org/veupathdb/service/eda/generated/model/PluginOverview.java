package org.veupathdb.service.eda.generated.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.List;

@JsonDeserialize(
    as = PluginOverviewImpl.class
)
public interface PluginOverview {
  @JsonProperty("name")
  String getName();

  @JsonProperty("name")
  void setName(String name);

  @JsonProperty("displayName")
  String getDisplayName();

  @JsonProperty("displayName")
  void setDisplayName(String displayName);

  @JsonProperty("description")
  String getDescription();

  @JsonProperty("description")
  void setDescription(String description);

  @JsonProperty("dataElementConstraints")
  List<DataElementConstraintPattern> getDataElementConstraints();

  @JsonProperty("dataElementConstraints")
  void setDataElementConstraints(List<DataElementConstraintPattern> dataElementConstraints);
}
