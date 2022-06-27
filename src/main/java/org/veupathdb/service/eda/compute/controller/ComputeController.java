package org.veupathdb.service.eda.compute.controller;

import jakarta.ws.rs.ForbiddenException;
import jakarta.ws.rs.core.Context;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.glassfish.jersey.server.ContainerRequest;
import org.veupathdb.lib.container.jaxrs.providers.UserProvider;
import org.veupathdb.service.eda.compute.EDA;
import org.veupathdb.service.eda.compute.plugins.PluginProvider;
import org.veupathdb.service.eda.compute.plugins.PluginRegistry;
import org.veupathdb.service.eda.compute.plugins.example.ExamplePluginProvider;
import org.veupathdb.service.eda.generated.model.ComputeOutputType;
import org.veupathdb.service.eda.generated.model.ComputeRequestBase;
import org.veupathdb.service.eda.generated.model.ExamplePluginRequest;
import org.veupathdb.service.eda.generated.model.JobResponse;
import org.veupathdb.service.eda.generated.resources.Computes;

public class ComputeController implements Computes {

  private final Logger Log = LogManager.getLogger(getClass());

  @Context
  private ContainerRequest request;

  // ╔════════════════════════════════════════════════════════════════════╗ //
  // ║  Plugin Endpoints                                                  ║ //
  // ║                                                                    ║ //
  // ║  Controller methods for handling requests to run specific          ║ //
  // ║  plugins.                                                          ║ //
  // ╚════════════════════════════════════════════════════════════════════╝ //


  @Override
  public PostComputesExampleResponse postComputesExample(ExamplePluginRequest entity) {
    return PostComputesExampleResponse.respond200WithApplicationJson(submitJob(new ExamplePluginProvider(), entity));
  }

  // ╔════════════════════════════════════════════════════════════════════╗ //
  // ║  Static Endpoints                                                  ║ //
  // ╚════════════════════════════════════════════════════════════════════╝ //

  @Override
  public GetComputesResponse getComputes() {
    return GetComputesResponse.respond200WithApplicationJson(PluginRegistry.getPluginOverview());
  }

  @Override
  public PostComputesByPluginAndFileResponse postComputesByPluginAndFile(
    String plugin,
    ComputeOutputType file,
    Object entity
  ) {
    return null;
  }

  // ╔════════════════════════════════════════════════════════════════════╗ //
  // ║  Helper Methods                                                    ║ //
  // ╚════════════════════════════════════════════════════════════════════╝ //

  private <R extends ComputeRequestBase, C> JobResponse submitJob(PluginProvider<R, C> plugin, R entity) {
    var auth = UserProvider.getSubmittedAuth(request).orElseThrow();

    // Check that the user has permission to run compute jobs.
    if (!EDA.getStudyPerms(entity.getStudyId(), auth).allowVisualizations())
      throw new ForbiddenException();

    //
    plugin.getValidator()
      .validate(entity);

    return EDA.submitComputeJob(plugin, entity, auth);
  }
}
