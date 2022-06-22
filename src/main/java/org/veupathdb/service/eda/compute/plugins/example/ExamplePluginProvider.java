package org.veupathdb.service.eda.compute.plugins.example;

import org.jetbrains.annotations.NotNull;
import org.veupathdb.service.eda.compute.plugins.AbstractPlugin;
import org.veupathdb.service.eda.compute.plugins.PluginContext;
import org.veupathdb.service.eda.compute.plugins.PluginProvider;
import org.veupathdb.service.eda.generated.model.ExamplePluginConfig;
import org.veupathdb.service.eda.generated.model.ExamplePluginRequest;

public class ExamplePluginProvider implements PluginProvider<ExamplePluginRequest, ExamplePluginConfig> {
  @NotNull
  @Override
  public String getUrlSegment() {
    return null;
  }

  @NotNull
  @Override
  public String getDisplayName() {
    return null;
  }

  @NotNull
  @Override
  public Class<ExamplePluginRequest> getRequestClass() {
    return null;
  }

  @NotNull
  @Override
  public AbstractPlugin<ExamplePluginRequest, ExamplePluginConfig> createPlugin(
    @NotNull PluginContext<ExamplePluginRequest, ExamplePluginConfig> ctx
  ) {
    return null;
  }
}
