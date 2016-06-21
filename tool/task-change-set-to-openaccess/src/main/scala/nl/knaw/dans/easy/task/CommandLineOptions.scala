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
           Task to set dataset status of a given list of identifiers (urn's) from Open Access for Registered Users to Open Access
           |
           |Options:
           |""".stripMargin)
  val doUpdate = opt[Boolean]("doUpdate", descr = "Without this argument no changes are made to the repository, the default is a test mode that logs the intended changes", default = Some(false))
  val url = opt[String]("url", noshort = true, descr = "Base url for the fedora repository", default = Some("http://localhost:8080/fedora"))
  val password = opt[String]("password", descr = "Password for fedora repository, if omitted provide it on stdin")
  val username = opt[String]("username", descr = "Username for fedora repository, if omitted provide it on stdin")
  val output = opt[String]("output", descr = "Name of the file where the changed pids are written", default = Some("changed_pids.txt"))
  val output_2 = opt[String]("output_2", descr = "Name of the file with information about the changed dataset files", default = Some("changed_files.txt"))
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

    val testMode = !opts.doUpdate()
    val updater = StreamUpdater.get(testMode)
    val changedPids : String = opts.output()
    val changedFiles : String = opts.output_2()
    val writer = new PrintWriter(new File(changedPids))
    val writer_2 = new PrintWriter(new File(changedFiles))

    new Settings(testMode, updater, writer, writer_2) {
      override def toString: String =
        s"Settings: test=$testMode, fedora-user=$username, fedora-baseurl=$url, pids-file=$changedPids, changed files=$changedFiles"
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
