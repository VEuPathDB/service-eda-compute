package org.veupathdb.service.eda.generated.model;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.Map;

@JsonDeserialize(
    as = JobResponseImpl.class
)
public interface JobResponse {
  @JsonProperty("jobID")
  String getJobID();

  @JsonProperty("jobID")
  void setJobID(String jobID);

  @JsonProperty("status")
  JobStatus getStatus();

  @JsonProperty("status")
  void setStatus(JobStatus status);

  @JsonProperty("queuePosition")
  Integer getQueuePosition();

  @JsonProperty("queuePosition")
  void setQueuePosition(Integer queuePosition);

  @JsonAnyGetter
  Map<String, Object> getAdditionalProperties();

  @JsonAnySetter
  void setAdditionalProperties(String key, Object value);
}
