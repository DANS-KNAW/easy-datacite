package nl.knaw.dans.easy.task

import java.io.{File, PrintWriter}
import java.net.URL

import com.yourmediashelf.fedora.client.request.FedoraRequest
import com.yourmediashelf.fedora.client.{FedoraClient, FedoraCredentials}
import org.rogach.scallop.ScallopConf

class CommandLineOptions (args: Array[String]) extends ScallopConf(args){

  banner("""
           Task to correct the easy-discipline in a set of datasets.
           |
           |Options:
           |""".stripMargin)
  val doUpdate = opt[Boolean]("apply-updates", descr = "Without this argument no changes are made to the repository, the default is a test mode that logs the intended changes", default = Some(false))
  val url = opt[String]("url", noshort = true, descr = "Base url for the fedora repository", default = Some("http://localhost:8080/fedora"))
  val password = opt[String]("password", required = true, descr = "Password for fedora repository, if omitted provide it on stdin")
  val username = opt[String]("username", required = true, descr = "Username for fedora repository, if omitted provide it on stdin")
  val pidsfile = opt[String]("pids-file", required = true, descr = "The name of file that contains list of pids.")
  val olddisc = opt[String]("odis", short='o', required = true, descr = "The current displince that will be changed.")
  val newdisc = opt[String]("ndis", short='n', required = true, descr = "The new discipline.")
  val output = opt[String]("output", descr = "Name of the file where the changed pids are written", default = Some("changed_pids.txt"))
  footer("")
}

object CommandLineOptions {
  def parse(args: Array[String]): Settings = {
    val opts = new CommandLineOptions(args)

    val url: URL = new URL(opts.url())
    val username: String = opts.username.get.getOrElse(askUsername(url.toString))
    val password: String = opts.password.get.getOrElse(askPassword(username,url.toString))
    FedoraRequest.setDefaultClient(new FedoraClient(new FedoraCredentials(url, username, password)))

    val testMode = !opts.doUpdate()
    val pidsfile : String = opts.pidsfile.apply()
    val odis: String = opts.olddisc.apply()
    val ndis : String = opts.newdisc.apply()
    val updater = StreamUpdater.get(testMode)
    val output : String = opts.output()
    val writer = new PrintWriter(new File(output))

   Settings(testMode, pidsfile, odis, ndis, updater, writer)
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
