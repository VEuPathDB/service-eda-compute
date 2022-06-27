package org.veupathdb.service.eda.generated.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum ComputeOutputType {
  @JsonProperty("meta")
  META("meta"),

  @JsonProperty("statistics")
  STATISTICS("statistics"),

  @JsonProperty("tabular")
  TABULAR("tabular");

  public final String name;

  ComputeOutputType(String name) {
    this.name = name;
  }


  public String getName() {
    return this.name;
  }
}
