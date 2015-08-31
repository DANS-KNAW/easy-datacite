package nl.knaw.dans.easy.task

import com.yourmediashelf.fedora.client.FedoraClient._
import com.yourmediashelf.fedora.client.FedoraClientException

import scala.util.{Failure, Success, Try}

object RemoveDataStreams extends AbstractMain {

  def fixObject(pid: String)(implicit settings: Settings): Unit = {
    log.info(s"Inspecting: $pid")
    Try(Some(
      purgeDatastream(pid, settings.streamId).execute()
    )).recoverWith {
      case e: FedoraClientException => if (e.getStatus == 404) Success(None) else Failure(e)
    } match {
      case Failure(e) =>
        log.error(s"can't purge: $pid/${settings.streamId} ${e.getMessage}")
      case Success(Some(_)) =>
        log.info(s"Purged: $pid/${settings.streamId}")
      case Success(None) =>
        log.warn(s"Not found: $pid/${settings.streamId}")
    }
  }
}