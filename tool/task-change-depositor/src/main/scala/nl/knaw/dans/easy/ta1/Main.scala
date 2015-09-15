package nl.knaw.dans.easy.ta1

import scala.xml.transform.{RewriteRule, RuleTransformer}
import scala.xml.{Node, NodeSeq}

/**
 * Configuration of how and when which datastreams should be changed,
 * the other classes and objects take care of processing, logging and
 * interpretation of command line arguments.
 *
 *
 * ==Clone this task for something similar==
 *
 * Copy the pom file and source folder in a new folder under easy-app/tool.
 *
 * Change the artifact-id, name and description in the pom file.
 *
 * The IDE test suite gets confused with identical classes in multiple projects, so make the package
 * unique. Do not use the refactoring in the IDE as it will change the package for all projects,
 * don't forget the main class specification in the pom.
 *
 *
 * ==Straight forward cases==
 *
 * Adjust only the Main class and the corresponding offline test.
 *
 *
 * ==More complex cases==
 *
 * This version of the updater does not support
 * [[com.yourmediashelf.fedora.client.request.RelationshipsRequest]]s,
 * it should apply the test-mode switch.
 * The [[nl.knaw.dans.easy.ta1.ObjectProcessor#run]] method could read more fields
 * than the fedora-pid to pass on to [[nl.knaw.dans.easy.ta1.Main#transformer]],
 * see the fields listed on [[http://teasy.dans.knaw.nl/fedora/objects]]
 * The transformDatastream method could read additional datastreams
 * to decide if the found datastream really needs a change.
 * Updating multiple streams for an object requires to change
 * [[nl.knaw.dans.easy.ta1.ObjectProcessor#fixObject]]
 */
object Main {

  /** Completes the definition of the commandline arguments. */
  class Options(args: Array[String]) extends CommandLineOptions(args) {
    banner( """task to change the depositor of datasets
              |
              |Options:
              | """.stripMargin)
    val oldDepositor = opt[String]("oldDepositor", required = true)
    val newDepositor = opt[String]("newDepositor", required = true)
    val query = opt[List[String]]("queries", required = true, descr =
        "one or more values for 'specific fields' on http://teasy.dans.knaw.nl/fedora/objects " +
        "e.g 'creator~*SMGI*' 'creator~*KITLV*' for objects matching either condition or " +
        "'creator~*SMGI* creator~*KITLV*' for objects matching both conditions")
  }

  def getSettings(args: Array[String]): Settings = {
    val options = new Options(args)
    val transformer = new RuleTransformer(
      new RewriteRule {
        override def transform(node: Node): NodeSeq = node match {
          case _ if (node.label == "depositorId")
            && (node.text == options.oldDepositor.apply())
          => <depositorId>{options.newDepositor.apply()}</depositorId>
          case _ => node
        }
      }
    )
    new Settings(
      updater = options.parse(),
      objectQueries = options.query.apply(),
      datastreamId = "AMD"
    ) {
      /* Applies the transformer. */
      override def transform(pid: String, rootNode: Node): Seq[Node] = {
        if (rootNode.descendant contains <datasetState>PUBLISHED</datasetState>)
          transformer.apply(rootNode)
        else rootNode
      }
    }
  }

  /** Executes the task. */
  def main(args: Array[String]): Unit = ObjectProcessor.run(getSettings(args))
}
