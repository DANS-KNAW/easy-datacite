package nl.knaw.dans.easy.task

import java.lang.Thread.sleep
import com.yourmediashelf.fedora.client.FedoraClient.modifyDatastream
import org.slf4j.LoggerFactory.getLogger
import scala.util.{Failure, Success, Try}

trait StreamUpdater {

  def updateDatastream(pid: String, streamId: String, content: String)(implicit settings: Settings)
}

object StreamUpdater {

  val log = getLogger(getClass)

  def get(testMode: Boolean, timeout: Long = 1000L) = {
    if (!testMode)
      new StreamUpdater {
        override def updateDatastream(pid: String, streamId: String, content: String)(implicit settings: Settings): Unit = {
          Try(
            modifyDatastream(pid, streamId).content(content).execute()
          ) match {
            case Success(response) =>
              if (response.getStatus == 200)
                log.info(s"saved $pid/$streamId")
              else
                log.warn(s"saving $pid/$streamId returned ${response.getStatus}")
              response.close()
            case Failure(e) =>
              log.error(s"saving $pid/$streamId threw ${e.getClass.getName} : ${e.getMessage}")
              throw e
          }
          sleep(timeout)
        }
      }
    else
      new StreamUpdater {
        override def updateDatastream(pid: String, streamId: String, content: String)(implicit settings: Settings): Unit = {}
      }
  }
}