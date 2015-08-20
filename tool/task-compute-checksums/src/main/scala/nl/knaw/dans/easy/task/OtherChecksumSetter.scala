package nl.knaw.dans.easy.task

import java.io.InputStream

import com.typesafe.config.ConfigFactory
import nl.knaw.dans.easy.lib._
import org.slf4j.{Logger, LoggerFactory}

import scala.io.Source
import scala.util.{Failure, Success, Try}
import scala.xml.XML

import java.io.File
import scala.io.Source

case class OtherChecksumSetter(fedora: NiceFedoraRestApi = new NiceFedoraRestApi(ConfigFactory.load()),
                               logger: Logger = LoggerFactory.getLogger(getClass),
                               skippedDatastreams: InputStream) {

  logger.info("fetching unique lines from stdin")
  var externalStreamIds: Seq[String] = Source.fromInputStream(skippedDatastreams).getLines().toList.distinct
  logger.info("completed reading stdin")

  val doneSetLogger: Logger = LoggerFactory.getLogger("done-set-other-obj-ids")
    
  def set(objectId: String): Try[Unit] = {
    for {streamId <- fedora.getAllStreamIds(objectId)
         if !externalStreamIds.contains(streamId) && streamId != "DC" // also skip DC because it fails
    } {
      fedora.put(s"objects/$objectId/datastreams/$streamId?checksumType=SHA-1&format=xml&ignoreContent=true&logMessage=Enabled%20checksum") match {
        case Failure(e) =>
          logger.error(s"failed to assign checksum to $streamId of $objectId: ${e.getMessage}")
          return Failure(e)
        case Success(s) =>
          val checksum = (XML.loadString(s) \\ "dsChecksum").text
          logger.info(s"$checksum is checksum for $streamId of $objectId")
      }
    }
    
    doneSetLogger.info(s"$objectId")
    
    Success(Unit)
  }
}

object OtherChecksumSetter {

  def main(args: Array[String]) {
    val logger: Logger = LoggerFactory.getLogger(getClass)
    logger.info("EASY-913B set checksums of fedora object streams except for EASY_FILE streams")

    val cs: OtherChecksumSetter = OtherChecksumSetter(skippedDatastreams = System.in)
    var skipObjIds: Seq[String] = List[String]()
    
    val skipObjIdsFilename: Option[String] = args.lift(1)
    if (!skipObjIdsFilename.getOrElse("").isEmpty) {
      skipObjIds = Source.fromFile(new File(skipObjIdsFilename.get)).getLines().toList.distinct
      logger.info(s"Skipping objects specified in file: ${skipObjIdsFilename.get}")
    }
   
    val objectProcessor = new FedoraObjectProcessor(
      objectIds = args(0),
      skipObjIds,
      callback = cs.set,
      fedoraBaseUrl = cs.fedora.baseUrl
    )

    logger.info("Start processing")
    objectProcessor.run
    logger.info("Done.")
  }
}
