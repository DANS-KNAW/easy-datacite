package nl.knaw.dans.easy.task


import com.yourmediashelf.fedora.client.FedoraClient._
import com.yourmediashelf.fedora.client.FedoraClientException
import nl.knaw.dans.easy.task.FindDraftContainingLicense._

import scala.util.{Failure, Success, Try}
import scala.xml.{XML, Node}

object Util {

    def getXml(pid: String, streamId : String) : Option[Node] = {
      Try(Some(XML.load(
        getDatastreamDissemination(pid, streamId).execute().getEntityInputStream
      ))).recoverWith {
        case e: FedoraClientException => if (e.getStatus == 404) Success(None) else Failure(e)
      } match {
        case Failure(e) =>
          log.error(s"can't load: $pid/$streamId ${e.getMessage}"); None
        case Success(None) =>
          log.error(s"Not found: $pid/$streamId"); None
        case Success(Some(xml)) => Some(xml)
      }
    }

    def hasDatastream(pid: String, streamId : String) : Boolean = {
      Try(Some(XML.load(
        getDatastream(pid, streamId).execute().getEntityInputStream
      ))).recoverWith {
        case e: FedoraClientException => if (e.getStatus == 404) Success(None) else Failure(e)
      } match {
        case Failure(e) =>
          log.error(s"can't load: $pid/$streamId ${e.getMessage}"); false
        case Success(None) =>
          log.info(s"Not found: $pid/$streamId"); false
        case Success(xml) =>
          log.info("pid: " + pid + " has DATASET_LICENSE"); true
      }
    }

}