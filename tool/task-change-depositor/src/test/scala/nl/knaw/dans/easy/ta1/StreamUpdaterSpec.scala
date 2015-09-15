package nl.knaw.dans.easy.ta1

import com.sun.jersey.api.client.ClientHandlerException

import scala.xml.Node


class StreamUpdaterSpec extends UnitSpec {

  // set fedora client
  new CommandLineOptions(Array("-uX", "-pX", "-f", "http://X")).parse()

  implicit val settings = new Settings(
    objectQueries = null,
    datastreamId = "X", // the only value used by the updater under test
    updater = null
  ) {
    override def transform(pid: String, n: Node): Seq[Node] = n
  }

  "updateDatastream" should "fail in update mode because of the invalid url" in {

    the[ClientHandlerException] thrownBy
      Updater.get(
        testMode = false
      ).updateDatastream(
          "x:1", "yyy"
        ) should have message "java.net.UnknownHostException: X"
  }

  it should "throw nothing in the test mode" in {

    Updater.get(
      testMode = true
    ).updateDatastream(
        "x:1", "yyy"
      )
    /* TODO intercept logging to assert correct call, for now only by visual inspection

        somehow override log in updater:

        val sb = new StringBuilder()
        new SubstituteLogger(Updater.getClass.getName){override def info(s: String) = {sb.append(s"$s\node")}}
        ...test...
        sb.toString shouldBe "test mode: skipped update of x:1/X"

        or see https://jsoftbiz.wordpress.com/2011/11/29/unit-testing-asserting-that-a-line-was-logged-by-logback/
     */
  }
}
