package nl.knaw.dans.easy.task

import org.slf4j.LoggerFactory

import scala.collection.mutable
import scala.io.Source
import scala.xml.transform.{RewriteRule, RuleTransformer}
import scala.xml.{Elem, Node, Text}

object Main {

  val TEST_DOI_NAMESPACE = "10.5072"
  val PRODUCTION_DOI_NAMESPACE = "10.17026"
  val DATASETS_INPUT_FILE = "/pids.txt"

  val log = LoggerFactory.getLogger(getClass)

  def main(args: Array[String]) {

    implicit val settings: Settings = CommandLineOptions.parse(args)
    implicit val depositors = mutable.Map[String, String]()

    log.info(s"Running with $settings")
    if (settings.testMode) log.info(s"Running in testmode; NO CHANGES WILL BE MADE")
 
    getPids.foreach {processObject}
    EmailProcessing.process

    settings.writer.close()
    settings.writer_2.close()
    log.info("Finished")
  }

  def getPids() : List[String] = {

    val stream = getClass.getResourceAsStream(DATASETS_INPUT_FILE)
    Source.fromInputStream(stream).getLines.toList
  }

  def processObject(pid : String)(implicit settings : Settings, depositors : mutable.Map[String, String]) : Unit = {

    log.info(s"Inspecting: $pid")
    if (changeNamespaceEMD(pid) && changeNamespaceDC(pid) && changeNamespaceRelsExt(pid)) {
      addToDepositorDatasets(getDepositor(pid), pid)
    }
  }

  def changeNamespaceEMD(pid: String)(implicit settings : Settings): Boolean = {

    val streamId = "EMD"
    Util.getXml(pid, streamId) match {
      case None => false
      case Some(xml) =>
        val newXml = datasetTransformer.transform(xml)
        if (!newXml.equals(xml)) {
          log.info(s"$pid/$streamId DOI namespace: $TEST_DOI_NAMESPACE -> $PRODUCTION_DOI_NAMESPACE")
          settings.updater.updateDatastream(pid, streamId, newXml.toString())
          settings.writer.println(pid)
          return true
        }
    }
    false
  }

  def changeNamespaceDC(pid: String)(implicit settings : Settings): Boolean = {

    val streamId = "DC"
    Util.getXml(pid, streamId) match {
      case None => false
      case Some(xml) =>
        val newXml = datasetTransformer.transform(xml)
        if (!newXml.equals(xml)) {
          log.info(s"$pid/$streamId DOI namespace: $TEST_DOI_NAMESPACE -> $PRODUCTION_DOI_NAMESPACE")
          settings.updater.updateDatastream(pid, streamId, newXml.toString())
          return true
        }
    }
    false
  }

  def changeNamespaceRelsExt(pid: String)(implicit settings : Settings): Boolean = {

    val streamId = "RELS-EXT"
    Util.getXml(pid, streamId) match {
      case None => false
      case Some(xml) =>
        val newXml = datasetTransformerRelsExt.transform(xml)
        if (!newXml.equals(xml)) {
          log.info(s"$pid/$streamId DOI namespace: $TEST_DOI_NAMESPACE -> $PRODUCTION_DOI_NAMESPACE")
          settings.updater.updateDatastream(pid, streamId, newXml.toString())
          return true
        }
    }
    false
  }

  def getDepositor(pid: String) = {

    val streamId = "AMD"
    Util.getXml(pid, streamId) match {
      case None => ""
      case Some(xml) => getDepositorName(xml)
    }
  }

  def getDepositorName (xml : Node): String = {

    if ((xml \\ "depositorId").nonEmpty) (xml \\ "depositorId").map(_.text).head else ""
  }

  def addToDepositorDatasets(depositor : String, pid : String)(implicit depositors : mutable.Map[String, String]) = {

    depositors(depositor) = depositors.getOrElse(depositor, "") + "\n" + pid + " " + readDatasetName(pid)
  }

  def readDatasetName(pid: String) = {

    val streamId = "DC"
    Util.getXml(pid, streamId) match {
      case None => ""
      case Some(xml) => getDatasetName(xml)
    }
  }

  def getDatasetName (xml : Node): String = {

    if ((xml \\ "title").nonEmpty) (xml \\ "title").map(_.text).head else ""
  }

  val datasetTransformer = new RuleTransformer(new RewriteRule {
    override def transform(n: Node): Seq[Node] = n match {
      case Elem(prefix, "identifier", attribs, scope, _*) if n.child.head.toString() contains TEST_DOI_NAMESPACE =>
        Elem(prefix, "identifier", attribs, scope, false, Text({
          PRODUCTION_DOI_NAMESPACE
        }))
      case other => other
    }
  })

  val datasetTransformerRelsExt = new RuleTransformer(new RewriteRule {
    override def transform(n: Node): Seq[Node] = n match {
      case Elem(prefix, "hasDoi", attribs, scope, _*) if n.child.head.toString() contains TEST_DOI_NAMESPACE =>
        Elem(prefix, "hasDoi", attribs, scope, false, Text({
          PRODUCTION_DOI_NAMESPACE
        }))
      case other => other
    }
  })
}