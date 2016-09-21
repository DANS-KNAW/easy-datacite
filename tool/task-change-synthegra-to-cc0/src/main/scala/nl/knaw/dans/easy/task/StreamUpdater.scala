package nl.knaw.dans.easy.task

import java.io.{ByteArrayInputStream, ByteArrayOutputStream}

import com.yourmediashelf.fedora.client.FedoraClient
import com.yourmediashelf.fedora.client.request.FedoraRequest
import nl.knaw.dans.easy.task.StreamUpdater.log
import org.slf4j.LoggerFactory
import rx.lang.scala.Observable

import scala.concurrent.duration._
import scala.language.postfixOps

trait StreamUpdater {

  def updateDatastream(pid: String, streamId: String, content: String): Observable[Unit]
}

abstract class AbstractFedoraStreamUpdater(timeout: Long = 1000L) extends StreamUpdater {

  def updateDatastream(pid: String, streamId: String, content: String) = {
    log.info(s"updating $pid/$streamId")
    log.debug(s"new content for $pid/$streamId:\n$content")
    val request = FedoraClient.modifyDatastream(pid, streamId).content(content)
    executeRequest(pid, streamId, request)
  }

  def executeRequest(pid: String, streamId: String, request: FedoraRequest[_]): Observable[Unit]
}

class TestStreamUpdater extends AbstractFedoraStreamUpdater {
  def executeRequest(pid: String, streamId: String, request: FedoraRequest[_]) =
    Observable.just(log.info(s"test-mode: skipping request for $pid/$streamId"))
}

class FedoraStreamUpdater(timeout: Long = 1000L) extends AbstractFedoraStreamUpdater {
  def executeRequest(pid: String, streamId: String, request: FedoraRequest[_]) = {
    log.info(s"executing request for $pid/$streamId")
    Observable.using(request.execute())(_.getStatus match {
      case 200 => Observable.just(log.info(s"saved $pid/$streamId"))
      case status =>
        val message = s"got status $status"
        log.info(message)
        Observable.error(new IllegalStateException(message))
    }, _.close())
      .doOnError(e => log.error(s"saving $pid/$streamId threw ${e.getClass.getName}: ${e.getMessage}"))
      .flatMapWith(_ => Observable.timer(timeout milliseconds))((u, _) => u)
  }
}

object StreamUpdater {

  val log = LoggerFactory.getLogger(getClass)

  def apply(timeout: Long = 1000L)(implicit settings: Settings): StreamUpdater =
    if (settings.testMode) new TestStreamUpdater
    else new FedoraStreamUpdater(timeout)
}
