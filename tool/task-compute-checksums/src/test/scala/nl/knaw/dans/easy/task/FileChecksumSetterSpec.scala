package nl.knaw.dans.easy.task

import java.io.File

import com.github.tomakehurst.wiremock.client.WireMock._
import org.apache.commons.io.FileUtils._

class FileChecksumSetterSpec extends UnitSpec {

  "run" should "have no problem with a checksum that was already set" in {

    val (errors, infos, logger) = captureErrorsAndInfos
    givenThat(put(urlEqualTo("/fedora/objects/easy-file:1/datastreams/EASY_FILE?checksumType=SHA-1&ignoreContent=true&logMessage=Enabled%20checksum")).willReturn(aResponse
      .withStatus(200)
      .withBody(readFileToString(new File("src/test/resources/file1/EASY_FILE-checksum.xml")))))

    val cs = "23b1304789895fc772e950d33ef9bf8a1aa71517"
    new FileChecksumSetter(logger,fedora).run(toInputStream(s"$cs\teasy-file:1\tEASY_FILE"))

    println(s"\n<<<${errors.toString()}>>>")
    println(s"[[[${infos.toString()}]]]")
    errors.toString() shouldBe ""
    infos.toString() shouldBe "EASY-913B set checksums on fedora object streams not embedded in FOXML\n" +
      s"SET VALUE: $cs PRE-CALCULATED: $cs, easy-file:1, EASY_FILE\n" +
      s"Done.\n"
  }
}
