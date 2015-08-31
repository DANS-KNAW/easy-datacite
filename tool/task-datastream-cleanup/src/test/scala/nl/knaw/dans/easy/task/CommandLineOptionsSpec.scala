package nl.knaw.dans.easy.task

import java.io.{ByteArrayOutputStream, PrintStream}

import com.sun.jersey.api.client.ClientHandlerException
import com.yourmediashelf.fedora.client.request.FedoraRequest

import scala.runtime.BoxedUnit
import scala.xml.{SAXParseException, XML}

class CommandLineOptionsSpec extends UnitSpec {

  val datasetContainer = <icmd:item-container-md xmlns:icmd="http://easy.dans.knaw.nl/easy/item-container-md/" version="0.1">
      <sid>easy-dataset:2835</sid>
    </icmd:item-container-md>

  val folderContainer = <icmd:item-container-md xmlns:icmd="http://easy.dans.knaw.nl/easy/item-container-md/" version="0.1">
      <sid>easy-folder:2</sid>
      <name>img</name>
      <parentSid>easy-folder:1</parentSid>
      <datasetSid>easy-dataset:1</datasetSid>
      <path>original/img</path>
    </icmd:item-container-md>

  "parse" should "set default fedora client" in {

    // NB: applies only when executed as the very first test
    FedoraRequest.isDefaultClientSet shouldBe false

    CommandLineOptions.parse(Array(
    "--username", "",
    "--password", "",
    "--url", "http://X",
    "--streamId", "EASY_ITEM_CONTAINER_MD",
    "--objectIds", "easy-dataset:*", "easy-f*:*",
    "--nodesToRemove", "sid", "parentSid", "datasetSid"
    )).toString shouldBe "Settings: test=true, removing (sid, parentSid, datasetSid) from EASY_ITEM_CONTAINER_MD of (easy-dataset:*, easy-f*:*) , fedora-user=, fedora-baseurl=http://X"
    FedoraRequest.isDefaultClientSet shouldBe true
  }

  it should "prompt for username with URL" in {
    assume(System.console() == null)
    val mockedOut = new ByteArrayOutputStream()
    Console.withOut(mockedOut) {
      a[NullPointerException] should be thrownBy CommandLineOptions.parse(Array(
        "--url", "http://X", "-sX", "-oX", "-nX"
      ))
    }
    mockedOut.toString shouldBe "Username for http://X: "
  }

  it should "prompt for password with user name and URL" in {

    assume(System.console() == null)
    val mockedOut = new ByteArrayOutputStream()
    Console.withOut(mockedOut) {
      a[NullPointerException] should be thrownBy CommandLineOptions.parse(Array(
      "-uX", "--url", "http://X", "-sX", "-oX", "-nX"
      ))
    }
    mockedOut.toString shouldBe "Password for X on http://X: "
  }

  "printHelp" should "print help info (for visual inspection)" in {

    new CommandLineOptions(Array(
    "--url", "http://X", "-sX", "-oX", "-nX"
    )).printHelp() shouldBe a[BoxedUnit]
  }

  "created transformer" should "remove sid from dataset container" in {
    CommandLineOptions.parse(Array(
      "-uX", "-pX", "--url", "http://X", "-sX", "-oX", "-nsid"
    )).transformer.transform(datasetContainer).toString() should not include "2835"
  }

  it should "remove sids from folder container" in {
    val s = CommandLineOptions.parse(Array(
      "-uX", "-pX", "--url", "http://X", "-sX", "-oX", "-n", "sid", "parentSid", "datasetSid"
    )).transformer.transform(folderContainer).toString()
    s should not include "easy-dataset:1"
    s should not include "easy-folder:1"
    s should not include "easy-folder:2"
    s should include ("original/img")
    s should include ("img")
  }

  it should "choke on garbage" in {
    the[SAXParseException] thrownBy CommandLineOptions.parse(Array(
      "-uX", "-pX", "--url", "http://X", "-sX", "-oX", "-nX"
    )).transformer.transform(XML.loadString(
      "just some non xml garbage"
    )) should have message "Content is not allowed in prolog."
  }

  "created updater" should "fail with invalid url" in {
    implicit val settings: Settings = CommandLineOptions.parse(Array(
      "--doUpdate", "-uX", "-pX", "--url", "http://X", "-sX", "-oX", "-nX"
    ))
    the[ClientHandlerException] thrownBy
      settings.updater.updateDatastream(
        "x:1", "yyy"
      ) should have message "java.net.UnknownHostException: X"
  }

  it should "throw nothing in the default test mode" in {
    implicit val settings: Settings = CommandLineOptions.parse(Array(
      "-uX", "-pX", "--url", "http://X", "-sX", "-oX", "-nX"
    ))
    settings.updater.updateDatastream("x:1", "yyy")
    /* TODO intercept logging to assert correct call, for now only by visual inspection

        somehow override log in updater:

        val sb = new StringBuilder()
        new SubstituteLogger(StreamUpdater.getClass.getName){override def info(s: String) = {sb.append(s"$s\n")}}
        ...test...
        sb.toString shouldBe "test mode: skipped update of x:1/X"

        or see http://stackoverflow.com/questions/3803184/setting-logback-appender-path-programmatically
     */
  }
}
