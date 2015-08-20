package nl.knaw.dans.easy.lib

import java.io.File

import com.github.tomakehurst.wiremock.client.WireMock._
import nl.knaw.dans.easy.lib.CustomMatchers._
import org.apache.commons.io.FileUtils._
import org.apache.commons.io.IOUtils

import scala.util.Success

class NiceFedoraRestApiSpec extends UnitSpec {

  "constructor" should "have values from the properties files" in {

    new NiceFedoraRestApi(cfg) should have(

      'baseUrl("http://invalidlocalhost:18080/fedora"), // this value ignores online tests
      'user("easy_webui"),
      'requestInterval(50)
    )
  }

  "getXml of an invalid object id" should "result in a 'HTTP/1.1 500 Internal Server Error': PID delimiter (:) is missing" in {

    givenThat(get(urlEqualTo("/fedora/objects/abc?format=xml&")).willReturn(aResponse
      .withStatus(500)
      .withBody("javax.ws.rs.WebApplicationException: org.fcrepo.server.errors.MalformedPidException: PID delimiter (:) is missing.")))

    fedora.getXml("objects/abc") should failWith(a[Exception], "HTTP-result code: 500")
  }

  "getAllStreamIds of an invalid object id" should "return an empty collection and log an error" in {

    // TODO intercept error logging to assert: "could not get stream-ids of objects/a:c"
    fedora.getAllStreamIds("objects/a:b").length shouldBe 0
  }

  "getStreamIds of an invalid object id" should "return an empty collection and log an error" in {

    // TODO intercept error logging to assert: "could not get stream-ids of objects/a:c"
    fedora.getStreamIds("objects/a:b", "M").length shouldBe 0
  }

  "openStream" should "succeed" in {

    givenThat(get(urlEqualTo("/fedora/objects/easy-file:1/datastreams/EASY_FILE/content")).willReturn(aResponse
      .withStatus(200)
      .withBody("some content")))
    IOUtils.readLines( fedora.openDataStream("easy-file:1","EASY_FILE")) should contain ("some content")
  }

  "getAllStreamIDs" should "return 4 IDs" in {

    givenThat(get(urlEqualTo("/fedora/objects/easy-file:1/datastreams?format=xml&")).willReturn(aResponse
      .withStatus(200)
      .withBody(readFileToString(new File("src/test/resources/file1/datastreams.xml")))))
    fedora.getAllStreamIds("easy-file:1") should equal(Array("DC", "EASY_FILE", "EASY_FILE_METADATA", "RELS-EXT").toList)
  }

  "getStreamIds with control group M" should "return 1 ID" in {

    givenThat(get(urlEqualTo("/fedora/objects/easy-file:1/objectXML?format=xml&")).willReturn(aResponse
      .withStatus(200)
      .withBody(readFileToString(new File("src/test/resources/file1/fo.xml")))))
    fedora.getStreamIds("easy-file:1", "M") should equal(Array("EASY_FILE").toList)
  }

  "put EASY_FILE checksum" should "return the checksum value" in {

    givenThat(put(urlEqualTo("/fedora/objects/easy-file:1/datastreams/EASY_FILE?format=xml&checksumType=SHA-1")).willReturn(aResponse
      .withStatus(200)
      .withBody(readFileToString(new File("src/test/resources/file1/EASY_FILE-checksum.xml")))))
    val result = fedora.put("objects/easy-file:1/datastreams/EASY_FILE?format=xml&checksumType=SHA-1")
    result shouldBe a[Success[_]]
    result.get should include("23b1304789895fc772e950d33ef9bf8a1aa71517")
  }

  "put DC checksum" should "fail with InputStream cannot be null" in {

    givenThat(put(urlEqualTo("/fedora/objects/easy-file:1/datastreams/DC?format=xml&checksumType=SHA-1")).willReturn(aResponse
      .withStatus(400)
      .withBody("InputStream cannot be null")))
    val result = fedora.put("objects/easy-file:1/datastreams/DC?format=xml&checksumType=SHA-1")
    result should failWith(a[Exception], "InputStream cannot be null")
  }
}
