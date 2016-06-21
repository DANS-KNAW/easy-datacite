package nl.knaw.dans.easy.task

import java.net.URL

import com.yourmediashelf.fedora.client.request.FedoraRequest
import com.yourmediashelf.fedora.client.{FedoraClient, FedoraCredentials}
import org.rogach.scallop.{ScallopConf}

import scala.xml.transform.{RewriteRule, RuleTransformer}
import scala.xml.{Elem, Node, NodeSeq}

class CommandLineOptions(args: Array[String]) extends ScallopConf(args) {

  appendDefaultToDescription = true
  editBuilder(_.setHelpWidth(110))
  banner("""Task to remove xml tags from datastreams of specified objects in the DANS EASY Archive
           |
           |Options:
           |""".stripMargin)
  val doUpdate = opt[Boolean]("doUpdate", descr = "Without this argument no changes are made to the repository, the default is a test mode that logs the intended changes", default = Some(false))
  val nodesToRemove = opt[List[String]]("nodesToRemove", descr = "list of xml tags to remove from the datastream", required = true)
  val objectIds = opt[List[String]]("objectIds", descr = "list of fedora object IDs, for example 'easy-dataset:*, easy-f*:*'", required = true)
  val password = opt[String]("password", descr = "The password for fedora repository, if omitted provide it on stdin")
  val streamId = opt[String]("streamId", descr = "The datastream to clean up", required = true)
  val url = opt[URL]("url", noshort = true, descr = "Base url for the fedora repository", default = Some(new URL("http://localhost:8080/fedora")))
  val username = opt[String]("username", descr = "The username for fedora repository, if omitted provide it on stdin")
  footer("")
  verify()
}

object CommandLineOptions {

  def parse(args: Array[String]): Settings = {
    val opts = new CommandLineOptions(args)

    val url: URL = opts.url.apply()
    val username: String = opts.username.get.getOrElse(askUsername(url.toString))
    val password: String = opts.password.get.getOrElse(askPassword(username,url.toString))
    FedoraRequest.setDefaultClient(new FedoraClient(new FedoraCredentials(url, username, password)))

    val streamId = opts.streamId.apply()
    val objectIdPatterns = opts.objectIds.apply()

    val testMode = !opts.doUpdate.apply()
    val updater = StreamUpdater.get(testMode)

    val nodesToRemove: List[String] = opts.nodesToRemove.apply()
    val transformer = new RuleTransformer(new RewriteRule {
      override def transform(n: Node): NodeSeq = n match {
        case e: Elem if nodesToRemove.contains(e.label) => NodeSeq.Empty
        case _ => n
      }
    })

    new Settings(streamId, objectIdPatterns, updater, transformer) {
      override def toString: String =
        s"Settings: test=$testMode, removing ${toStr(nodesToRemove)} from $streamId of ${toStr(idPatterns)} , fedora-user=$username, fedora-baseurl=$url"
    }
  }

  def toStr(a: List[String]): String = a.toArray.deep.toString().replace("Array", "")

  def askUsername(url: String): String = {
    print(s"Username for $url: ")
    System.console().readLine()
  }

  def askPassword(user: String, url: String): String = {
    print(s"Password for $user on $url: ")
    System.console.readPassword().mkString
  }
}
