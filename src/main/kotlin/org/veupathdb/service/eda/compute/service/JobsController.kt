package org.veupathdb.service.eda.compute.service

import jakarta.ws.rs.NotFoundException
import jakarta.ws.rs.core.StreamingOutput
import org.veupathdb.lib.compute.platform.AsyncPlatform
import org.veupathdb.lib.compute.platform.job.JobFileReference
import org.veupathdb.lib.hash_id.HashID
import org.veupathdb.service.eda.compute.util.toJobResponse
import org.veupathdb.service.eda.generated.resources.Jobs

object JobsController : Jobs {
  override fun getJobsByJobId(jobId: String): Jobs.GetJobsByJobIdResponse =
    Jobs.GetJobsByJobIdResponse.respond200WithApplicationJson(
      (AsyncPlatform.getJob(jobId.toHashID()) ?: throw NotFoundException()).toJobResponse())

  override fun getJobsFilesByJobId(jobId: String) =
    fileList(jobId)
      ?.map(JobFileReference::name)
      ?.let(Jobs.GetJobsFilesByJobIdResponse::respond200WithApplicationJson)
      ?: throw NotFoundException()

  override fun getJobsFilesByJobIdAndFileName(jobId: String, fileName: String) =
    fileList(jobId)
      ?.let { it.find { it.name == fileName } }
      ?.let { StreamingOutput { o -> it.open().use { i -> i.transferTo(o) } } }
      ?.let { Jobs.GetJobsFilesByJobIdAndFileNameResponse.respond200WithTextPlain(it,
        Jobs.GetJobsFilesByJobIdAndFileNameResponse.headersFor200().withContentDisposition("attachment; filename=$fileName")) }
      ?: throw NotFoundException()

  @Suppress("NOTHING_TO_INLINE")
  private inline fun fileList(rawID: String) =
    AsyncPlatform.getJob(rawID.toHashID())
      ?.let { AsyncPlatform.getJobFiles(it.jobID) }

  @Suppress("NOTHING_TO_INLINE")
  private inline fun String.toHashID(): HashID {
    return try {
      // Attempt to parse the raw string as a HashID
      HashID(this)
    } catch (e: IllegalArgumentException) {
      // If it is not a valid hash ID string, throw a not found exception as
      // there are no jobs that can be located with an invalid ID.
      throw NotFoundException()
    }
  }
}