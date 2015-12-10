package nl.knaw.dans.easy.task

import org.slf4j.LoggerFactory

import scala.collection.mutable
import scala.io.Source
import scala.util.{Success, Try}
import scala.xml.transform.{RewriteRule, RuleTransformer}
import scala.xml._
import scala.xml.XML

object Main { 

  val log = LoggerFactory.getLogger(getClass)
  val prefixDct = "dct"
  val prefixDcterms="dcterms"

  def main(args: Array[String]) {

    implicit val settings: Settings = CommandLineOptions.parse(args)
    implicit val depositors = mutable.Map[String, String]()

    log.info(s"Running with $settings")
    if (settings.testMode) log.info(s"Running in testmode; NO CHANGES WILL BE MADE")

    getPids(settings.pidsfile).foreach(pid => processObject(pid).get)

    settings.writer.close()
    log.info("Finished")
  }

  def getPids(pids : String) : List[String] = {
    Source.fromFile(pids).getLines.toList
  }

  def processObject(pid : String)(implicit settings : Settings, depositors : mutable.Map[String, String]): Try[Unit]  = {
    log.info(s"Inspecting: $pid")

    for {
      changedAudience <- changeDisciplineAudience(pid)
      changedRelsExt <- changeDisciplineRelsExt(pid)
    } yield if (changedAudience && changedRelsExt) {
                log.info(pid + " " + readDatasetName(pid) + " is modified")
                settings.writer.println(pid)
            } else if (changedAudience || changedRelsExt) {
                settings.writer.println(pid)
                log.warn("In " + pid + " " + readDatasetName(pid) + "only one of the datastreams is modified!")
            } else log.info(pid + " " + readDatasetName(pid) + " was not modified")
            
  }

  def changeDisciplineAudience(pid : String)(implicit settings : Settings): Try[Boolean] = {
    val streamId = "EMD"
    Util.getDatastreamXmlOrLogError(pid, streamId) match {
      case None => Success(false)
      case Some(xml) => Try {
          val (removed, prefOld, xmlOldAudienceRemoved) = removeAudience(xml, pid, streamId)
          if (removed) {
            val (added, newXml) = addNewAudience(xmlOldAudienceRemoved, prefOld, pid, streamId)
            if (added) {
              log.info(s"$pid/$streamId change ${prefOld}:audience discipline: ${settings.odis} -> ${settings.ndis}")
              settings.updater.updateDatastream(pid, streamId, newXml.head.toString())
              true
            } else false
          } else false
      }
    }
  }

  def changeDisciplineRelsExt(pid: String)(implicit settings : Settings): Try[Boolean] = {
    val streamId = "RELS-EXT"
    Util.getDatastreamXmlOrLogError(pid, streamId) match {
      case None => Success(false)
      case Some(xml) => Try {
            val (removed_IsMemberOfOAISet, xmlOldDisciplineRemoved_IsMemberOfOAISet) = removeDisciplineIsMemberOfOAISet(xml, pid, streamId)
            if (removed_IsMemberOfOAISet) {
              val (added_IsMemberOfOAISet, xmldDisciplineRemoved_IsMemberOfOAISet) = addNewDisciplineIsMemberOfOAISet(xmlOldDisciplineRemoved_IsMemberOfOAISet, pid, streamId)
              if (added_IsMemberOfOAISet) {
                val (removed_IsMemberOf, xmlOldDisciplineRemoved_IsMemberOf) = removeDisciplineIsMemberOf(xmldDisciplineRemoved_IsMemberOfOAISet, pid, streamId)
                if (removed_IsMemberOf) {
                  val (added_IsMemberOf, newXml) = addNewDisciplineIsMemberOf(xmlOldDisciplineRemoved_IsMemberOf, pid, streamId)
                  if(added_IsMemberOf) {
                    log.info(s"$pid/$streamId change discipline: ${settings.odis} -> ${settings.ndis}")
                    settings.updater.updateDatastream(pid, streamId, newXml.head.toString())
                    true
                  } else false
                } else false
              } else false
            } else false
      }
    }
  }

  def removeAudience(xml: Node, pid : String, streamId :String)(implicit settings : Settings): (Boolean, String, Seq[Node]) = {

    var changed = false
    var prefixOld = ""
    object datasetTransformer extends RuleTransformer(new RewriteRule {
      override def transform(n: Node): Seq[Node] = n match {
        case audience@Elem(prefix, "audience", attribs, scope, _*) if List(prefixDct, prefixDcterms).contains(prefix) && audience.text.trim() == settings.odis =>
          changed = true
          prefixOld=prefix
          Seq()
        case other => other
      }
    })
    //when changed is false, pref is "".
    val outputXml = datasetTransformer.transform(xml)
    (changed, prefixOld, outputXml)
  }

  def addNewAudience(xml: Seq[Node], prefOld: String, pid: String, streamId :String)(implicit settings : Settings): (Boolean, Seq[Node]) = {

    var newDiscipline = false
    object datasetTransformer extends RuleTransformer(new RewriteRule {
      override def transform(n: Node): Seq[Node] = n match {
        //Here, we don't need to check the audience prefix. It will only check whether the ndis exist or not
        case audience@Elem("emd", "audience", attribs, scope, _*) if( audience.child.exists(_.text.trim == settings.ndis)) =>
          newDiscipline = true;
          audience
        case audience@Elem("emd", "audience", attribs, scope, _*) if !audience.child.exists(_.text.trim == settings.ndis) =>
          newDiscipline = true
          //check the original prefix
          if (prefOld == prefixDct)
            <emd:audience>
              <dct:audience eas:schemeId="custom.disciplines">{settings.ndis}</dct:audience>
              { audience.child.map(copyNodeNoNamespace) }
            </emd:audience>
          else 
            <emd:audience>
              <dcterms:audience eas:schemeId="custom.disciplines">{settings.ndis}</dcterms:audience>
              { audience.child.map(copyNodeNoNamespace) }
            </emd:audience>
        case other => other
      }
    })
    val outputXml = datasetTransformer.transform(xml)
    (newDiscipline, outputXml)
  }

  def copyNodeNoNamespace(node: Node): Node =
    node match {
      case elem: Elem => elem.copy(scope = TopScope, child = elem.child.map(copyNodeNoNamespace))
      case n => n
    }

  def removeDisciplineIsMemberOfOAISet(xml: Node, pid: String, streamId :String)(implicit settings : Settings): (Boolean, Seq[Node]) = {

    var changed = false
    object datasetTransformer extends RuleTransformer(new RewriteRule {
      override def transform(n: Node): Seq[Node] = n match {
        case isMemberOfOAISet@Elem(_, "isMemberOfOAISet", attribs, scope, _*) if attributeValueEquals(s"info:fedora/${settings.odis}")(isMemberOfOAISet) =>
          changed = true
          Seq()
        case other => other
      }
    })
    val outputXml = datasetTransformer.transform(xml)
    (changed, outputXml)
  }

  def addNewDisciplineIsMemberOfOAISet(xml: Seq[Node], pid: String, streamId :String)(implicit settings : Settings): (Boolean, Seq[Node]) = {

    var newDiscipline = false
    object datasetTransformer extends RuleTransformer(new RewriteRule {
      override def transform(n: Node): Seq[Node] = n match {
        case desc@Elem(_, "Description", attribs, scope, _*) if desc.child.exists(_.text.trim == settings.ndis) =>
          newDiscipline = true;
          desc
        case desc@Elem(_, "Description", attribs, scope, _*) if !desc.child.exists(_.text.trim == settings.ndis) =>
          newDiscipline = true
          <rdf:Description rdf:about={s"info:fedora/$pid"}>
            {desc.child.map(copyNodeNoXmlnsRdfNamespace)}
            <isMemberOfOAISet xmlns="http://dans.knaw.nl/ontologies/relations#"
                              rdf:resource={s"info:fedora/${settings.ndis}"}/>
          </rdf:Description>
        case other => other
      }
    })
    val outputXml = datasetTransformer.transform(xml)
    (newDiscipline, outputXml)
  }

  def removeDisciplineIsMemberOf(xml: Seq[Node], pid: String, streamId :String)(implicit settings : Settings): (Boolean, Seq[Node]) = {

    var changed = false
    object datasetTransformer extends RuleTransformer(new RewriteRule {
      override def transform(n: Node): Seq[Node] = n match {
        case isMemberOfOAISet@Elem(_, "isMemberOf", attribs, scope, _*) if attributeValueEquals(s"info:fedora/${settings.odis}")(isMemberOfOAISet) =>
          changed = true
          Seq()
        case other => other
      }
    })
    val outputXml = datasetTransformer.transform(xml)
    (changed, outputXml)
  }

  def addNewDisciplineIsMemberOf(xml: Seq[Node], pid: String, streamId :String)(implicit settings : Settings): (Boolean, Seq[Node]) = {

    var newDiscipline = false
    object datasetTransformer extends RuleTransformer(new RewriteRule {
      override def transform(n: Node): Seq[Node] = n match {
        case desc@Elem(_, "Description", attribs, scope, _*) if desc.child.exists(_.text.trim == settings.ndis) =>
          newDiscipline = true;
          desc
        case desc@Elem(_, "Description", attribs, scope, _*) if !desc.child.exists(_.text.trim == settings.ndis) =>
          newDiscipline = true
          <rdf:Description rdf:about={s"info:fedora/$pid"}>
            {desc.child.map(copyNodeNoXmlnsRdfNamespace)}
            <isMemberOf xmlns="http://dans.knaw.nl/ontologies/relations#"
                        rdf:resource={s"info:fedora/${settings.ndis}"}/>
          </rdf:Description>
        case other => other
      }
    })
    val outputXml = datasetTransformer.transform(xml)
    (newDiscipline, outputXml)
  }

  def attributeValueEquals(value: String)(node: Node) = {
    node.attributes.exists(_.value.text == value)
  }

  def attributeValueExist(node : Node, nodeName : String, attval : String) : Boolean = {
      for(entry <- node.child) {
        if ((entry.label.trim() == nodeName) && entry.attributes.exists(_.value.text == attval)) {
          return true
        }
      }
    false
  }

  /*
  This a hack.
  Situation:
  Given xml node input as follows:
  <hasModel xmlns="info:fedora/fedora-system:def/model#"
            rdf:resource="info:fedora/easy-model:oai-item1"/>
   The element copy will add namespaces in the attribute:
   <hasModel xmlns="info:fedora/fedora-system:def/model#"
            rdf:resource="info:fedora/easy-model:oai-item1"
            xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#"/>
   Georgi has a solution to remove all namespaces. See copyNodeNoNamespace function.
   However that is not that we need in RelExt since the following output will be generated:
   <hasModel rdf:resource="info:fedora/easy-model:oai-item1"></hasModel>
   The hack:
   - copy node
   - convert it to string
   - replace all xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#" with ""
   - put it back as node

   The right way is to remove unneeded namespace.
   */
  def copyNodeNoXmlnsRdfNamespace(node: Node): Node =
    node match {
      case elem: Elem =>
        val xmlString:String = elem.mkString.replaceAll("""xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#"""", "")
        val xmlNode = XML.loadString(xmlString)
        xmlNode
      case n => n
    }

  def readDatasetName(pid: String) = {
    val streamId = "DC"
    Util.getDatastreamXmlOrLogError(pid, streamId) match {
      case None => ""
      case Some(xml) => getDatasetName(xml)
    }
  }

  def getDatasetName (xml : Node): String = {
    if ((xml \\ "title").nonEmpty) (xml \\ "title").map(_.text).head else ""
  }
}