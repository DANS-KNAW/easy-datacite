package nl.knaw.dans.easy.task

import java.io.File

import com.github.tomakehurst.wiremock.client.WireMock._
import nl.knaw.dans.easy.lib.FedoraObjectProcessor
import org.apache.commons.io.FileUtils._
import org.slf4j.helpers.SubstituteLogger

import scala.util.Success

class ChecksumCalculatorSpec extends UnitSpec {

  "iterating with callback" should "succeed" in {

    givenThat(get(urlEqualTo("/fedora/objects?resultFormat=xml&pid=true&query=pid~*:*&maxResults=50")).willReturn(aResponse
      .withStatus(200)
      .withBody("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
      "<result xmlns=\"http://www.fedora.info/definitions/1/0/types/\">\n" +
      "    <resultList><objectFields><pid>easy-file:1</pid></objectFields></resultList>\n" +
      "</result>\n")))
    givenThat(get(urlEqualTo("/fedora/objects/easy-file:1/objectXML?format=xml&")).willReturn(aResponse
      .withStatus(200)
      .withBody(readFileToString(new File("src/test/resources/file1/fo.xml")))))
    givenThat(get(urlEqualTo("/fedora/objects/easy-file:1/datastreams/EASY_FILE/content")).willReturn(aResponse
      .withStatus(200)
      .withBody(readFileToString(new File("src/test/resources/file1/content.xml")))))

    val sb = new StringBuilder()
    val cc = new ChecksumCalculator(
      fedora = fedora,
      dataLogger = new SubstituteLogger("mock"){override def info(s: String) = {sb.append(s"$s\n")}},
      logger = new SubstituteLogger("mock"){override def info(s: String) = {sb.append(s"$s\n")}}
    )
    new FedoraObjectProcessor(
      objectIds = "*:*",
      callback = cc.calculateStreamChecksums,
      fedoraBaseUrl = fedora.baseUrl,
      chunkSize = 50).run shouldBe a[Success[_]]

    // the rest is more interesting when replacing the wiremocked fedora with teasy/fedora
    val result = sb.toString()
    print(result)
    val fedoraIds = result.split("\n").map(_.split("\t")(1))
    val streamIds = result.split("\n").map(_.split("\t")(2))
    println("OBJECTS = " + fedoraIds.distinct.deep.toString())
    println("NAME SPACES = " + fedoraIds.map(_.replaceAll(":.*", "")).distinct.deep.toString())
    println("STREAMS = " + streamIds.distinct.deep.toString())

    // line by line, field by filed makes it easier to spot eventual problems
    for {line <- result.split("\n").map(_.toLowerCase)}{
      line should fullyMatch regex ".+\t.+:.+\t.+"
      line should fullyMatch regex "[0-9a-f]+\t.+:.+\t.+"
      line should fullyMatch regex ".+\t[-a-z0-9._]+:.+\t.+"
      line should fullyMatch regex ".+\t.+:[-a-z0-9._]+\t.+"
      line should fullyMatch regex ".+\t.+:.+\t[-a-z0-9._]+"
    }
  }

  ignore should "run" in {
    // requires JVM argument for .config, see calculate.sh
    ChecksumCalculator.main(Array())
  }
}
