package nl.knaw.dans.easy.lib

import java.io.InputStream
import java.net.{MalformedURLException, URL}

import com.typesafe.config.{Config, ConfigException}
import org.joda.time.LocalDateTime
import org.slf4j.LoggerFactory

import scala.math._
import scala.util.{Failure, Success, Try}
import scala.xml.{Elem, XML}
import scalaj.http.Http

/** See also <a href="https://wiki.duraspace.org/display/FEDORA35/REST+API">FEDORA35/REST+API</a> */
case class NiceFedoraRestApi(baseUrl: String, user: String, password: String, requestInterval: Int, finalRetryDelay: Int) {

  private val log = LoggerFactory.getLogger(getClass)
  private var lastResponseCompletion = LocalDateTime.now.minusMillis(requestInterval)

  log.info(s"fedoraBaseUrl=$baseUrl")
  log.info(s"user=$user")
  log.info(s"requestInterval=$requestInterval")
  log.info(s"finalRetryDelay=$finalRetryDelay")

  /**
   * @throws MalformedURLException if the value for fedora.base.url is invalid
   * @throws ConfigException.Missing if value is absent or null
   * @throws ConfigException.WrongType if value is not convertible
   */
  def this(conf: Config) = {
    this(
      baseUrl = new URL(conf.getString("fedora.base.url")).toString,
      user = conf.getString("fedora.user"),
      password = conf.getString("fedora.password"),
      requestInterval = conf.getInt("fedora.request.interval.millis"),
      finalRetryDelay = conf.getInt("fedora.request.retry.delay.final.seconds")
    )
  }

  /** gets ids of data streams with a specific control group
    *
    * @param objectId for example "easy-file:1"
    * @param controlGroup one of "X", "M", "R", or "E" (Inline *X*ML, *M*anaged Content, *R*edirect, or *E*xternal Referenced)
    * @return zero (both in case of an error or in case of just none) or more ids of data streams
    */
  def getStreamIds(objectId: String, controlGroup: String): Seq[String] = {

    getXml(s"objects/$objectId/objectXML") match {
      case Failure(e) =>
        log.error(s"could not get FOXML of $objectId : ${e.getMessage}")
        Seq()
      case Success(responseBody) =>
        val result = for {node <- (responseBody \\ "datastream").theSeq
                          if node.attribute("CONTROL_GROUP").head.head.text.equals(controlGroup)
        } yield node.attribute("ID").head.head.text
        log.info(s"${result.length} external stream-id(s) for $objectId : ${result.toArray.deep}")
        result
    }
  }

  /** gets ids of all the data streams of an object
   *
   * @param objectId for example "easy-file:1"
   * @return zero (both in case of an error or in case of just none) or more ids of data streams
   */
  def getAllStreamIds(objectId: String): Seq[String] = {

    getXml(s"objects/$objectId/datastreams") match {
      case Failure(e) =>
        log.error(s"could not get stream-ids of $objectId : ${e.getMessage}")
        Seq()
      case Success(responseBody) =>
        val result = (responseBody \\ "datastream").theSeq.map(_.attribute("dsid").head.head.text)
        log.info(s"${result.length} streams-ids for $objectId : ${result.toArray.deep}")
        result
    }
  }

  def openDataStream (objectId: String, streamId: String): InputStream = {
    dontHurry()
    val url: URL = new URL(s"$baseUrl/objects/$objectId/datastreams/$streamId/content")
    val stream = url.openStream()
    lastResponseCompletion = LocalDateTime.now // is not the time of closing the stream
    stream
  }

  /**
   * @param path URI portion between baseUrl and ?
   * @param params URI portion after ?format=xml
   */
  def getXml(path: String, params: String=""): Try[Elem] = {

    get(s"$path?format=xml&$params") match {
      case Failure(e) => Failure(e)
      case Success(s) =>
        Success(XML.loadString(s))
    }
  }
  
  @annotation.tailrec
  private def retry[Try[String]](availableRetries: Int)(action:  => Try[String]): Try[String] = {
      val r = action
      r match {
        case Success(s) => return r
        case Failure(f) => if (availableRetries > 0) {
             log.info(s"FAILED: ${f.getMessage}")
             var delay:Int = 1
             if (availableRetries == 1) 
               delay = finalRetryDelay
             log.info(s"Trying again in $delay s...")
             Thread.sleep(delay*1000)
             retry(availableRetries - 1)(action)
        } else {
          r
        }
      }
  }

  /** @param query URI without the fedora base URL */
  def put(query: String):Try[String] = {
    retry (10) {
      dontHurry()
      val url: String = s"$baseUrl/$query"
      val result = Http(url)
        .timeout(connTimeoutMs = 5000, readTimeoutMs = 180000)
        .method("PUT")
        .auth(user, password).asString
      lastResponseCompletion = LocalDateTime.now
      if (result.code != 200)
        Failure(new scala.Exception(s"$url : HTTP-result code=${result.code} body: ${result.body}"))
      else
        Success(result.body)
    }
  }

  /** @param query URI without the fedora base URL */
  def get(query: String): Try[String] = {
	  retry (1) {
			  dontHurry()
			  val url: String = s"$baseUrl/$query"
			  val result = Http(url)
			  .timeout(connTimeoutMs = 5000, readTimeoutMs = 180000)
			  .method("GET")
			  .auth(user, password)
			  .header("Content-Type", "text/xml") // fedora needs to have 'text/xml' for the xml datastreams !
			  .header("Charset", "UTF-8").asString
			  lastResponseCompletion = LocalDateTime.now
			  log.debug(s"${result.code} $url")
			  if (result.code != 200)
				  Failure(new scala.Exception(s"$url : HTTP-result code: ${result.code} body: ${result.body}"))
				  else
					  Success(result.body)
		 }
  }

  private def dontHurry(): Unit = {
    val sinceLastCompletion: Long  = LocalDateTime.now.toDate.getTime - lastResponseCompletion.toDate.getTime
    val waitFor = max(0,requestInterval - sinceLastCompletion)
    log.debug(s"time since last call: $sinceLastCompletion, waiting for another $waitFor")
    Thread.sleep(waitFor)
  }
}
