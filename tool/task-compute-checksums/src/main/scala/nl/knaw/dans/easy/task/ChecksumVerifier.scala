package nl.knaw.dans.easy.task

import java.io.InputStream

import com.typesafe.config.ConfigFactory
import nl.knaw.dans.easy.lib.NiceFedoraRestApi
import org.slf4j.{Logger, LoggerFactory}

import scala.util.{Failure, Success}

import java.io.File
import scala.io.Source

class ChecksumVerifier(logger: Logger,
                       fedora: NiceFedoraRestApi = new NiceFedoraRestApi(ConfigFactory.load), 
                       skipObjIds: Seq[String] = List[String]()
                        ) {

  val doneVerifyLogger: Logger = LoggerFactory.getLogger("done-verify-obj-ids")
  
  def run(input: InputStream): Unit = {

    logger.info("EASY-913C verify checksums of fedora object streams against pre-calculated ones")
    for (fields <- precalculatedReader(input)) {
      if (!skipObjIds.contains(fields(1))) {
        verify(fields(0), fields(1), fields(2))
      } else {
        logger.info(s"Skipping object: ${fields(1)}")
      }
    }
    logger.info("Done.")
  }

  def verify(calculatedChecksum: String, objectId: String, streamId: String) = {

    fedora.getXml(s"objects/$objectId/datastreams/$streamId") match {
      case Failure(e) =>
        logger.error(s"$calculatedChecksum, $objectId, $streamId : could not verify input : ${e.getMessage}")
      case Success(xml) =>
        val fedoraCheckSum: String = (xml \\ "dsChecksum").text
        if (calculatedChecksum.equals(fedoraCheckSum))
          logger.info(s"$calculatedChecksum, $objectId, $streamId : checksum OK")
        else logger.error(s"$calculatedChecksum, $objectId, $streamId : checksum mismatch : $fedoraCheckSum")
                  
        doneVerifyLogger.info(s"$objectId") // it is verified, but checksum could be not OK
    }
  }
}

object ChecksumVerifier {

  def main(args: Array[String]) {
    val logger: Logger = LoggerFactory.getLogger(getClass)
    val skipObjIdsFilename: Option[String] = args.lift(0)
    if (!skipObjIdsFilename.getOrElse("").isEmpty) {
      val skipObjIds: Seq[String] = Source.fromFile(new File(skipObjIdsFilename.get)).getLines().toList.distinct
      logger.info(s"Skipping objects specified in file: ${skipObjIdsFilename.get}")
      new ChecksumVerifier(logger, skipObjIds = skipObjIds).run(input = System.in)
    } else {
      new ChecksumVerifier(logger).run(input = System.in)
    }
  }
}
