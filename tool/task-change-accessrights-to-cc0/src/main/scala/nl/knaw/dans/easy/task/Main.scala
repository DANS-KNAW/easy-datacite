package nl.knaw.dans.easy.task

import java.io.File

import com.yourmediashelf.fedora.client.FedoraClient
import com.yourmediashelf.fedora.client.request.FedoraRequest
import nl.knaw.dans.easy.task.{ CommandLineOptions => cmd }
import org.apache.commons.csv.CSVRecord
import org.slf4j.LoggerFactory
import rx.lang.scala.Observable

import scala.collection.JavaConversions.{ asScalaBuffer, iterableAsScalaIterable }
import scala.xml.transform.{ RewriteRule, RuleTransformer }
import scala.xml.{ Elem, Node, Text }

object Main {

  private val log = LoggerFactory.getLogger(getClass)

  def main(args: Array[String]): Unit = {
    implicit val settings = cmd.parse(args)
    FedoraRequest.setDefaultClient(new FedoraClient(settings.fedoraCredentials))

    log.debug(s"Using the following settings: $settings")
    if (settings.testMode) log.info(s"Running in testmode; NO CHANGES WILL BE MADE")

    run
      .doOnTerminate {
        settings.changedPids.close()
        settings.changedFiles.close()
        log.info("Finished")
      }
      .doOnError(e => log.error(s"error detected: ${e.getMessage}"))
      .toBlocking
      .subscribe
  }

  def run(implicit settings: Settings): Observable[Unit] = {
    for {
      Record(_, _, urn, _, accessRight) <- parse(settings.inputFile)
      datasetPid <- getDatasetPidByURN(urn)
      _ = settings.changedPids.println(datasetPid)
      _ <- getFileMetadata(datasetPid)
        .publish(fileMetadataObs => {
          val changedVisible = fileMetadataObs.filter { case (_, xml) => visibilityIsAnonymous(xml) }
            .flatMap { case (filePid, xml) => changeFileAccessibility(filePid, xml, accessrightRegisteredUserToBeChanged) }
          val changedArchaeology = fileMetadataObs.filter(_ => accessRight == groupAccess)
            .flatMap { case (filePid, xml) => changeFileAccessibility(filePid, xml, accessrightArchaeologyToBeChanged) }

          changedVisible merge changedArchaeology
        })
    } yield ()
  }

  def parse(file: File): Observable[Record] = Observable.defer {
    Observable.from(file.parse())
      .filter(_.nonEmpty)
      .drop(1)
      .map(csvToRecord)
  }

  def csvToRecord(csvRecord: CSVRecord): Record = {
    val year = csvRecord.get(0).trim.toInt
    val rightHolder = csvRecord.get(1).trim
    val urn = csvRecord.get(2).trim
    val title = csvRecord.get(3).trim
    val accessRight = csvRecord.get(4).trim

    Record(year, rightHolder, urn, title, accessRight)
  }

  def getDatasetPidByURN(urn: Urn): Observable[DatasetPid] = Observable.defer {
    Observable.from(FedoraClient.findObjects().pid().query(s"identifier~$urn").execute().getPids)
      .filter(_ startsWith "easy-dataset:")
      // below: `first` rather than `take(1)`,
      // because it raises a `NoSuchElementException` if the stream is empty
      .first
  }

  def getFileMetadata(datasetPid: DatasetPid): Observable[(FilePid, Elem)] = {
    getFileIdentifiers(datasetPid).flatMapWith(getXml(fileStreamId))((filePid, xml) => (filePid, xml))
  }

  def changeFileAccessibility(filePid: FilePid, xml: Elem, oldAccessright: String)(implicit settings: Settings): Observable[Unit] = {
    Observable.just(transform(accessibleToFileMetadata, oldAccessright, newAccessright))
      .map(_.transform(xml))
      .flatMap {
        case `xml` => Observable.empty
        case newXml => Observable.defer {
          log.info(s"$filePid/$fileStreamId: $oldAccessright -> $newAccessright")
          StreamUpdater().updateDatastream(filePid, fileStreamId, newXml.toString())
            .doOnNext(_ => settings.changedFiles.println(s"    $filePid    ${ (newXml \\ filename).map(_.text).head }"))
        }
      }
  }

  def visibilityIsAnonymous(xml: Elem): Boolean = {
    (xml \\ visibleToFileMetadata).text.toLowerCase == visibilityDefaultFileMetadata
  }

  /** transform on file level */
  def transform(label: String, oldValue: String, newValue: String): RuleTransformer = {
    new RuleTransformer(new RewriteRule {
      override def transform(n: Node): Seq[Node] = n match {
        case Elem(prefix, `label`, attribs, scope, children)
          if children.head.toString contains oldValue =>
          Elem(prefix, label, attribs, scope, false, Text(newValue))
        case other => other
      }
    })
  }
}
