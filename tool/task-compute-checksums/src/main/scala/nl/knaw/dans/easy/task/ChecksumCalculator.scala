package nl.knaw.dans.easy.task

import com.typesafe.config.ConfigFactory
import nl.knaw.dans.easy.lib._
import org.apache.commons.codec.digest.DigestUtils
import org.slf4j.{Logger, LoggerFactory}

import scala.util.{Success, Try}

import java.io.File
import scala.io.Source

case class ChecksumCalculator(fedora: NiceFedoraRestApi,
                              dataLogger: Logger = LoggerFactory.getLogger("checksums"),
                              logger: Logger = LoggerFactory.getLogger(getClass)) {

  val doneCalcLogger: Logger = LoggerFactory.getLogger("done-calc-file-obj-ids")

  def calculateStreamChecksums(objectId: String): Try[Unit] = {
    for {streamId <- fedora.getStreamIds(objectId,"M")} {
      dataLogger.info(s"${getCheckSum(objectId,streamId)}\t$objectId\t$streamId")
      ChecksumCalculator.streamIds.add(streamId)
    }
    
    doneCalcLogger.info(s"$objectId") // Maybe only log if no streams fail!

    Success(Unit)
  }

  private def getCheckSum(objectId: String, streamId:String): String = {
    try {
      val stream = fedora.openDataStream(objectId,streamId)
      try {
        // as found in nl.knaw.dans.common.lang.repo.AbstractBinaryUnit.calculateSha1Checksum
        DigestUtils.sha1Hex(stream)
      }
      finally stream.close()
    }
    catch {
      case e: Throwable => s"??? ${e.getClass} ${e.getMessage}"
    }
  }
}

object ChecksumCalculator {

  val streamIds = new scala.collection.mutable.HashSet[String]()

  def main(args: Array[String]) {

    val logger = LoggerFactory.getLogger(getClass)
    logger.info("EASY-913A precalculate checksums of streams that are not stored in the FOXML of Fedora objects")
    val cc = new ChecksumCalculator(new NiceFedoraRestApi(ConfigFactory.load()))

    var skipObjIds: Seq[String] = List[String]()
    
    val skipObjIdsFilename: Option[String] = args.lift(1)
    if (!skipObjIdsFilename.getOrElse("").isEmpty) {
      skipObjIds = Source.fromFile(new File(skipObjIdsFilename.get)).getLines().toList.distinct
      logger.info(s"Skipping objects specified in file: ${skipObjIdsFilename.get}")
    }
    
    val objectProcessor = new FedoraObjectProcessor(
      objectIds = args(0),
      skipObjIds,
      callback = cc.calculateStreamChecksums,
      fedoraBaseUrl = cc.fedora.baseUrl
    )
    objectProcessor.run

    val streamIdLogger: Logger = LoggerFactory.getLogger("stream-ids")
    for (s <- streamIds){streamIdLogger.info(s"$s")}

    logger.info("Done.")
  }
}

