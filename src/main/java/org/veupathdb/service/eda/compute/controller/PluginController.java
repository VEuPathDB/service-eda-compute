package org.veupathdb.service.eda.compute.controller;

import jakarta.ws.rs.core.Context;
import org.glassfish.jersey.server.ContainerRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.veupathdb.lib.container.jaxrs.providers.UserProvider;
import org.veupathdb.service.eda.compute.EDA;
import org.veupathdb.service.eda.compute.plugins.PluginProvider;
import org.veupathdb.service.eda.compute.plugins.PluginRegistry;
import org.veupathdb.service.eda.compute.plugins.example.ExamplePluginProvider;
import org.veupathdb.service.eda.generated.model.ComputeRequestBase;
import org.veupathdb.service.eda.generated.model.ExamplePluginRequest;
import org.veupathdb.service.eda.generated.model.JobResponse;
import org.veupathdb.service.eda.generated.resources.Plugins;

public class PluginController implements Plugins {

  private final Logger Log = LoggerFactory.getLogger(getClass());

  @Context
  private ContainerRequest request;

  // ╔═════════════════════════════════════════════════════════════════════╗//
  // ║  Meta Endpoints                                                     ║//
  // ╚═════════════════════════════════════════════════════════════════════╝//

  @Override
  public GetPluginsResponse getPlugins() {
    return GetPluginsResponse.respond200WithApplicationJson(PluginRegistry.getPluginOverview());
  }

  // ╔═════════════════════════════════════════════════════════════════════╗//
  // ║  Plugin Processing Endpoints                                        ║//
  // ║                                                                     ║//
  // ║  Controller methods for handling requests to run specific plugins.  ║//
  // ╚═════════════════════════════════════════════════════════════════════╝//


  @Override
  public PostPluginsExampleResponse postPluginsExample(ExamplePluginRequest entity) {
    return PostPluginsExampleResponse.respond200WithApplicationJson(submitJob(new ExamplePluginProvider(), entity));
  }

  // ╔═════════════════════════════════════════════════════════════════════╗//
  // ║  Helper Methods                                                     ║//
  // ╚═════════════════════════════════════════════════════════════════════╝//

  private <R extends ComputeRequestBase, C> JobResponse submitJob(PluginProvider<R, C> plugin, R entity) {
    return EDA.submitComputeJob(plugin, entity, UserProvider.getSubmittedAuth(request).orElseThrow());
  }
}
