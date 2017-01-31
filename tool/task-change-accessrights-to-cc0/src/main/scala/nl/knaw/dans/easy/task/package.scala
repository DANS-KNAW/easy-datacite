package nl.knaw.dans.easy

import java.io.{ File, InputStream, PrintWriter }
import java.nio.charset.Charset
import javax.naming.ldap.LdapContext

import com.yourmediashelf.fedora.client.request.RiSearch
import com.yourmediashelf.fedora.client.{ FedoraClient, FedoraClientException, FedoraCredentials }
import org.apache.commons.csv.{ CSVFormat, CSVParser }
import org.apache.commons.io.{ Charsets, IOUtils }
import org.slf4j.LoggerFactory
import rx.lang.scala.Observable

import scala.io.Source
import scala.xml.{ Elem, XML }

package object task {

  type DatasetPid = String
  type FilePid = String
  type Urn = String

  case class Settings(testMode: Boolean,
                      inputFile: File,
                      fedoraCredentials: FedoraCredentials,
                      ldapContext: LdapContext,
                      changedPids: PrintWriter,
                      changedFiles: PrintWriter) {
    override def toString =
      s"Settings: testMode=$testMode, inputFile=$inputFile, fedora-user=${fedoraCredentials.getUsername}, fedora-baseurl=${fedoraCredentials.getBaseUrl}"
  }
  case class Record(year: Int, rightHolder: String, urn: Urn, title: String, accessRight: String)

  val encoding = Charsets.UTF_8
  val groupAccess = "GROUP_ACCESS"
  val accessrightRegisteredUserToBeChanged = "KNOWN"
  val accessrightArchaeologyToBeChanged = "RESTRICTED_GROUP"
  val newAccessright = "ANONYMOUS"

  val visibilityDefaultFileMetadata = "anonymous"

  val fileStreamId = "EASY_FILE_METADATA"

  val filename = "name"
  val accessibleToFileMetadata = "accessibleTo"
  val visibleToFileMetadata = "visibleTo"

  implicit class FileExtensions(val file: File) extends AnyVal {
    def parse(encoding: Charset = encoding, format: CSVFormat = CSVFormat.RFC4180): CSVParser = {
      CSVParser.parse(file, encoding, format)
    }
  }

  implicit class InputStreamExtensions(val stream: InputStream) extends AnyVal {
    def loadXML: Elem = XML.load(stream)

    def closeQuietly(): Unit = IOUtils.closeQuietly(stream)
  }

  def getXml(streamId: String)(pid: FilePid): Observable[Elem] = {
    val log = LoggerFactory.getLogger(getClass)

    lazy val stream = FedoraClient.getDatastreamDissemination(pid, streamId)
      .execute()
      .getEntityInputStream

    Observable.using(stream)(s => Observable.just(s.loadXML), _.closeQuietly())
      .onErrorResumeNext {
        case e: FedoraClientException if e.getStatus == 404 =>
          Observable.empty
            .doOnCompleted(log.error(s"Not found: $pid/$streamId", e))
        case e => Observable.error(e)
      }
      .doOnError(e => log.error(s"can't load: $pid/$streamId - ${e.getMessage}", e))
  }

  def getFileIdentifiers(datasetPid: DatasetPid): Observable[FilePid] = {
    lazy val query = "PREFIX dans: <http://dans.knaw.nl/ontologies/relations#> " +
      "PREFIX fmodel: <info:fedora/fedora-system:def/model#> " +
      s"SELECT ?s WHERE {?s dans:isSubordinateTo <info:fedora/$datasetPid> . ?s fmodel:hasModel <info:fedora/easy-model:EDM1FILE>}"
    lazy val stream = new RiSearch(query).lang("sparql").format("csv").execute().getEntityInputStream

    Observable.using(stream)(s => {
      Observable.from(Source.fromInputStream(s).getLines().toIterable)
        .drop(1)
        .map(_.split("/").last)
    }, _.closeQuietly())
  }
}
