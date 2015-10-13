package nl.knaw.dans.easy.task

import com.yourmediashelf.fedora.client.FedoraClient._
import com.yourmediashelf.fedora.client.FedoraClientException
import org.apache.commons.io.IOUtils
import org.slf4j.LoggerFactory

import scala.util.{Failure, Success, Try}
import scala.xml.{Elem, XML, Node}

object Util {

  val log = LoggerFactory.getLogger(getClass)

  def getXml(pid: String, streamId: String): Option[Node] = {
    loadDatastreamAsXml(pid, streamId) match {
      case Failure(e) =>
        log.error(s"can't load: $pid/$streamId ${e.getMessage}"); None
      case Success(None) =>
        log.error(s"Not found: $pid/$streamId"); None
      case Success(Some(xml)) => Some(xml)
    }
  }

  def loadDatastreamAsXml(pid: String, streamId: String): Try[Option[Node]] = {
    val result = for {
      is   <- Try { getDatastreamDissemination(pid, streamId).execute().getEntityInputStream }
      node <- Try { Some(XML.load(is)) }
      _    <- Try { IOUtils.closeQuietly(is) }
    } yield node

    result.recoverWith {
      case e: FedoraClientException => if (e.getStatus == 404) Success(None) else Failure(e)
    }
  }
}
