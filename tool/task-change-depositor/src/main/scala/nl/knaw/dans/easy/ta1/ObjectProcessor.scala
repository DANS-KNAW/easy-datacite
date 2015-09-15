package nl.knaw.dans.easy.ta1

import com.yourmediashelf.fedora.client.FedoraClient._
import com.yourmediashelf.fedora.client.FedoraClientException
import org.apache.commons.io.IOUtils
import org.slf4j.LoggerFactory.getLogger

import scala.annotation.tailrec
import scala.collection.JavaConversions._
import scala.util.{Failure, Success, Try}
import scala.xml.{Elem, XML}

/** The executor of the task. */
object ObjectProcessor {
  val log = getLogger(getClass)

  /**
   * Applies transformer to the result of objectQueries.
   *
   * @param settings configuration of desired changes
   */
  def run(implicit settings: Settings) {

    /**
     * Iterates over selected objects.
     *
     * @param query a value from settings.objectQueries
     * @param token a token to fetch the next batch of objects,
     *              defaults to None to start the iteration
     */
    @tailrec
    def iter(query: String, token: Option[String] = None): Unit = {
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
    settings.objectQueries.foreach(query => iter(s"$query"))
    log.info("Done all queries.")
  }

  /**
   * Applies transformer to the datastream of an object.
   *
   * @param pid the fedora-id of the object for which a datastream is changed
   * @param settings configuration of the desired changes and test mode
   * @return
   */
  def fixObject(pid: String)(implicit settings: Settings): Unit = {
    Try(Some(loadDatastreamAsXml(pid, settings))).recoverWith {
      case e: FedoraClientException => if (e.getStatus == 404) Success(None) else Failure(e)
    } match {
      case Failure(e) =>
        log.error(s"can't load: $pid/${settings.datastreamId} ${e.getMessage}")
      case Success(None) =>
        log.warn(s"Not found: $pid/${settings.datastreamId}")
      case Success(Some((rootNode))) =>
        val oldXml = rootNode
        val newXML = settings.transform(pid, oldXml)
        if (newXML.equals(oldXml))
          log.info(s"No changes: $pid/${settings.datastreamId}\nold: $oldXml")
        else {
          log.info(s"Changing: $pid/${settings.datastreamId}\nold: $oldXml\nnew: $newXML")
          settings.updater.updateDatastream(pid, newXML.toString())
        }
    }
  }

  def loadDatastreamAsXml(pid: String, settings: Settings): Elem = {
    log.info(s"Loading: $pid/${settings.datastreamId}")
    val is = getDatastreamDissemination(pid, settings.datastreamId).execute().getEntityInputStream
    try {
      XML.load(is)
    } finally {
      IOUtils.closeQuietly(is)
      is.close()
    }
  }
}