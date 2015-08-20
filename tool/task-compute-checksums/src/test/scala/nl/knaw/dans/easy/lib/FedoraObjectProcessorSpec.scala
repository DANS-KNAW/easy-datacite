package nl.knaw.dans.easy.lib

import com.github.tomakehurst.wiremock.client.WireMock._
import nl.knaw.dans.easy.lib.CustomMatchers._

import scala.util.{Failure, Success, Try}

class FedoraObjectProcessorSpec extends UnitSpec {

  ignore should "log an error" in{
    var count = 0
    def callback(id: String): Try[Unit] = {Success(Unit)}

    // TODO intercept logging to assert it
    new FedoraObjectProcessor(objectIds="@:#", callback = callback).run
  }

  "it" should "test a non-numeric chunksize in the configuration" in {
    // TODO alternative configuration for complete code coverage
  }

  "run" should "call callback for each ID in the chunks" in{
    var count = 0
    def callback(id: String): Try[Unit] = {
      count += 1
      Success(Unit)
    }
    expectTwoChunks()

    new FedoraObjectProcessor(objectIds="*:*", callback = callback, fedoraBaseUrl = fedora.baseUrl, chunkSize = 50).run
    count shouldBe 83
  }

  it should "call callback just once when it fails" in{
    var count = 0
    def callback(id: String): Try[Unit] = {
      count += 1
      Failure(new scala.Exception)
    }
    expectTwoChunks()

    new FedoraObjectProcessor(objectIds="*:*", callback = callback, fedoraBaseUrl = fedora.baseUrl, chunkSize = 50).run
    count shouldBe 1
  }

  it should "return an error" in{
    var count = 0
    def callback(id: String): Try[Unit] = {
      count += 1
      Failure(new scala.Exception)
    }
    givenThat(get(urlEqualTo("/fedora/objects?resultFormat=xml&pid=true&query=pid~*:*&maxResults=50")).willReturn(aResponse
      .withStatus(500)
      .withBody("some error")))

    new FedoraObjectProcessor(objectIds="*:*", callback = callback, fedoraBaseUrl = fedora.baseUrl, chunkSize = 50)
      .run should failWith(a[Exception],"500 Internal Server Error")
  }
}
