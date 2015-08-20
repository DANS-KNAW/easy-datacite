package nl.knaw.dans.easy.lib

import com.typesafe.config.ConfigFactory
import org.slf4j.LoggerFactory

import scala.annotation.tailrec
import scala.language.postfixOps
import scala.util.{Failure, Success, Try}
import scala.xml.XML
import scalaj.http.Http

/** See also <a href="https://wiki.duraspace.org/display/FEDORA35/REST+API#RESTAPI-findObjects">find objects</a>
  *
  * @param objectIds defaults to object ids of datasets, suffixes "/objects?...&query=pid~"
  * @param fedoraBaseUrl defaults to deasy, prefixes "/objects?...&query=pid~"
  * @param chunkSize number of ids fetched at once from fedora (&maxResults=...)
  * @param callback has one argument: an ID of a fedora object
  */
case class FedoraObjectProcessor(objectIds: String = "easy-dataset:*",
                                 skipObjIds: Seq[String] = List[String](),
                                 callback: (String) => Try[Unit],
                                 fedoraBaseUrl: String = "http://localhost:18080/fedora",
                                 chunkSize: Int = Try(ConfigFactory.load().getInt("fedora.object.processor.chunk.size")
                                                     ).getOrElse(500)) {
  private val log = LoggerFactory.getLogger(getClass)
  log.info(s"fedoraBaseUrl=$fedoraBaseUrl")
  log.info(s"objectIds=$objectIds")
  log.info(s"chunkSize=$chunkSize")
  if (!(objectIds matches "[-a-z0-9._*?]+:[-a-z0-9._*?]+"))
    log.error( s"$objectIds is not a valid Fedora ID filter. Examples: '*:*' for all objects, 'easy-f*:1??' for files and folders with an ID of 100 to 199")

  def run = process(token = None)

  @tailrec
  private def process(token: Option[String]): Try[Unit] = {
    getChunkOfObjectIDs(token) match {
      case Failure(e) => Failure(e)
      case Success((fedoraIds, lastToken)) =>
        Try(fedoraIds.foreach(fedoraId => { // inner loop over id-s in a chunk
          if (!skipObjIds.contains(fedoraId)) {
            callback(fedoraId) match {
              case Failure(e) => throw e // terminate the inner loop
              case Success(_) => ()
            }
          } else {
            log.info(s"Skipping object: $fedoraId")
          }
        })) match {
          case Failure(e) =>
            Failure(e) // terminate the outer loop because a callback failed
          case Success(_) =>
            if (lastToken.isEmpty)
              Success(Unit) // natural termination of the outer loop
            else
              process(lastToken) // the tail-recursive call for the outer loop
        }
    }
  }

  protected def getChunkOfObjectIDs(token: Option[String]): Try[(Seq[String], Option[String])] = {
    val params = s"pid=true&query=pid~$objectIds&maxResults=$chunkSize" +
      (token match {
        case None => ""
        case Some(t) => s"&sessionToken=$t"
      })

    // TODO make use of NiceFedoraRestApi.getXml
    val query: String = s"$fedoraBaseUrl/objects?resultFormat=xml&$params"
    log.info(query)
    val result = Http(query)
      .header("Content-Type", "application/xml")
      .header("Charset", "UTF-8").asString
    if (result.isSuccess) {
      val xml = XML.loadString(result.body)

      val objectIdNodes = xml \\ "resultList" \ "objectFields" \ "pid"
      val newTokenNodes = xml \\ "listSession" \ "token"
      val newToken = if (newTokenNodes.nonEmpty) Some(newTokenNodes.text) else None
      val objectIds = objectIdNodes.map(_.text)
      Success((objectIds, newToken))
    } else {
      Failure(new Exception(s"Get request failed: ${result.statusLine} FOR: $params"))
    }
  }
}
