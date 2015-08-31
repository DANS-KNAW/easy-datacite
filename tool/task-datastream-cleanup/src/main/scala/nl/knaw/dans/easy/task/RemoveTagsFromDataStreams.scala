package nl.knaw.dans.easy.task

import com.yourmediashelf.fedora.client.FedoraClient._
import com.yourmediashelf.fedora.client.FedoraClientException

import scala.util.{Failure, Success, Try}
import scala.xml.XML

object RemoveTagsFromDataStreams extends AbstractMain {

  def fixObject(pid: String)(implicit settings: Settings): Unit = {
    log.info(s"Inspecting: $pid")
    Try(Some(XML.load(
      getDatastreamDissemination(pid, settings.streamId).execute().getEntityInputStream
    ))).recoverWith {
      case e: FedoraClientException => if (e.getStatus == 404) Success(None) else Failure(e)
    } match {
      case Failure(e) =>
        log.error(s"can't load: $pid/${settings.streamId} ${e.getMessage}")
      case Success(None) =>
        log.warn(s"Not found: $pid/${settings.streamId}")
      case Success(Some(oldXml)) =>
          val newXML = settings.transformer.transform(oldXml)
          if (newXML.equals(oldXml))
            log.info(s"No changes: $pid/${settings.streamId}\nold: $oldXml")
          else {
            log.info(s"Changing: $pid/${settings.streamId}\nold: $oldXml\nnew: $newXML")
            settings.updater.updateDatastream(pid, newXML.toString())
            Thread.sleep(1000L)
          }
    }
  }
}