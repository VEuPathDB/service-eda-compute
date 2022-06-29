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
import org.veupathdb.service.eda.compute.plugins.PluginMeta;
import org.veupathdb.service.eda.compute.plugins.PluginProvider;
import org.veupathdb.service.eda.compute.plugins.PluginRegistry;
import org.veupathdb.service.eda.compute.plugins.example.ExamplePluginProvider;
import org.veupathdb.service.eda.generated.model.ComputeRequestBase;
import org.veupathdb.service.eda.generated.model.ExamplePluginRequest;
import org.veupathdb.service.eda.generated.model.JobResponse;
import org.veupathdb.service.eda.generated.resources.Computes;
import org.veupathdb.service.eda.generated.support.ResponseDelegate;

import java.util.function.Function;


/**
 * Compute Plugins Controller
 * <p>
 * This controller is the home location where specific compute plugin endpoints
 * are registered.
 * <p>
 * New plugin endpoints should be added in the Plugin Endpoints region in this
 * file, after the "Plugin Endpoints" doc block and before the "endregion Plugin
 * Endpoints" statement.
 * <p>
 * Plugin endpoints should follow the example set by the
 * {@link #postComputesExample(ExamplePluginRequest)} method and call the
 * {@link #submitJob(PluginProvider, ComputeRequestBase)} method, passing in an
 * instance of the target {@link PluginProvider} for their plugin along with the
 * raw request body (entity).
 *
 * @author Elizabeth Paige Harper - https://github.com/foxcapades
 * @since 1.0.0
 */
public class ComputeController implements Computes {

  private final Logger Log = LogManager.getLogger(getClass());

  @Context
  private ContainerRequest request;

  // region Plugin Endpoints
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

  @Override
  public PostComputesExampleByFileResponse postComputesExampleByFile(String file, ExamplePluginRequest entity) {
    return resultFile(new ExamplePluginProvider(), file, entity, PostComputesExampleByFileResponse::respond200WithTextPlain);
  }

  // endregion Plugin Endpoints

  // region Constant Endpoints
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

  // endregion Constant Endpoints

  // ╔════════════════════════════════════════════════════════════════════╗ //
  // ║  Helper Methods                                                    ║ //
  // ╚════════════════════════════════════════════════════════════════════╝ //

  /**
   * Submits a new plugin execution job to one of the internal job queues.
   *
   * @param plugin {@code PluginProvider} that will be used to validate and
   * submit the new job request.
   *
   * @param entity The raw request payload.
   *
   * @return Basic information about the submitted job to be returned to the
   * caller.
   *
   * @param <R> Type of the raw request body that the target plugin accepts.
   *
   * @param <C> Type of the configuration wrapped by the raw request body that
   * the target plugin accepts.
   */
  private <R extends ComputeRequestBase, C> JobResponse submitJob(PluginProvider<R, C> plugin, R entity) {
    var auth = UserProvider.getSubmittedAuth(request).orElseThrow();

    requirePermissions(entity, auth);

    // Validate the request body.
    plugin.getValidator()
      .validate(entity);

    return EDA.getOrSubmitComputeJob(plugin, entity, auth);
  }

  public <R extends ResponseDelegate, P extends ComputeRequestBase> R resultFile(
    PluginMeta<P> plugin,
    String file,
    ComputeRequestBase entity,
    Function<Object, R> responseFn
  ) {
    // If there was no plugin with the given name, throw a 404
    if (plugin == null)
      throw new NotFoundException();

    requirePermissions(entity, null);

    var jobFiles = EDA.getComputeJobFiles(plugin, entity);

    var fileName = switch(file.toLowerCase()) {
      case "meta"       -> ReservedFiles.OutputMeta;
      case "tabular"    -> ReservedFiles.OutputTabular;
      case "statistics" -> ReservedFiles.OutputStats;
      default           -> throw new NotFoundException();
    };

    var fileRef = jobFiles.stream()
      .filter(f -> f.getName().equals(fileName))
      .findFirst()
      .orElseThrow(NotFoundException::new);

    return responseFn.apply((StreamingOutput) output -> {
      try(var input = fileRef.open()) {
        input.transferTo(output);
      }
    });
  }

  /**
   * Ensures that the requester has the required permission(s) to call a plugin
   * endpoint for a target study.
   * <p>
   * As of the time of this writing, the permission required for plugin
   * execution and lookup is "allowVisualizations".
   *
   * @param entity Raw request body containing the ID of the study the user must
   * have permissions on.
   *
   * @param auth The auth header to use in validation, or {@code null} for the
   * auth header from the {@link #request} context to be used.
   *
   * @throws ForbiddenException If the requester does not have the required
   * permission(s) on the target study.
   */
  private void requirePermissions(@NotNull ComputeRequestBase entity, @Nullable Tuples.TwoTuple<String, String> auth) {
    if (auth == null)
      auth = UserProvider.getSubmittedAuth(request).orElseThrow();

    // Check that the user has permission to run compute jobs.
    if (!EDA.getStudyPerms(entity.getStudyId(), auth).allowVisualizations())
      throw new ForbiddenException();
  }
}
