package nl.knaw.dans.easy.task

import org.apache.commons.csv.CSVRecord
import org.slf4j.LoggerFactory

import scala.util.{Failure, Success}

class FedoraObjectFixer(transformers: XmlDatastreamTransformer*) {
  val log = LoggerFactory.getLogger(getClass)

  // not just use pids, but pass extra params from the other columns in csv
  def fix(paramsRecord: CSVRecord)(implicit settings: Settings) = {
    transformers.foreach(fixXmlDatastream(paramsRecord, _))
  }

  def fixXmlDatastream(paramsRecord: CSVRecord, transformer: XmlDatastreamTransformer)(implicit settings: Settings) = {
    val pid = paramsRecord.get(0)
    val streamId = transformer.getStreamId
    settings.updater.getXml(pid, streamId) match {
      case Failure(e) => log.error(s"can't load: $pid/$streamId ${e.getMessage}")
      case Success(xml) =>
        val newXml = transformer.transform(paramsRecord, xml)
        if (!newXml.equals(xml)) {
          log.info(s"transformed: $pid/$streamId")
          settings.updater.updateDatastream(pid, streamId, newXml.toString())
          if(!settings.testMode) settings.writer.println(pid)
        }
    }
  }

}
