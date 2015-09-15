package nl.knaw.dans.easy.ta1

import scala.runtime.BoxedUnit

class MainSpec extends UnitSpec {

  private val settings = Main.getSettings(Array(
    "-uX", "-pX", "-f", "http://X", "-oEmiliealsarchivaris", "-nY" ,"-qZ"
  ))

  "printHelp" should "print help info (for visual inspection)" in {

    new Main.Options(Array(
      "-uX", "-pX", "-f", "http://X", "-oX", "-nY" ,"-qZ"
    )).printHelp() shouldBe a[BoxedUnit]
  }

  "transform" should "change a published dataset" in {
    val content =
      <damd:administrative-md xmlns:damd="http://easy.dans.knaw.nl/easy/dataset-administrative-metadata/" version="0.1">
        <datasetState>PUBLISHED</datasetState>
        <depositorId>Emiliealsarchivaris</depositorId>
      </damd:administrative-md>

    settings.transform(
      "easy-dataset:1", content
    ).toString() should not include "Emiliealsarchivaris"
  }

  it should "not change a dataset in maintenance" in {
    val content =
      <damd:administrative-md xmlns:damd="http://easy.dans.knaw.nl/easy/dataset-administrative-metadata/" version="0.1">
        <datasetState>MAINTENANCE</datasetState>
        <depositorId>Emiliealsarchivaris</depositorId>
      </damd:administrative-md>

    settings.transform(
      "easy-dataset:1", content
    ).toString() shouldBe content.toString()
  }

  it should "not change other depositors" in {
    val content =
      <damd:administrative-md xmlns:damd="http://easy.dans.knaw.nl/easy/dataset-administrative-metadata/" version="0.1">
        <datasetState>PUBLISHED</datasetState>
        <depositorId>PietjePuk</depositorId>
      </damd:administrative-md>

    settings.transform(
      "easy-dataset:1", content
    ).toString() shouldBe content.toString()
  }
}
