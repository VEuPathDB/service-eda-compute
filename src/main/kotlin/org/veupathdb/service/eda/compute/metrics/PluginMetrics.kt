package org.veupathdb.service.eda.compute.metrics

import io.prometheus.client.Counter
import io.prometheus.client.Histogram

object PluginMetrics {

  val execTime: Histogram = Histogram.build()
    .buckets(
      0.1,
      0.5,
      1.0,
      1.5,
      3.0,
      5.0,
      10.0,
      15.0,
      30.0,
      60.0,
      120.0,
      180.0,
      300.0,
      3000.0,
    )
    .labelNames("plugin_name")
    .name("plugin_exec_time")
    .help("Plugin execution time in seconds.")
    .register()

  val successes: Counter = Counter.build()
    .labelNames("plugin_name")
    .name("plugin_successes")
    .help("Number of successful executions of a plugin")
    .register()

  val failures: Counter = Counter.build()
    .labelNames("plugin_name")
    .name("plugin_failures")
    .help("Number of failed executions of a plugin")
    .register()
}