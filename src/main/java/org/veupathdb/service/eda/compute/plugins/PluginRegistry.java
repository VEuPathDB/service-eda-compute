package org.veupathdb.service.eda.compute.plugins;

import org.jetbrains.annotations.NotNull;
import org.veupathdb.service.eda.compute.plugins.example.ExamplePluginProvider;
import org.veupathdb.service.eda.generated.model.ComputeRequestBase;
import org.veupathdb.service.eda.generated.model.PluginOverview;
import org.veupathdb.service.eda.generated.model.PluginOverviewImpl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PluginRegistry {

  private static final Map<String, PluginProvider<? extends ComputeRequestBase, ?>> registry;

  static {
    registry = new HashMap<>() {{
      put("example", new ExamplePluginProvider());
    }};
  }

  @NotNull
  @SuppressWarnings("unchecked")
  public static PluginProvider<ComputeRequestBase, ?> get(String name) {
    var raw = registry.get(name);

    if (raw == null)
      throw new IllegalArgumentException("Unrecognized plugin name \"$name\".");

    return (PluginProvider<ComputeRequestBase, ?>) raw;
  }

  @NotNull
  public static List<PluginOverview> getPluginOverview() {
    var out = new ArrayList<PluginOverview>(registry.size());

    registry.values().forEach(it -> {
      var impl = new PluginOverviewImpl();
      impl.setName(it.getUrlSegment());
      impl.setDisplayName(it.getDisplayName());
      impl.setDescription(it.getDescription());
      out.add(impl);
    });

    return out;
  }
}