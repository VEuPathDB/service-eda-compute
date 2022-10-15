package org.veupathdb.service.eda.compute.plugins.example;

import org.jetbrains.annotations.NotNull;
import org.veupathdb.service.eda.common.model.ReferenceMetadata;
import org.veupathdb.service.eda.compute.plugins.*;
import org.veupathdb.service.eda.generated.model.ExamplePluginConfig;
import org.veupathdb.service.eda.generated.model.ExamplePluginRequest;
import org.veupathdb.service.eda.generated.model.ExamplePluginRequestImpl;

import java.util.function.Supplier;

public class ExamplePluginProvider implements PluginProvider<ExamplePluginRequest, ExamplePluginConfig> {
  @NotNull
  @Override
  public String getUrlSegment() {
    return "example";
  }

  @NotNull
  @Override
  public String getDisplayName() {
    return "Example Plugin";
  }

  @NotNull
  @Override
  public PluginQueue getTargetQueue() {
    return PluginQueue.Fast;
  }

  @NotNull
  @Override
  public Class<? extends ExamplePluginRequest> getRequestClass() {
    return ExamplePluginRequestImpl.class;
  }

  @NotNull
  @Override
  public PluginConfigValidator<ExamplePluginRequest> getValidator() {
    return new ExamplePluginInputValidator();
  }

  @NotNull
  @Override
  public ExamplePlugin createPlugin(@NotNull PluginContext<ExamplePluginRequest, ExamplePluginConfig> context) {
    return new ExamplePlugin(context);
  }
}
