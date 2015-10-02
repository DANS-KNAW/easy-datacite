package nl.knaw.dans.easy.task

import com.yourmediashelf.fedora.client.FedoraClient._
import org.slf4j.LoggerFactory

import scala.io.Source
import scala.xml.transform.{RewriteRule, RuleTransformer}
import scala.xml.{Elem, Node, Text}

object Main {

  val DATASET_RIGHTS_TO_BE_CHANGED = "OPEN_ACCESS_FOR_REGISTERED_USERS"
  val NEW_DATASET_RIGHTS = "OPEN_ACCESS"
  val FILE_RIGHTS_TO_BE_CHANGED = "KNOWN"
  val NEW_FILE_RIGHTS = "ANONYMOUS"
  val INPUT_FILE = "/identifiers.txt"

  val log = LoggerFactory.getLogger(getClass)

  def main(args: Array[String]) {

    implicit val settings: Settings = CommandLineOptions.parse(args)

    log.info(s"Running with $settings")
    if (settings.testMode) log.info(s"Running in testmode; NO CHANGES WILL BE MADE")
 
    getIdentifiers.foreach {processObject}

    settings.writer.close()
    settings.writer_2.close()
    log.info("Finished")
  }

  def getIdentifiers() : List[String] = {

    val stream = getClass.getResourceAsStream(INPUT_FILE)
    Source.fromInputStream(stream).getLines.toList
  }

  def processObject(identifier : String)(implicit settings: Settings) : Unit = {

    log.info(s"Inspecting: $identifier")
    val pid = getPid(identifier)
    if (!pid.isEmpty) fixObject(pid, identifier)
  }

  def getPid(identifier: String) : String = {

    val pids = findObjects().pid().query(s"identifier~" + identifier).execute.getPids
    if (pids.isEmpty) {
      log.warn(s"There are no datasets with identifier $identifier");
      ""
    } else if (pids.size() > 1) {
      log.warn(s"There are more than one dataset with identifier $identifier");
      ""
    } else {
      pids.get(0)
    }
  }

  def fixObject(pid: String, identifier: String)(implicit settings: Settings) = {

    if (changeDatasetRightsEMD(pid, identifier) && changeDatasetRightsDC(pid)) {
      changeDatasetFileRights(pid)
    }
  }

  def changeDatasetRightsEMD(pid: String, identifier: String)(implicit settings: Settings): Boolean = {

    val streamId = "EMD"
    Util.getXml(pid, streamId) match {
      case None => false
      case Some(xml) =>
        val newXml = datasetTransformerEMD.transform(xml)
        if (!newXml.equals(xml)) {
          log.info(s"$pid/$streamId : $DATASET_RIGHTS_TO_BE_CHANGED -> $NEW_DATASET_RIGHTS")
          settings.updater.updateDatastream(pid, streamId, newXml.toString())
          settings.writer.println(pid)
          settings.writer_2.println(s"\n$identifier"); settings.writer_2.println(pid)
          return true
        }
    }
    false
  }

  def changeDatasetRightsDC(pid: String)(implicit settings: Settings): Boolean = {

    val streamId = "DC"
    Util.getXml(pid, streamId) match {
      case None => false
      case Some(xml) =>
        val newXml = datasetTransformerDC.transform(xml)
        if (!newXml.equals(xml)) {
          log.info(s"$pid/$streamId : $DATASET_RIGHTS_TO_BE_CHANGED -> $NEW_DATASET_RIGHTS")
          settings.updater.updateDatastream(pid, streamId, newXml.toString())
          return true
        }
    }
    false
  }

  def changeDatasetFileRights(pid: String)(implicit settings: Settings): Unit = {

    Util.getFileIdentifiers(pid).foreach(changeFileRights)
  }


  def changeFileRights(pid: String)(implicit settings: Settings) = {

    val streamId = "EASY_FILE_METADATA"
    Util.getXml(pid, streamId) match {
      case None =>
      case Some(xml) =>
        val newXml = fileTransformer.transform(xml)
        if (!newXml.equals(xml)) {
          log.info(s"$pid/$streamId : $FILE_RIGHTS_TO_BE_CHANGED -> $NEW_FILE_RIGHTS")
          settings.updater.updateDatastream(pid, streamId, newXml.toString())
          settings.writer_2.println(s"   $pid   " + readFileName(pid))
        }
    }
  }

  def readFileName(pid: String) = {

    val streamId = "DC"
    Util.getXml(pid, streamId) match {
      case None => ""
      case Some(xml) => getFileName(xml)
    }
  }

  def getFileName (xml : Node): String = {

    if ((xml \\ "title").nonEmpty) (xml \\ "title").map(_.text).head else ""
  }

  val datasetTransformerEMD = new RuleTransformer(new RewriteRule {
    override def transform(n: Node): Seq[Node] = n match {
      case Elem(prefix, "accessRights", attribs, scope, _*) if n.child.head.toString() contains DATASET_RIGHTS_TO_BE_CHANGED =>
        Elem(prefix, "accessRights", attribs, scope, false, Text({
          NEW_DATASET_RIGHTS
        }))
      case other => other
    }
  })

  val datasetTransformerDC = new RuleTransformer(new RewriteRule {
    override def transform(n: Node): Seq[Node] = n match {
      case Elem(prefix, "rights", attribs, scope, _*) if n.child.head.toString() contains DATASET_RIGHTS_TO_BE_CHANGED =>
        Elem(prefix, "rights", attribs, scope, false, Text({
          NEW_DATASET_RIGHTS
        }))
      case other => other
    }
  })

  val fileTransformer = new RuleTransformer(new RewriteRule {
    override def transform(n: Node): Seq[Node] = n match {
      case Elem(prefix, "accessibleTo", attribs, scope, _*) if n.child.head.toString() contains FILE_RIGHTS_TO_BE_CHANGED =>
        Elem(prefix, "accessibleTo", attribs, scope, false, Text({
          NEW_FILE_RIGHTS
        }))
      case other => other
    }
  })
}