package org.veupathdb.service.eda.compute.controller;

import jakarta.ws.rs.ForbiddenException;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.StreamingOutput;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.glassfish.jersey.server.ContainerRequest;
import org.gusdb.fgputil.Tuples;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.veupathdb.lib.container.jaxrs.providers.UserProvider;
import org.veupathdb.service.eda.compute.EDA;
import org.veupathdb.service.eda.compute.jobs.ReservedFiles;
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
  // ║  Constant Endpoints                                                ║ //
  // ║                                                                    ║ //
  // ║  Endpoints that must exist regardless of what plugins are added    ║ //
  // ║  or removed.                                                       ║ //
  // ╚════════════════════════════════════════════════════════════════════╝ //

  @Override
  public GetComputesResponse getComputes() {
    return GetComputesResponse.respond200WithApplicationJson(PluginRegistry.getPluginOverview());
  }

  @Override
  public PostComputesByPluginAndFileResponse postComputesByPluginAndFile(
    String plugin,
    ComputeOutputType file,
    ComputeRequestBase entity
  ) {
    var pluginMeta = PluginRegistry.get(plugin);

    // If there was no plugin with the given name, throw a 404
    if (pluginMeta == null)
      throw new NotFoundException();

    requirePermissions(entity, null);

    var jobFiles = EDA.getComputeJobFiles(pluginMeta, entity);

    var fileName = switch(file) {
      case META -> ReservedFiles.OutputMeta;
      case TABULAR -> ReservedFiles.OutputTabular;
      case STATISTICS -> ReservedFiles.OutputStats;
    };

    var fileRef = jobFiles.stream()
      .filter(f -> f.getName().equals(fileName))
      .findFirst()
      .orElseThrow(NotFoundException::new);

    return PostComputesByPluginAndFileResponse.respond200With((StreamingOutput) output -> {
      try(var input = fileRef.open()) {
        input.transferTo(output);
      }
    });
  }


  // ╔════════════════════════════════════════════════════════════════════╗ //
  // ║  Helper Methods                                                    ║ //
  // ╚════════════════════════════════════════════════════════════════════╝ //

  private <R extends ComputeRequestBase, C> JobResponse submitJob(PluginProvider<R, C> plugin, R entity) {
    var auth = UserProvider.getSubmittedAuth(request).orElseThrow();

    requirePermissions(entity, auth);

    // Validate the request body.
    plugin.getValidator()
      .validate(entity);

    return EDA.submitComputeJob(plugin, entity, auth);
  }

  private void requirePermissions(@NotNull ComputeRequestBase entity, @Nullable Tuples.TwoTuple<String, String> auth) {
    if (auth == null)
      auth = UserProvider.getSubmittedAuth(request).orElseThrow();

    // Check that the user has permission to run compute jobs.
    if (!EDA.getStudyPerms(entity.getStudyId(), auth).allowVisualizations())
      throw new ForbiddenException();
  }
}
