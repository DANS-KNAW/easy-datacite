/**
  * Copyright (C) 2016 DANS - Data Archiving and Networked Services (info@dans.knaw.nl)
  *
  * Licensed under the Apache License, Version 2.0 (the "License");
  * you may not use this file except in compliance with the License.
  * You may obtain a copy of the License at
  *
  *         http://www.apache.org/licenses/LICENSE-2.0
  *
  * Unless required by applicable law or agreed to in writing, software
  * distributed under the License is distributed on an "AS IS" BASIS,
  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  * See the License for the specific language governing permissions and
  * limitations under the License.
  */
package nl.knaw.dans.easy.task

import java.io.{File, PrintWriter}
import java.net.URL
import java.util.Properties

import com.yourmediashelf.fedora.client.request.FedoraRequest
import com.yourmediashelf.fedora.client.{FedoraClient, FedoraCredentials}
import org.rogach.scallop.ScallopConf

class CommandLineOptions(args: Array[String]) extends ScallopConf(args) {

  appendDefaultToDescription = true
  editBuilder(_.setHelpWidth(110))

  printedName = "easy-add-geo-coordinates"
  version(s"$printedName ${Version()}")
  banner(s"""
            |Add geographic coordinates (point or box in RD) to Datasets
            |
            |The csv input file must contain the following columns:
            | pid, x, y, minx, maxx, miny, maxy
            |
            |Options:
            |""".stripMargin)

  val doUpdate = opt[Boolean]("doUpdate", default = Some(false),
    descr = "Without this argument no changes are made to the repository, the default is a test mode that logs the intended changes")
  val url = opt[String]("url", noshort = true, default = Some("http://localhost:8080/fedora"),
    descr = "Base url for the fedora repository")
  val password = opt[String]("password",
    descr = "Password for fedora repository, if omitted provide it on stdin")
  val username = opt[String]("username",
    descr = "Username for fedora repository, if omitted provide it on stdin")
  val output = opt[String]("output", default = Some("changed_pids.txt"),
    descr = "Name of the file where the changed pids are written")

  val csvFilename = opt[String]("csv-file", required = true,
    descr = "The name of file that contains the pids and parameters needed to fix the object with that pid.")
  val msg = opt[String]("msg",
    descr = "The message that ends up in the audit trail of the Fedora Object giving a justification for the update)")

  footer("")
  verify()
}

object CommandLineOptions {
  def parse(args: Array[String]): Settings = {
    val opts = new CommandLineOptions(args)

    val url = new URL(opts.url())
    val username = opts.username.toOption.getOrElse(askUsername(url.toString))
    val password = opts.password.toOption.getOrElse(askPassword(username,url.toString))

    FedoraRequest.setDefaultClient(new FedoraClient(new FedoraCredentials(url, username, password)))

    val testMode = !opts.doUpdate()
    val updater = FedoraStreams(testMode)
    val changedPids = opts.output()
    val writer = new PrintWriter(new File(changedPids))
    val csvFilename = opts.csvFilename.apply()
    val justificationMsg = s"automated object fixing: ${opts.printedName} ${Version()}" + "; " + opts.msg.toOption.getOrElse("")

    Settings(testMode, updater, writer, csvFilename, justificationMsg, username, url, changedPids)
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

object Version {
  def apply(): String = {
    val props = new Properties()
    props.load(getClass.getResourceAsStream("/Version.properties"))
    props.getProperty("application.version")
  }
}
