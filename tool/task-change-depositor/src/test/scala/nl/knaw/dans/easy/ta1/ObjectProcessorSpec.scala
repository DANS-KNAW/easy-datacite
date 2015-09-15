package nl.knaw.dans.easy.ta1

import com.github.tomakehurst.wiremock.client.WireMock._
import nl.knaw.dans.easy.ta1.ObjectProcessor.fixObject

import scala.xml.Node

class ObjectProcessorSpec extends UnitSpec {

  ignore should "not call the updater" in {
    implicit val settings = new Settings(
      updater = null,
      objectQueries = null,
      datastreamId = "AMD"
    ) {
      override def transform(pid: String, rootNode: Node): Seq[Node] = rootNode
    }

    println("hiep hiep")
    givenThat(get(urlEqualTo("/fedora/objects/easy-dataset:1/datastreams/AMD/content"))
      .willReturn(aResponse.withStatus(200).withBody(
      <damd:administrative-md
      xmlns:damd="http://easy.dans.knaw.nl/easy/dataset-administrative-metadata/"
      version="0.1">
        <datasetState>PUBLISHED</datasetState>
        <depositorId>PietjePuk</depositorId>
      </damd:administrative-md>
        .toString()
    )))
    println("hoera")
    /* FIXME the above results in: "Connection to http://localhost:8080 refused"
     * See also task-compute-checksums OtherChecksumSetterSpec
     * When copying one of those givenThat's here, it also fails
     */

    fixObject("easy-dataset:1")
  }
}
