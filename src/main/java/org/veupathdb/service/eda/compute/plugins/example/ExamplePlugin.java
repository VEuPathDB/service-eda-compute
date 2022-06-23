package org.veupathdb.service.eda.compute.plugins.example;

import org.jetbrains.annotations.NotNull;
import org.veupathdb.service.eda.compute.plugins.AbstractPlugin;
import org.veupathdb.service.eda.compute.plugins.PluginContext;
import org.veupathdb.service.eda.generated.model.ExamplePluginConfig;
import org.veupathdb.service.eda.generated.model.ExamplePluginRequest;

public class ExamplePlugin extends AbstractPlugin<ExamplePluginRequest, ExamplePluginConfig> {
  @Override
  protected void execute(@NotNull PluginContext<ExamplePluginRequest, ExamplePluginConfig> context) {

  }
}
