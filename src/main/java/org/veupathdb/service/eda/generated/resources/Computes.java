package org.veupathdb.service.eda.generated.resources;

import java.util.List;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.GenericEntity;
import jakarta.ws.rs.core.Response;
import org.veupathdb.service.eda.generated.model.BadRequestError;
import org.veupathdb.service.eda.generated.model.ComputeOutputType;
import org.veupathdb.service.eda.generated.model.ComputeRequestBase;
import org.veupathdb.service.eda.generated.model.ExamplePluginRequest;
import org.veupathdb.service.eda.generated.model.JobResponse;
import org.veupathdb.service.eda.generated.model.PluginOverview;
import org.veupathdb.service.eda.generated.model.ServerError;
import org.veupathdb.service.eda.generated.model.UnprocessableEntityError;
import org.veupathdb.service.eda.generated.support.ResponseDelegate;

@Path("/computes")
public interface Computes {
  @GET
  @Produces("application/json")
  GetComputesResponse getComputes();

  @POST
  @Path("/{plugin}/{file}")
  @Produces("*/*")
  @Consumes("application/json")
  PostComputesByPluginAndFileResponse postComputesByPluginAndFile(
      @PathParam("plugin") String plugin, @PathParam("file") ComputeOutputType file,
      ComputeRequestBase entity);

  @POST
  @Path("/example")
  @Produces("application/json")
  @Consumes("application/json")
  PostComputesExampleResponse postComputesExample(ExamplePluginRequest entity);

  class GetComputesResponse extends ResponseDelegate {
    private GetComputesResponse(Response response, Object entity) {
      super(response, entity);
    }

    private GetComputesResponse(Response response) {
      super(response);
    }

    public static GetComputesResponse respond200WithApplicationJson(List<PluginOverview> entity) {
      Response.ResponseBuilder responseBuilder = Response.status(200).header("Content-Type", "application/json");
      GenericEntity<List<PluginOverview>> wrappedEntity = new GenericEntity<List<PluginOverview>>(entity){};
      responseBuilder.entity(wrappedEntity);
      return new GetComputesResponse(responseBuilder.build(), wrappedEntity);
    }
  }

  class PostComputesByPluginAndFileResponse extends ResponseDelegate {
    private PostComputesByPluginAndFileResponse(Response response, Object entity) {
      super(response, entity);
    }

    private PostComputesByPluginAndFileResponse(Response response) {
      super(response);
    }

    public static PostComputesByPluginAndFileResponse respond200With(Object entity) {
      Response.ResponseBuilder responseBuilder = Response.status(200).header("Content-Type", "*/*");
      responseBuilder.entity(entity);
      return new PostComputesByPluginAndFileResponse(responseBuilder.build(), entity);
    }
  }

  class PostComputesExampleResponse extends ResponseDelegate {
    private PostComputesExampleResponse(Response response, Object entity) {
      super(response, entity);
    }

    private PostComputesExampleResponse(Response response) {
      super(response);
    }

    public static PostComputesExampleResponse respond200WithApplicationJson(JobResponse entity) {
      Response.ResponseBuilder responseBuilder = Response.status(200).header("Content-Type", "application/json");
      responseBuilder.entity(entity);
      return new PostComputesExampleResponse(responseBuilder.build(), entity);
    }

    public static PostComputesExampleResponse respond400WithApplicationJson(
        BadRequestError entity) {
      Response.ResponseBuilder responseBuilder = Response.status(400).header("Content-Type", "application/json");
      responseBuilder.entity(entity);
      return new PostComputesExampleResponse(responseBuilder.build(), entity);
    }

    public static PostComputesExampleResponse respond422WithApplicationJson(
        UnprocessableEntityError entity) {
      Response.ResponseBuilder responseBuilder = Response.status(422).header("Content-Type", "application/json");
      responseBuilder.entity(entity);
      return new PostComputesExampleResponse(responseBuilder.build(), entity);
    }

    public static PostComputesExampleResponse respond500WithApplicationJson(ServerError entity) {
      Response.ResponseBuilder responseBuilder = Response.status(500).header("Content-Type", "application/json");
      responseBuilder.entity(entity);
      return new PostComputesExampleResponse(responseBuilder.build(), entity);
    }
  }
}
