package nl.knaw.dans.easy.task

import java.io.{File, PrintWriter}
import java.net.URL
import javax.naming.Context
import javax.naming.ldap.InitialLdapContext

import com.yourmediashelf.fedora.client.FedoraCredentials
import nl.knaw.dans.easy.task.CommandLineOptions._
import org.rogach.scallop._
import org.slf4j.LoggerFactory

import scala.util.{Failure, Success, Try}

object CommandLineOptions {

  val log = LoggerFactory.getLogger(getClass)

  def parse(args: Array[String]): Settings = {
    log.debug("Parsing command line ...")
    val opts = new ScallopCommandLine(args)

    val fedoraUrl = opts.fedoraUrl()
    val fedoraUser = opts.fedoraUsername.toOption.getOrElse(ask(fedoraUrl.toString,"user name"))
    val fedoraPassword = opts.fedoraPassword.toOption.getOrElse(askPassword(fedoraUser, fedoraUrl.toString))

    val ldapUrl = opts.ldapUrl()
    val ldapPrincipal = opts.ldapPrincipal.toOption.getOrElse(ask(ldapUrl.toString, "principal"))
    val ldapPassword = opts.ldapPassword.toOption.getOrElse(askPassword(ldapPrincipal, ldapUrl.toString))
    Settings(
      !opts.doUpdate(),
      opts.inputFile(),
      new FedoraCredentials(fedoraUrl, fedoraUser, fedoraPassword),
      changedPids = new PrintWriter(new File(opts.changedPidsOutput())),
      changedFiles = new PrintWriter(new File(opts.changedFilesOutput())),
      ldapContext = {
        import java.{util => ju}

        val env = new ju.Hashtable[String, String]
        env.put(Context.PROVIDER_URL, ldapUrl)
        env.put(Context.SECURITY_AUTHENTICATION, "simple")
        env.put(Context.SECURITY_PRINCIPAL, ldapPrincipal)
        env.put(Context.SECURITY_CREDENTIALS, ldapPassword)
        env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory")
        new InitialLdapContext(env, null)
      })
  }

  def ask(url: String, prompt: String): String = {
    print(s"$prompt for $url: ")
    System.console().readLine()
  }

  def askPassword(user: String, url: String): String = {
    print(s"Password for $user on $url: ")
    System.console().readPassword().mkString
  }
}

class ScallopCommandLine(args: Array[String]) extends ScallopConf(args) {

  appendDefaultToDescription = true
  editBuilder(_.setHelpWidth(110))

  private def createExecption(msg: String) = {
    log.error(msg)
    new IllegalArgumentException(msg)
  }

  banner(
    """
      |Task to set all datasets of a given user from Open Acces for Registered Users to Open Access
      |
      |Options:
    """.stripMargin)

  val doUpdate = opt[Boolean](name = "doUpdate", noshort = true,
    descr = "Without this argument no changes are made to the repository, the default is a test mode that logs the intended changes",
    default = Some(false))

  val fedoraUrl = opt[URL](name = "fedora-url", short = 'f',
    descr = "Base url for the fedora repository",
    default = Some(new URL("http://localhost:8080/fedora")))
  val fedoraUsername = opt[String](name = "fedora-username", noshort = true,
    descr = "Username for fedora repository, if omitted provide it on stdin")
  val fedoraPassword = opt[String](name = "fedora-password", noshort = true,
    descr = "Password for fedora repository, if omitted provide it on stdin")

  val ldapUrl = opt[String](name = "ldap-url", short = 'u',
    descr = "Base url for the ldap service",
    default = Some("ldap://localhost:0389"))//(shouldBeUrl) throws ldap is not a valid protocol
  val ldapPrincipal = opt[String](name = "ldap-principal", noshort = true,
    descr = "Username for ldap service, if omitted provide it on stdin")
  val ldapPassword = opt[String](name = "ldap-password", noshort = true,
    descr = "Password for ldap service, if omitted provide it on stdin")

  val changedPidsOutput = opt[String](name = "changedPids", short = 'p',
    descr = "Name of the file where the changed pids are written",
    default = Some("changed_pids.txt"))
  val changedFilesOutput = opt[String](name = "changedFiles", short = 'c',
    descr = "Name of the file with information about the changed dataset files",
    default = Some("changed_files.txt"))

  val inputFile = trailArg[File](name = "input-file", required = true, descr = "The CSV file with urns of the datasets to be changed")

  validateFileExists(inputFile)
  validateFileIsFile(inputFile)

  footer("")
  verify()
}
