package nl.knaw.dans.easy.lib

import java.io.File

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock
import com.github.tomakehurst.wiremock.client.WireMock._
import com.github.tomakehurst.wiremock.core.WireMockConfiguration._
import com.typesafe.config.ConfigFactory
import org.apache.commons.io.FileUtils._
import org.scalatest._

abstract class UnitSpec extends FlatSpec with Matchers with
OptionValues with Inside with Inspectors with OneInstancePerTest {

  val cfg = ConfigFactory.parseFile(new File("src/main/assembly/dist/cfg/application.conf"))
  val fedora: NiceFedoraRestApi = new NiceFedoraRestApi("http://localhost:8080/fedora", "", "", 0, 1)

  val wireMockServer = UnitSpec.wireMockServer
  WireMock.reset()

  def expectTwoChunks(): Unit = {
    givenThat(get(urlEqualTo("/fedora/objects?resultFormat=xml&pid=true&query=pid~*:*&maxResults=50")).willReturn(aResponse
      .withStatus(200)
      .withBody(readFileToString(new File("src/test/resources/object-ids/chunk1.xml")))))
    givenThat(get(urlEqualTo("/fedora/objects?resultFormat=xml&pid=true&query=pid~*:*&maxResults=50&sessionToken=004f101baaaa0177cacc366cd85cdf46")).willReturn(aResponse
      .withStatus(200)
      .withBody(readFileToString(new File("src/test/resources/object-ids/chunk2.xml")))))
  }
}

object UnitSpec {
  configureFor("localhost", 8080)
  val wireMockServer = new WireMockServer(wireMockConfig().port(8080))
  wireMockServer.start()
}
