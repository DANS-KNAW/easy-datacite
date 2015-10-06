package nl.knaw.dans.easy.task

import java.io.InputStream

import com.yourmediashelf.fedora.client.FedoraClient._
import com.yourmediashelf.fedora.client.FedoraClientException
import com.yourmediashelf.fedora.client.request.RiSearch
import org.apache.commons.io.IOUtils
import org.slf4j.LoggerFactory

import scala.io.Source
import scala.util.{Failure, Success, Try}
import scala.xml.{XML, Node}

object Util {

  val log = LoggerFactory.getLogger(getClass)

  def getXml(pid: String, streamId: String): Option[Node] = {
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

  def getFileIdentifiers(datasetId: String): List[String] = {

    val query = s"PREFIX dans: <http://dans.knaw.nl/ontologies/relations#> " +
      s"PREFIX fmodel: <info:fedora/fedora-system:def/model#> " +
      s"SELECT ?s WHERE {?s dans:isSubordinateTo <info:fedora/$datasetId> . ?s fmodel:hasModel <info:fedora/easy-model:EDM1FILE>}"

    val response = new RiSearch(query)
      .lang("sparql")
      .format("csv")
      .execute()

    var ins: InputStream = null
    try {
      ins = response.getEntityInputStream
      val lines = Source.fromInputStream(ins).getLines
      lines.toList.tail.map(_.split("/").last)
    } finally {
      IOUtils.closeQuietly(ins)
    }
  }
}