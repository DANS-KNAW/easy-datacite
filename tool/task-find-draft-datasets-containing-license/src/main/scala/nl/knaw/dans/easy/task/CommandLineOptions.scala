package nl.knaw.dans.easy.task

import java.io.{File, PrintWriter}
import java.net.URL

import com.yourmediashelf.fedora.client.request.FedoraRequest
import com.yourmediashelf.fedora.client.{FedoraClient, FedoraCredentials}
import org.rogach.scallop.ScallopConf

class CommandLineOptions (args: Array[String]) extends ScallopConf(args){

  appendDefaultToDescription = true
  editBuilder(_.setHelpWidth(110))
  banner("""
           Task to find draft datasets containing license.
           |
           |Options:
           |""".stripMargin)
  val url = opt[String]("url", noshort = true, descr = "Base url for the fedora repository", default = Some("http://localhost:8080/fedora"))
  val password = opt[String]("password", descr = "Password for fedora repository, if omitted provide it on stdin")
  val username = opt[String]("username", descr = "Username for fedora repository, if omitted provide it on stdin")
  val output = opt[String]("output", descr = "Name of the file where the pids of draft datasets containing license are written", default = Some("draft_with_license_pids.txt"))
  footer("")
  verify()
}

object CommandLineOptions {
  def parse(args: Array[String]): Settings = {
    val opts = new CommandLineOptions(args)

    val url: URL = new URL(opts.url())
    val username: String = opts.username.get.getOrElse(askUsername(url.toString))
    val password: String = opts.password.get.getOrElse(askPassword(username,url.toString))
    FedoraRequest.setDefaultClient(new FedoraClient(new FedoraCredentials(url, username, password)))

    val draftWithLicensePids : String = opts.output()
    val writer = new PrintWriter(new File(draftWithLicensePids))

    new Settings(writer) {
      override def toString: String =
        s"Settings: fedora-user=$username, fedora-baseurl=$url, pids-file=$draftWithLicensePids"
    }
  }

  def askUsername(url: String): String = {
    print(s"Username for $url: ")
    System.console().readLine()
  }

  def askPassword(user: String, url: String): String = {
    print(s"Password for $user on $url: ")
    System.console.readPassword().mkString
  }
}
