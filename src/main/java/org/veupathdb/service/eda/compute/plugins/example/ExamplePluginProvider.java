package org.veupathdb.service.eda.compute.plugins.example;

import org.jetbrains.annotations.NotNull;
import org.veupathdb.service.eda.compute.plugins.AbstractPlugin;
import org.veupathdb.service.eda.compute.plugins.PluginProvider;
import org.veupathdb.service.eda.compute.plugins.PluginQueue;
import org.veupathdb.service.eda.generated.model.ExamplePluginConfig;
import org.veupathdb.service.eda.generated.model.ExamplePluginRequest;

public class ExamplePluginProvider implements PluginProvider<ExamplePluginRequest, ExamplePluginConfig> {
  @NotNull
  @Override
  public String getUrlSegment() {
    return "example";
  }

  @NotNull
  @Override
  public String getDisplayName() {
    return "Example";
  }

  @NotNull
  @Override
  public PluginQueue getTargetQueue() {
    return PluginQueue.Fast;
  }

  @NotNull
  @Override
  public Class<ExamplePluginRequest> getRequestClass() {
    return ExamplePluginRequest.class;
  }

  @NotNull
  @Override
  public AbstractPlugin<ExamplePluginRequest, ExamplePluginConfig> createPlugin() {
    return null;
  }
}
