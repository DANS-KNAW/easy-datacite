package nl.knaw.dans.easy.task

import java.io.File

import com.github.tomakehurst.wiremock.client.WireMock._
import org.apache.commons.io.FileUtils._

class ChecksumVerifierSpec extends UnitSpec {

  "ChecksumVerifier" should "log a checksum mismatch" in {
    val (logged, logger) = captureError
    givenThat(get(urlEqualTo("/fedora/objects/easy-file:1/datastreams/EASY_FILE?format=xml&")).willReturn(aResponse
      .withStatus(200)
      .withBody(readFileToString(new File("src/test/resources/file1/EASY_FILE-checksum.xml")))))

    new ChecksumVerifier(logger,fedora).run(toInputStream(
      "e3d81f927ccfde88f4dc0a713110fb7d8a6f2d26\teasy-file:1\tEASY_FILE\n"))

    println("\n"+logged.toString())
    logged.toString() should include ("easy-file:1, EASY_FILE")
    logged.toString() should include ("checksum mismatch")
  }

  it should "log a checksum match" in {
    val (logged, logger) = captureInfo
    givenThat(get(urlEqualTo("/fedora/objects/easy-file:1/datastreams/EASY_FILE?format=xml&")).willReturn(aResponse
      .withStatus(200)
      .withBody(readFileToString(new File("src/test/resources/file1/EASY_FILE-checksum.xml")))))

    new ChecksumVerifier(logger,fedora).run(toInputStream(
      "23b1304789895fc772e950d33ef9bf8a1aa71517\teasy-file:1\tEASY_FILE\n"))

    println("\n"+logged.toString())
    logged.toString() should include ("easy-file:1, EASY_FILE")
    logged.toString() should include ("checksum OK")
  }
}


