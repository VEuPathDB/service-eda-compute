package org.veupathdb.service.eda.compute.process

import org.slf4j.LoggerFactory
import org.veupathdb.service.eda.compute.exec.ComputeJobContext
import org.veupathdb.service.eda.compute.jobs.Const
import java.nio.file.Path

class ComputeProcessBuilder(
  val computeJobContext: ComputeJobContext,
  val command: String,
  val workDir: Path
) {

  private val Log = LoggerFactory.getLogger(javaClass)

  private val args = ArrayList<String>(10)
  private val env  = HashMap<String, String>()

  init {
    this.args.add(command)
    this.env["PATH"] = System.getenv("PATH")
  }

  fun addArg(arg: String): ComputeProcessBuilder {
    this.args.add(arg)
    return this
  }

  fun addArgs(vararg args: String): ComputeProcessBuilder {
    this.args.addAll(args)
    return this
  }

  fun addArgs(args: Iterable<String>): ComputeProcessBuilder {
    this.args.addAll(args)
    return this
  }

  fun setEnv(variable: String, value: String): ComputeProcessBuilder {
    this.env[variable] = value
    return this
  }

  fun setEnv(vars: Map<String, String>): ComputeProcessBuilder {
    this.env.putAll(vars)
    return this
  }

  fun execute() {
    Log.info("Executing command {}", command)
    Log.debug("Command arguments: {}", args)
    Log.debug("Command environment: {}", env)

    val proc = ProcessBuilder(args).also {
      it.environment().putAll(env)
      it.directory(workDir.toFile())
      it.redirectError(workDir.resolve(Const.OutputFileErrors).toFile())
    }.start()

    proc.waitFor()


  }
}