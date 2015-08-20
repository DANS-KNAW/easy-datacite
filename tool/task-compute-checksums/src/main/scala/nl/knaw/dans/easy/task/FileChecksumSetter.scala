package nl.knaw.dans.easy.task

import java.io.InputStream

import com.typesafe.config.ConfigFactory
import nl.knaw.dans.easy.lib._
import org.slf4j.{Logger, LoggerFactory}

import scala.util.{Try, Failure, Success}
import scala.xml.XML

import java.io.File
import scala.io.Source

class FileChecksumSetter(logger: Logger,
                         fedora: NiceFedoraRestApi = new NiceFedoraRestApi(ConfigFactory.load), 
                         skipObjIds: Seq[String] = List[String]()
                          ) {

  val doneSetLogger: Logger = LoggerFactory.getLogger("done-set-file-obj-ids")
  
  def run(input: InputStream): Unit = {
    logger.info("EASY-913B set checksums on fedora object streams not embedded in FOXML")
    for (fields <- precalculatedReader(input)) {
      if (!skipObjIds.contains(fields(1))) {
        setChecksum(fields(0), fields(1), fields(2)) match {
          case Failure(e) =>
            logger.error(e.getMessage)
            return
          case Success(_) =>
        }
      } else {
        logger.info(s"Skipping object: ${fields(1)}")
      }
    }
    logger.info("Done.")
  }

  def setChecksum(calculatedChecksum: String, objectId: String, streamId: String): Try[Unit] = {

    fedora.put(s"objects/$objectId/datastreams/$streamId?checksumType=SHA-1&ignoreContent=true&logMessage=Enabled%20checksum") match {
      case Failure(e) =>
        Failure(e)
      case Success(s) =>
        val fedoraChecksum = (XML.loadString(s) \\ "dsChecksum").text
        logger.info(s"SET VALUE: $fedoraChecksum PRE-CALCULATED: $calculatedChecksum, $objectId, $streamId")
        if (!fedoraChecksum.equals(calculatedChecksum))
          Failure(new Exception("checksums do not match"))
        else
          doneSetLogger.info(s"$objectId")
          Success()
    }
  }
}

object FileChecksumSetter {

  def main(args: Array[String]) {
    val logger: Logger = LoggerFactory.getLogger(getClass)
    val skipObjIdsFilename: Option[String] = args.lift(0)
    if (!skipObjIdsFilename.getOrElse("").isEmpty) {
      val skipObjIds: Seq[String] = Source.fromFile(new File(skipObjIdsFilename.get)).getLines().toList.distinct
      logger.info(s"Skipping objects specified in file: ${skipObjIdsFilename.get}")
      new FileChecksumSetter(logger, skipObjIds = skipObjIds).run(input = System.in)
    } else {
      new FileChecksumSetter(logger).run(input = System.in)
    }
  }
}
