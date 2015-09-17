package nl.knaw.dans.easy.task

import scala.xml.transform.{RewriteRule, RuleTransformer}
import scala.xml.{Elem, Node, Text}

object ChangeRights extends AbstractMain {

  val datasetRightsToBeChanged = "GROUP_ACCESS"
  val newDatasetRights = "OPEN_ACCESS_FOR_REGISTERED_USERS"
  val fileRightsToBeChanged = "RESTRICTED_GROUP"
  val newFileRights = "KNOWN"
  val urn = """<dc:identifier.*eas:scheme="PID".*>(.*)</dc:identifier>""".r


  def fixObject(pid: String)(implicit settings: Settings) = {

    log.info(s"Inspecting: $pid")
    if (published(pid) && changeDatasetRightsEMD(pid) && changeDatasetRightsDC(pid)){
      changeDatasetFileRights(pid)
    }
  }

  def published (pid : String): Boolean = {
    Util.getXml(pid, "AMD") match {
      case None => false
      case Some(xml) => getDatasetState(xml) == "PUBLISHED"
    }
  }

  def changeDatasetRightsEMD(pid: String)(implicit settings: Settings) : Boolean = {

    val streamId = "EMD"
    Util.getXml(pid, streamId) match {
      case None => false
      case Some(xml) =>
        val newXml =  datasetTransformerEMD.transform(xml)
        if (!newXml.equals(xml)) {
          if (onlyOneRightsHolder(xml, settings.rightsHolder)){
            log.info(s"Rightsholder: " + getRightsHolder(xml))
            log.info(s"$pid/$streamId : $datasetRightsToBeChanged -> $newDatasetRights")
            settings.updater.updateDatastream(pid, streamId, newXml.toString())
            if(!settings.testMode) settings.writer.println(pid)
            return true
          }
          else {
            log.warn(s"$pid not changed because there is more than one rightsholder; pid: " + getUrn(xml))
          }
      }
    }
    false
  }

  def changeDatasetRightsDC(pid: String)(implicit settings: Settings) : Boolean = {

    val streamId = "DC"
    Util.getXml(pid, streamId) match {
      case None => false
      case Some(xml) =>
        val newXml = datasetTransformerDC.transform(xml)
        if (!newXml.equals(xml)) {
          log.info(s"$pid/$streamId : $datasetRightsToBeChanged -> $newDatasetRights")
          settings.updater.updateDatastream(pid, streamId, newXml.toString())
          return true
        }
    }
    false
  }

  def changeDatasetFileRights(pid : String)(implicit settings: Settings) : Unit = {

    Util.getFileIdentifiers(pid).foreach(changeFileRights)
  }


  def changeFileRights(pid: String)(implicit settings: Settings) = {

    val streamId = "EASY_FILE_METADATA"
    Util.getXml(pid, streamId) match {
      case None =>
      case Some(xml) =>
        val newXml = fileTransformer.transform(xml)
        if (!newXml.equals(xml)) {
          log.info(s"$pid/$streamId : $fileRightsToBeChanged -> $newFileRights")
          settings.updater.updateDatastream(pid, streamId, newXml.toString())
        }
    }
  }

  val datasetTransformerEMD = new RuleTransformer(new RewriteRule {
    override def transform(n: Node): Seq[Node] = n match {
      case Elem(prefix, "accessRights", attribs, scope, _*) if n.child.head.toString() contains datasetRightsToBeChanged =>
        Elem(prefix, "accessRights", attribs, scope, false, Text({newDatasetRights}))
      case other => other
    }
  })

  val datasetTransformerDC = new RuleTransformer(new RewriteRule {
    override def transform(n: Node): Seq[Node] = n match {
      case Elem(prefix, "rights", attribs, scope, _*) if n.child.head.toString() contains datasetRightsToBeChanged =>
        Elem(prefix, "rights", attribs, scope, false, Text({newDatasetRights}))
      case other => other
    }
  })

  val fileTransformer = new RuleTransformer(new RewriteRule {
    override def transform(n: Node): Seq[Node] = n match {
      case Elem(prefix, "accessibleTo", attribs, scope, _*) if n.child.head.toString() contains fileRightsToBeChanged =>
        Elem(prefix, "accessibleTo", attribs, scope, false, Text({newFileRights}))
      case other => other
    }
  })

  def getDatasetState (xml : Node): String = {

    if ((xml \\ "datasetState").nonEmpty) (xml \\ "datasetState").map(_.text).head else ""
  }

  def onlyOneRightsHolder(xml : Node, rightsHolder : String): Boolean = {

    val xmlString = xml.toString().toLowerCase
    val givenRightsHolders = ("""<dct:rightsHolder>.*""" + rightsHolder + """.*</dct:rightsHolder>""").toLowerCase.r
    val allRightsHolders = """<dct:rightsHolder>.*</dct:rightsHolder>""".toLowerCase.r
    val numberOfGivenRightsHolders = givenRightsHolders.findAllIn(xmlString).size
    val numberOfAllRightsHolders = allRightsHolders.findAllIn(xmlString).size
    numberOfGivenRightsHolders == numberOfAllRightsHolders
  }

  def getRightsHolder(xml : Node) : String = {

    if ((xml \\ "rightsHolder").nonEmpty) (xml \\ "rightsHolder").map(_.text).head else ""
  }

  def getUrn(xml : Node) : String = {

    (for (m <- urn findFirstMatchIn xml.toString) yield m group 1).getOrElse("")
  }
}