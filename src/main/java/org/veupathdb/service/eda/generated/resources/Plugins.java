package org.veupathdb.service.eda.generated.resources;

import java.util.List;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.GenericEntity;
import jakarta.ws.rs.core.Response;
import org.veupathdb.service.eda.generated.model.BadRequestError;
import org.veupathdb.service.eda.generated.model.ExamplePluginRequest;
import org.veupathdb.service.eda.generated.model.JobResponse;
import org.veupathdb.service.eda.generated.model.PluginOverview;
import org.veupathdb.service.eda.generated.model.ServerError;
import org.veupathdb.service.eda.generated.model.UnprocessableEntityError;
import org.veupathdb.service.eda.generated.support.ResponseDelegate;

@Path("/plugins")
public interface Plugins {
  @GET
  @Produces("application/json")
  GetPluginsResponse getPlugins();

  @POST
  @Path("/example")
  @Produces("application/json")
  @Consumes("application/json")
  PostPluginsExampleResponse postPluginsExample(ExamplePluginRequest entity);

  class GetPluginsResponse extends ResponseDelegate {
    private GetPluginsResponse(Response response, Object entity) {
      super(response, entity);
    }

    private GetPluginsResponse(Response response) {
      super(response);
    }

    public static GetPluginsResponse respond200WithApplicationJson(List<PluginOverview> entity) {
      Response.ResponseBuilder responseBuilder = Response.status(200).header("Content-Type", "application/json");
      GenericEntity<List<PluginOverview>> wrappedEntity = new GenericEntity<List<PluginOverview>>(entity){};
      responseBuilder.entity(wrappedEntity);
      return new GetPluginsResponse(responseBuilder.build(), wrappedEntity);
    }
  }

  class PostPluginsExampleResponse extends ResponseDelegate {
    private PostPluginsExampleResponse(Response response, Object entity) {
      super(response, entity);
    }

    private PostPluginsExampleResponse(Response response) {
      super(response);
    }

    public static PostPluginsExampleResponse respond200WithApplicationJson(JobResponse entity) {
      Response.ResponseBuilder responseBuilder = Response.status(200).header("Content-Type", "application/json");
      responseBuilder.entity(entity);
      return new PostPluginsExampleResponse(responseBuilder.build(), entity);
    }

    public static PostPluginsExampleResponse respond400WithApplicationJson(BadRequestError entity) {
      Response.ResponseBuilder responseBuilder = Response.status(400).header("Content-Type", "application/json");
      responseBuilder.entity(entity);
      return new PostPluginsExampleResponse(responseBuilder.build(), entity);
    }

    public static PostPluginsExampleResponse respond422WithApplicationJson(
        UnprocessableEntityError entity) {
      Response.ResponseBuilder responseBuilder = Response.status(422).header("Content-Type", "application/json");
      responseBuilder.entity(entity);
      return new PostPluginsExampleResponse(responseBuilder.build(), entity);
    }

    public static PostPluginsExampleResponse respond500WithApplicationJson(ServerError entity) {
      Response.ResponseBuilder responseBuilder = Response.status(500).header("Content-Type", "application/json");
      responseBuilder.entity(entity);
      return new PostPluginsExampleResponse(responseBuilder.build(), entity);
    }
  }
}
