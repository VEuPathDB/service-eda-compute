package org.veupathdb.service.eda.compute.plugins;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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

  private static final Logger Log = LogManager.getLogger(PluginRegistry.class);

  private static final Map<String, PluginProvider<? extends ComputeRequestBase, ?>> Registry;

  static {
    //
    // Begin Plugin List
    //
    // Add new plugins here.
    //
    var pluginList = List.<PluginProvider<?, ?>>of(
      new ExamplePluginProvider()
    );
    //
    // End Plugin List
    //

    Log.info("Registering {} plugins.", pluginList.size());
    Registry = new HashMap<>(pluginList.size());
    for (var plugin : pluginList)
      Registry.put(plugin.getUrlSegment(), plugin);
  }

  @NotNull
  @SuppressWarnings("unchecked")
  public static PluginProvider<ComputeRequestBase, Object> get(String name) {
    var raw = Registry.get(name);

    if (raw == null)
      throw new IllegalArgumentException("Unrecognized plugin name \"$name\".");

    return (PluginProvider<ComputeRequestBase, Object>) raw;
  }

  /**
   * Builds an overview of details about the plugins currently registered with
   * the service.
   *
   * @return
   */
  @NotNull
  public static List<PluginOverview> getPluginOverview() {
    var out = new ArrayList<PluginOverview>(Registry.size());

    for (var it : Registry.values()) {
      var impl = new PluginOverviewImpl();
      impl.setName(it.getUrlSegment());
      impl.setDisplayName(it.getDisplayName());
      impl.setDescription(it.getDescription());
      out.add(impl);
    }

    return out;
  }
}