/**
 * Copyright (C) 2016-2017 DANS - Data Archiving and Networked Services (info@dans.knaw.nl)
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
package nl.knaw.dans.easy.task.add.newlicense

import java.io.File
import java.nio.file.{Files, Paths}
import java.util.Properties
import javax.naming.Context
import javax.naming.ldap.InitialLdapContext

import com.yourmediashelf.fedora.client.{FedoraClient, FedoraCredentials}
import org.apache.commons.configuration.PropertiesConfiguration
import org.rogach.scallop._
import org.slf4j.LoggerFactory


class CommandLineOptions(args: Array[String]) extends ScallopConf(args) {
  val log = LoggerFactory.getLogger(getClass)
  appendDefaultToDescription = true
  editBuilder(_.setHelpWidth(110))

  private val shouldBeFile = singleArgConverter(value =>
    new File(value) match {
      case f if f.isFile => f
      case _ => throw createException(s"'$value' is not a file")
    }
  )

  private val createIfNotExists = singleArgConverter(value =>
    new File(value) match {
      case f if f.isFile => f
      case f if !f.exists() || f.isDirectory => Files.createFile(Paths.get(value)).toFile
      case _ => throw createException(s"'$value' is not a file")
    }
  )

  private def createException(msg: String) = {
    log.error(msg)
    new IllegalArgumentException(msg)
  }

  printedName = "easy-task-add-new-license"

  version(s"$printedName v${Version()}")
  banner(s"""
            |Add the new license agreement to the dataset for the given datasetID list.
            |
            |Options:
            |""".stripMargin)

  val pidsfile = opt[File](name = "pids-file",
    required = true,
    descr = "The name of file that contains the list of pids.")(shouldBeFile)
  val outputFile = opt[File](name = "output-file",
    required = true,
    descr = "The name of the file where the process status (success/failure) of the given pids are written")(createIfNotExists)
  val doUpdate = opt[Boolean]("doUpdate",
    noshort = true,
    descr = "Without this argument no new license will added, the default is a test mode that logs the intended changes",
    default = Some(false))
  footer("")
  verify()
}

object CommandLineOptions {

  val log = LoggerFactory.getLogger(getClass)

  def parse(args: Array[String]): (Parameters, File) = {
    log.debug("Loading application properties ...")
    val homeDir = new File(System.getProperty("app.home"))
    val props = {
      val ps = new PropertiesConfiguration()
      ps.setDelimiterParsingDisabled(true)
      ps.load(new File(homeDir, "cfg/application.properties"))

      ps
    }

    val z = props.getString("fcrepo.url")
    log.debug("Parsing command line ...")
    val opts = new CommandLineOptions(args)

    val params = new Parameters(
      pidsfile = opts.pidsfile(),
      doUpdate = opts.doUpdate(),
      fedoraClient = new FedoraClient(new FedoraCredentials(
        props.getString("fcrepo.url"),
        props.getString("fcrepo.user"),
        props.getString("fcrepo.password"))),
      ldapContext = {
        import java.{util => ju}

        val env = new ju.Hashtable[String, String]
        env.put(Context.PROVIDER_URL, props.getString("auth.ldap.url"))
        env.put(Context.SECURITY_AUTHENTICATION, "simple")
        env.put(Context.SECURITY_PRINCIPAL, props.getString("auth.ldap.user"))
        env.put(Context.SECURITY_CREDENTIALS, props.getString("auth.ldap.password"))
        env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory")

        new InitialLdapContext(env, null)
      },
      licenseResource = new File(props.getString("license.resources"))
    )

    val outputFile = opts.outputFile()

    log.debug(s"Using the following settings: $params")
    log.debug(s"Output will be written to ${outputFile.getAbsolutePath}")

    (params, outputFile)
  }
}

object Version {
  def apply(): String = {
    val props = new Properties()
    props.load(getClass.getResourceAsStream("/Version.properties"))
    props.getProperty("application.version")
  }
}
