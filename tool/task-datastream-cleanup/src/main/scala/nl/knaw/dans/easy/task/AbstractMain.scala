package nl.knaw.dans.easy.task

import com.yourmediashelf.fedora.client.FedoraClient._
import org.slf4j.LoggerFactory

import scala.annotation.tailrec
import scala.collection.JavaConversions._

abstract class AbstractMain {
  val log = LoggerFactory.getLogger(getClass)

  def main(args: Array[String]) {

    implicit val settings: Settings = CommandLineOptions.parse(args)

    @tailrec
    def iter(query: String, token: Option[String]): Unit = {
      val objectsQuery = findObjects().maxResults(settings.batchSize).pid().query(query)
      val objectsResponse = token match {
        case None =>
          log.info(s"Start $query")
          objectsQuery.execute
        case Some(t) =>
          objectsQuery.sessionToken(t).execute
      }
      objectsResponse.getPids.foreach(fixObject)
      if (objectsResponse.hasNext)
        iter(query, Some(objectsResponse.getToken))
      else log.info(s"Finished $query")
    }

    log.info(s"Running with $settings")
    settings.idPatterns.foreach(idPattern => iter(s"pid~$idPattern", None))
    log.info("Done all.")
  }

  def fixObject(pid: String)(implicit settings: Settings): Unit
}