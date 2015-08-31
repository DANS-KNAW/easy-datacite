package nl.knaw.dans.easy.task

import com.yourmediashelf.fedora.client.FedoraClient.modifyDatastream
import org.slf4j.LoggerFactory

class StreamUpdater {
  val log = LoggerFactory.getLogger(getClass)

  def updateDatastream(pid: String, content: String)(implicit settings: Settings) {
    modifyDatastream(pid, settings.streamId).content(content).execute()
    log.info(s"saved $pid/${settings.streamId}")
  }
}

object StreamUpdater {
  def get(testMode: Boolean) = {
    if (!testMode)
      new StreamUpdater
    else
      new StreamUpdater {
        override def updateDatastream(pid: String, content: String)(implicit settings: Settings): Unit = {
          log.info(s"test mode: skipped update of $pid/${settings.streamId}")
        }
      }
  }
}