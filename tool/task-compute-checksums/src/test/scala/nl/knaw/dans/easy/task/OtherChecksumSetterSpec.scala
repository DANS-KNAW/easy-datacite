package nl.knaw.dans.easy.task

import java.io.{File, ByteArrayInputStream}

import com.github.tomakehurst.wiremock.client.WireMock._
import nl.knaw.dans.easy.lib.FedoraObjectProcessor
import org.apache.commons.io.FileUtils._

class OtherChecksumSetterSpec extends UnitSpec {

  "run" should "have no problem with a checksum that was already set" in {
    val (errors, infos, logger) = captureErrorsAndInfos
    givenThat(get(urlEqualTo("/fedora/objects?resultFormat=xml&pid=true&query=pid~*:*&maxResults=500")).willReturn(aResponse
      .withStatus(200)
      .withBody("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
      "<result xmlns=\"http://www.fedora.info/definitions/1/0/types/\">\n" +
      "    <resultList><objectFields><pid>easy-file:1</pid></objectFields></resultList>\n" +
      "</result>\n")))
    givenThat(get(urlEqualTo("/fedora/objects/easy-file:1/datastreams?format=xml&")).willReturn(aResponse
      .withStatus(200)
      .withBody(readFileToString(new File("src/test/resources/file1/datastreams.xml")))))
    givenThat(put(urlEqualTo("/fedora/objects/easy-file:1/datastreams/EASY_FILE_METADATA?checksumType=SHA-1&format=xml&ignoreContent=true&logMessage=Enabled%20checksum")).willReturn(aResponse
      .withStatus(200)
      .withBody("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
      "<datastreamProfile pid=\"easy-file:1\" dsID=\"EASY_FILE_METADATA\">\n" +
      "    <dsChecksumType>SHA-1</dsChecksumType>\n" +
      "    <dsChecksum>12345</dsChecksum>\n" +
      "</datastreamProfile>")))
    givenThat(put(urlEqualTo("/fedora/objects/easy-file:1/datastreams/RELS-EXT?checksumType=SHA-1&format=xml&ignoreContent=true&logMessage=Enabled%20checksum")).willReturn(aResponse
      .withStatus(200)
      .withBody("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
      "<datastreamProfile pid=\"easy-file:1\" dsID=\"EASY_FILE_METADATA\">\n" +
      "    <dsChecksumType>SHA-1</dsChecksumType>\n" +
      "    <dsChecksum>67890</dsChecksum>\n" +
      "</datastreamProfile>")))

    // DC streams causes a 400-Bad-request: 'InputStream cannot be null'
    // EASY-FILE is an external data stream
    val skippedStreams: ByteArrayInputStream = toInputStream("EASY_FILE\nDC")

    val objectProcessor = new FedoraObjectProcessor(
      objectIds = "*:*",
      callback = new OtherChecksumSetter(fedora, logger, skippedStreams).set,
      fedoraBaseUrl = fedora.baseUrl
    ).run

    println(s"\n<<<${errors.toString()}>>>")
    println(s"[[[${infos.toString()}]]]")
    errors.toString() shouldBe ""
    infos.toString() shouldBe "fetching unique lines from stdin\n" +
      "completed reading stdin\n" +
      "12345 is checksum for EASY_FILE_METADATA of easy-file:1\n" +
      "67890 is checksum for RELS-EXT of easy-file:1\n"
  }
}
