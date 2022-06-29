package org.veupathdb.service.eda.compute.plugins

import org.veupathdb.service.eda.common.client.spec.StreamSpec
import org.veupathdb.service.eda.compute.jobs.ReservedFiles

interface Plugin {

  /**
   * One or more [StreamSpec] that will be used to fetch the tabular data
   * required by this plugin.
   *
   * Each `StreamSpec` in this list will be used to download the tabular data
   * from the EDA Merge Service into a file in the current job's local scratch
   * workspace.  The created files will be named with the stream name specified
   * in each `StreamSpec`.
   *
   * The files can be accessed using the job workspace handle provided in the
   * [context] property.
   *
   * For example, if your plugin defines a [StreamSpec] with the stream name
   * "foobar", the downloaded file would be available at execution time by
   * calling:
   * ```
   * getContext().getWorkspace().openStream("foobar")
   * ```
   *
   * Because the name of the stream determines the name of the file, the
   * following reserved file names are not valid for use as stream names:
   * * `input-meta`
   * * `input-config`
   * * `input-request`
   * * `output-stats`
   * * `output-meta`
   * * `output-data`
   * * `error.log`
   * * `exception.log`
   *
   * If any [StreamSpec] is defined with one of the above reserved file names as
   * a stream name, an error will be logged and the plugin will not be executed.
   */
  val streamSpecs: List<StreamSpec>

  /**
   * Executes the plugin.
   *
   * This method MUST NOT throw any exceptions.  Exceptions from the execution
   * of this plugin should be caught, and the stacktrace written out to the
   * workspace file name [ReservedFiles.OutputException].
   */
  fun run()
}