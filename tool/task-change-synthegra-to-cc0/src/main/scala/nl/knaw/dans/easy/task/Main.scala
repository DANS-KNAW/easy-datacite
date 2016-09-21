package nl.knaw.dans.easy.task

import java.io.{ByteArrayOutputStream, File, OutputStream}

import com.yourmediashelf.fedora.client.FedoraClient
import com.yourmediashelf.fedora.client.request.FedoraRequest
import nl.knaw.dans.easy.task.{CommandLineOptions => cmd}
import org.apache.commons.csv.CSVRecord
import org.slf4j.LoggerFactory
import rx.lang.scala.Observable

import scala.collection.JavaConversions.{asScalaBuffer, iterableAsScalaIterable}
import scala.xml.transform.{RewriteRule, RuleTransformer}
import scala.xml.{Elem, Node, Text}

object Main {

  val log = LoggerFactory.getLogger(getClass)

  def main(args: Array[String]) {
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

  def run(implicit settings: Settings) = {
    for {
      Record(_, _, urn, _, accessRight) <- parse(settings.inputFile)
      pid <- getDatasetPidByURN(urn)
      _ <- changeFilesFromVisible(pid)
      _ <- if (accessRight == groupAccess) changeFilesFromArchaeology(pid) else Observable.just(())
    } yield ()
  }

  def parse(file: File): Observable[Record] = {
    Observable.from(file.parse())
      .filter(_.nonEmpty)
      .drop(1)
      .map(csvToRecord)
  }

  def csvToRecord(csvRecord: CSVRecord): Record = {
    val year = csvRecord.get(0).toInt
    val rightHolder = csvRecord.get(1)
    val urn = csvRecord.get(2)
    val title = csvRecord.get(3)
    val accessRight = csvRecord.get(4)

    Record(year, rightHolder, urn, title, accessRight)
  }

  def getDatasetPidByURN(urn: Urn): Observable[Pid] = {
    Observable.from(FedoraClient.findObjects().pid().query(s"identifier~$urn").execute().getPids)
      .filter(_ startsWith "easy-dataset:")
      .first
  }

  def getFileMetadata(filePid: Pid) = getXml(filePid, fileStreamId)

  def changeFilesFromArchaeology(datasetPid: Pid)(implicit settings: Settings) = {

    def changeFileRights(filePid: Pid)(implicit settings: Settings) = {
      (md: Elem) => Observable.just(transform(accessibleToFileMetadata, fileRightsArchaeologyToBeChanged, newFileRights).transform(md))
        .flatMap(newXml => Observable[Unit](subscriber => {
          if (newXml == md) subscriber.onCompleted()
          else {
            log.info(s"$filePid/$fileStreamId: $fileRightsArchaeologyToBeChanged -> $newFileRights")

            StreamUpdater().updateDatastream(filePid, fileStreamId, newXml.toString())
              .map(_ => (newXml \\ filename).map(_.text).head)
              .doOnNext(fileName => settings.changedFiles.println(s"    $filePid    $fileName"))
              .map(_ => ())
              .subscribe(subscriber)
          }
        }))
    }

    getFileIdentifiers(datasetPid)
      .flatMap(filePid => getFileMetadata(filePid)
        .flatMap(changeFileRights(filePid)))
  }

  def changeFilesFromVisible(datasetPid: Pid)(implicit settings: Settings) = {

    def changeFileRights(filePid: Pid)(implicit settings: Settings) = {
      (md: Elem) => Observable.just(transform(accessibleToFileMetadata, fileRightsToBeChanged, newFileRights).transform(md))
        .flatMap(newXml => Observable[Unit](subscriber => {
          if (newXml == md) subscriber.onCompleted()
          else {
            log.info(s"$filePid/$fileStreamId: $fileRightsToBeChanged -> $newFileRights")

            StreamUpdater().updateDatastream(filePid, fileStreamId, newXml.toString())
              .map(_ => (newXml \\ filename).map(_.text).head)
              .doOnNext(fileName => settings.changedFiles.println(s"    $filePid    $fileName"))
              .map(_ => ())
              .subscribe(subscriber)
          }
        }))
    }

    getFileIdentifiers(datasetPid)
      .flatMap(filePid => getFileMetadata(filePid)
        .filter(visibilityIsAnonymous)
        .flatMap(changeFileRights(filePid)))
  }

  def visibilityIsAnonymous(xml: Elem): Boolean = {
    (xml \\ visibleToFileMetadata).text.toLowerCase == visibilityDefaultFileMetadata
  }

  /** transform on dataset level */
  def transform(label: String, newValue: String) = {
    new RuleTransformer(new RewriteRule {
      override def transform(n: Node): Seq[Node] = n match {
        case Elem(prefix, `label`, attribs, scope, children) =>
          Elem(prefix, label, attribs, scope, false, Text(newValue))
        case other => other
      }
    })
  }

  /** transform on file level */
  def transform(label: String, oldValue: String, newValue: String) = {
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
