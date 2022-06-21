package org.veupathdb.service.eda.generated.model;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "jobID",
    "status",
    "queuePosition"
})
public class JobResponseImpl implements JobResponse {
  @JsonProperty("jobID")
  private String jobID;

  @JsonProperty("status")
  private JobStatus status;

  @JsonProperty("queuePosition")
  private Integer queuePosition;

  @JsonIgnore
  private Map<String, Object> additionalProperties = new ExcludingMap();

  @JsonProperty("jobID")
  public String getJobID() {
    return this.jobID;
  }

  @JsonProperty("jobID")
  public void setJobID(String jobID) {
    this.jobID = jobID;
  }

  @JsonProperty("status")
  public JobStatus getStatus() {
    return this.status;
  }

  @JsonProperty("status")
  public void setStatus(JobStatus status) {
    this.status = status;
  }

  @JsonProperty("queuePosition")
  public Integer getQueuePosition() {
    return this.queuePosition;
  }

  @JsonProperty("queuePosition")
  public void setQueuePosition(Integer queuePosition) {
    this.queuePosition = queuePosition;
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
