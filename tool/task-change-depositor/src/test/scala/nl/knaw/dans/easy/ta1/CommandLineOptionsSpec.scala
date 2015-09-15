package nl.knaw.dans.easy.ta1

import java.io.ByteArrayOutputStream
import com.yourmediashelf.fedora.client.request.FedoraRequest
import scala.runtime.BoxedUnit

class CommandLineOptionsSpec extends UnitSpec {


  "parse" should "set default fedora client" in {

    // ignore if not executed as the very first test on parsing command line arguments
    assume(!FedoraRequest.isDefaultClientSet,"because previous tests did set the default fedora client")

    new CommandLineOptions(Array(
      "--username", "X",
      "--password", "X",
      "--fcrepo-server", "http://X"
    )).parse()
    FedoraRequest.isDefaultClientSet shouldBe true
  }

  it should "prompt for username with the specified URL" in {

    // ignore if the prompt requires manual interaction
    assume(System.console() == null)

    val mockedOut = new ByteArrayOutputStream()
    Console.withOut(mockedOut) {
      a[NullPointerException] should be thrownBy
      new CommandLineOptions(Array(
      "-f", "http://X"
      )).parse()
    }
    mockedOut.toString shouldBe "Username for http://X : "
  }

  it should "prompt for password the specified URL" in {

    // ignore if the prompt requires manual interaction
    assume(System.console() == null)

    val mockedOut = new ByteArrayOutputStream()
    Console.withOut(mockedOut) {
      a[NullPointerException] should be thrownBy
        new CommandLineOptions(Array(
          "-uX", "-f", "http://X"
        )).parse()
    }
    mockedOut.toString shouldBe "Password for X on http://X : "
  }

  it should "prompt for username with the default URL" in {

    assume(System.console() == null)
    val mockedOut = new ByteArrayOutputStream()
    Console.withOut(mockedOut) {
      a[NullPointerException] should be thrownBy
        new CommandLineOptions(Array()).parse()
    }
    mockedOut.toString shouldBe "Username for http://localhost:8080/fedora : "
  }

  "printHelp" should "print help info (for visual inspection)" in {
    // note that the banner should be added by a subclass
    new CommandLineOptions(Array(
      "-f", "http://X"
    )).printHelp() shouldBe a[BoxedUnit]
  }

  "updater" should "be in test mode" in {
    new CommandLineOptions(Array(
      "-uX", "-pX", "-f", "http://X"
    )).parse().toString should include ("test mode")
  }

  it should "be in update mode" in {
    new CommandLineOptions(Array(
      "-a", "-uX", "-pX", "-f", "http://X"
    )).parse().toString should include ("update mode")
  }
}
