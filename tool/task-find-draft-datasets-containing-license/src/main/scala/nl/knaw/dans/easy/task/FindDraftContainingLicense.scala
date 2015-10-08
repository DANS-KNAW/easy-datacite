package nl.knaw.dans.easy.task

import scala.xml.{Node}

object FindDraftContainingLicense extends AbstractMain {


  def findObject(pid: String)(implicit settings: Settings) = {

    log.info(s"Inspecting: $pid")
    if (isDraft(pid) && hasLicense(pid)){
      settings.writer.println(pid)
      log.warn(pid + " has DATASET_LICENSE")
    }
  }

  def isDraft (pid : String): Boolean = {
    Util.getXml(pid, "AMD") match {
      case None => false
      case Some(xml) => getDatasetState(xml) == "DRAFT"
    }
  }

  def hasLicense (pid : String): Boolean = {
    Util.hasDatastream(pid, "DATASET_LICENSE")
  }

  def getDatasetState (xml : Node): String = {
    if ((xml \\ "datasetState").nonEmpty) (xml \\ "datasetState").map(_.text).head else ""
  }



}