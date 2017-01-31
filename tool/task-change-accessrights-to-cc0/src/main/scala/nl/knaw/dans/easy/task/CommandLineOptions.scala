package nl.knaw.dans.easy.task

import java.io.{ File, PrintWriter }
import java.net.URL
import javax.naming.Context
import javax.naming.ldap.InitialLdapContext

import com.yourmediashelf.fedora.client.FedoraCredentials
import org.rogach.scallop._
import org.slf4j.{ Logger, LoggerFactory }

object CommandLineOptions {

  val log: Logger = LoggerFactory.getLogger(getClass)

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
      changedPids = new PrintWriter(new File(opts.changedDatasetPidsOutput())),
      changedFiles = new PrintWriter(new File(opts.changedFilesOutput())),
      ldapContext = {
        import java.{ util => ju }

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

  banner(
    """
      |Task to set all accessrights of fileitems of given datasets to Open Access
      |
      |Options:
    """.stripMargin)

  val doUpdate: ScallopOption[Boolean] = opt[Boolean](name = "doUpdate", noshort = true,
    descr = "Without this argument no changes are made to the repository, the default is a test mode that logs the intended changes",
    default = Some(false))

  val fedoraUrl: ScallopOption[URL] = opt[URL](name = "fedora-url",
    descr = "Base url for the fedora repository",
    default = Some(new URL("http://localhost:8080/fedora")))
  val fedoraUsername: ScallopOption[String] = opt[String](name = "fedora-username", noshort = true,
    descr = "Username for fedora repository, if omitted provide it on stdin")
  val fedoraPassword: ScallopOption[String] = opt[String](name = "fedora-password", noshort = true,
    descr = "Password for fedora repository, if omitted provide it on stdin")

  val ldapUrl: ScallopOption[String] = opt[String](name = "ldap-url", short = 'u',
    descr = "Base url for the ldap service",
    default = Some("ldap://localhost:0389"))//(shouldBeUrl) throws ldap is not a valid protocol
  val ldapPrincipal: ScallopOption[String] = opt[String](name = "ldap-principal", noshort = true,
    descr = "Username for ldap service, if omitted provide it on stdin")
  val ldapPassword: ScallopOption[String] = opt[String](name = "ldap-password", noshort = true,
    descr = "Password for ldap service, if omitted provide it on stdin")

  val changedDatasetPidsOutput: ScallopOption[String] = opt[String](name = "dataset-ids", short = 'd',
    descr = "Name of the file where the dataset ids are written",
    default = Some("dataset_ids.txt"))
  val changedFilesOutput: ScallopOption[String] = opt[String](name = "changed-file-item-ids", short = 'f',
    descr = "Name of the file where the changed fileitem ids are written",
    default = Some("fileitem_ids.txt"))

  val inputFile: ScallopOption[File] = trailArg[File](name = "input-file", required = true, descr = "The CSV file with urns of the datasets to be changed")

  validateFileExists(inputFile)
  validateFileIsFile(inputFile)

  footer("")
  verify()
}
