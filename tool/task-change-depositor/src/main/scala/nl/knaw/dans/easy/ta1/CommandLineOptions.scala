package nl.knaw.dans.easy.ta1

import java.net.URL

import com.yourmediashelf.fedora.client.request.FedoraRequest
import com.yourmediashelf.fedora.client.{FedoraClient, FedoraCredentials}
import org.rogach.scallop.ScallopConf
import org.slf4j.LoggerFactory

class CommandLineOptions(args: Array[String]) extends ScallopConf(args) {

  footer("")
  val applyUpdates = opt[Boolean]("apply-updates", short = 'a', default = Some(false),
    descr = "Without this argument no changes are made to the repository, " +
      "the default is a test mode that logs the intended changes")

  val password = opt[String]("fcrepo-password", short = 'p',
    descr = "The password for fedora repository, if omitted provide it on stdin")
  val url = opt[URL]("fcrepo-server", short = 'f', default = Some(new URL("http://localhost:8080/fedora")),
    descr = "Base url for the fedora repository")
  val username = opt[String]("fcrepo-user", short = 'u',
    descr = "The username for fedora repository, if omitted provide it on stdin")


  /**
   * Parses the command line arguments to connect with a fedora client.
   *
   * @return a [[nl.knaw.dans.easy.ta1.Updater]] in the test/update mode
   *         as specified with the command line arguments
   */
  def parse(): Updater = {

    val urlValue: URL = url.apply()
    val usernameValue: String = username.get.getOrElse(CommandLineOptions.askUsername(urlValue.toString))
    val passwordValue: String = password.get.getOrElse(CommandLineOptions.askPassword(usernameValue, urlValue.toString))
    FedoraRequest.setDefaultClient(new FedoraClient(new FedoraCredentials(urlValue, usernameValue, passwordValue)))

    val testModeValue = !applyUpdates.apply()
    CommandLineOptions.log.info(s"Settings: test=$testModeValue, fedora-user=$usernameValue, fedora-baseurl=$urlValue")

    Updater.get(testModeValue)
  }
}

object CommandLineOptions {

  val log = LoggerFactory.getLogger(getClass)

  private def askUsername(url: String): String = {
    print(s"Username for $url : ")
    System.console().readLine()
  }

  private def askPassword(user: String, url: String): String = {
    print(s"Password for $user on $url : ")
    System.console.readPassword().mkString
  }
}
