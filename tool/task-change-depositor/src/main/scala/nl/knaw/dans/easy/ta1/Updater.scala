package nl.knaw.dans.easy.ta1

import java.lang.Thread.sleep

import com.yourmediashelf.fedora.client.FedoraClient.modifyDatastream
import org.slf4j.LoggerFactory.getLogger

import scala.util.{Failure, Success, Try}

/** Updater of changed datastreams */
trait Updater {

  /**
   * Saves the content of a datastream, in test mode the intended change is only logged.
   *
   * @param pid id of a fedora object
   * @param content the new content of a datastream
   * @param settings the configuration of the datastream id and test mode
   * @return
   */
  def updateDatastream(pid: String, content: String)(implicit settings: Settings)
}

object Updater {

  val log = getLogger(getClass)

  /**
   * the pids to pass on to https://github.com/DANS-KNAW/easy-update-solr-index
   * needs a special appender in loagbaxk.xml
   **/
  val pidLog = getLogger("UpdatedPids")

  /**
   * Saves a changed datastream in the repository.
   *
   * @param testMode If true intended updates are logged but not executed.
   * @param timeout Time to wait after an actual update, default one second.
   *                Prevents blocking other production processes.
   * @return
   */
  def get(testMode: Boolean, timeout: Long = 1000L) = {
    if (!testMode)
      new Updater {
        override def toString = s"update mode for $getClass"

        override def updateDatastream(pid: String, content: String)(implicit settings: Settings): Unit = {
          Try(
            modifyDatastream(pid, settings.datastreamId).content(content).execute()
          ) match {
            case Success(response) =>
              if (response.getStatus == 200)
                log.info(s"saved $pid/${settings.datastreamId}")
              else
                log.warn(s"saving $pid/${settings.datastreamId} returned ${response.getStatus}")
              response.close()
              pidLog.info(pid)
            case Failure(e) =>
              log.error(s"saving $pid/${settings.datastreamId} threw ${e.getClass.getName} : ${e.getMessage}")
              throw e
          }
          sleep(timeout)
        }
      }
    else
      new Updater {
        override def toString = s"test mode for $getClass"

        override def updateDatastream(pid: String, content: String)(implicit settings: Settings): Unit = {
          log.info(s"test mode: skipped update of $pid/${settings.datastreamId}")
          pidLog.info(pid)
        }
      }
  }
}