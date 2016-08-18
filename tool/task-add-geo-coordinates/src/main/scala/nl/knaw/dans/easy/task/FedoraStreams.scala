/**
  * Copyright (C) 2016 DANS - Data Archiving and Networked Services (info@dans.knaw.nl)
  *
  * Licensed under the Apache License, Version 2.0 (the "License");
  * you may not use this file except in compliance with the License.
  * You may obtain a copy of the License at
  *
  *         http://www.apache.org/licenses/LICENSE-2.0
  *
  * Unless required by applicable law or agreed to in writing, software
  * distributed under the License is distributed on an "AS IS" BASIS,
  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  * See the License for the specific language governing permissions and
  * limitations under the License.
  */
package nl.knaw.dans.easy.task

import com.yourmediashelf.fedora.client.FedoraClient
import com.yourmediashelf.fedora.client.request.FedoraRequest
import nl.knaw.dans.easy.task.FedoraStreams.log
import org.slf4j.LoggerFactory
import resource._

import scala.util.{Failure, Success, Try}
import scala.xml.{Elem, XML}

trait FedoraStreams {

  def updateDatastream(pid: String, streamId: String, content: String): Try[Unit]

  def getXml(pid: String, streamId: String): Try[Elem]
}

abstract class AbstractFedoraFedoraStreams(timeout: Long = 1000L) extends FedoraStreams {

  def updateDatastream(pid: String, streamId: String, content: String) = {
    log.info(s"updating $pid/$streamId")
    val request = FedoraClient.modifyDatastream(pid, streamId).content(content)
    executeRequest(pid, streamId, request)
  }

  def getXml(pid: String, streamId: String): Try[Elem] = Try(
    managed(FedoraClient.getDatastreamDissemination(pid, streamId).execute())
      .acquireAndGet(fedoraResponse => XML.load(fedoraResponse.getEntityInputStream))
  )

  def executeRequest[T](pid: String, streamId: String, request: FedoraRequest[T]): Try[Unit]
}

class TestFedoraStreams extends AbstractFedoraFedoraStreams {
  def executeRequest[T](pid: String, streamId: String, request: FedoraRequest[T]) =
    Success(log.info(s"test-mode: skipping request for $pid/$streamId"))
}

class FedoraFedoraStreams(timeout: Long = 1000L) extends AbstractFedoraFedoraStreams {
  def executeRequest[T](pid: String, streamId: String, request: FedoraRequest[T]) = {
    log.info(s"executing request for $pid/$streamId")
    managed(request.execute())
      .acquireAndGet(_.getStatus match {
        case 200 => log.info(s"saved $pid/$streamId")
          Success(Unit)
        case status =>
          Failure(new IllegalStateException(s"got status $status"))
      })
  }
}

object FedoraStreams {

  val log = LoggerFactory.getLogger(getClass)

  def apply(testMode: Boolean, timeout: Long = 1000L): FedoraStreams =
    if (testMode) new TestFedoraStreams
    else new FedoraFedoraStreams(timeout)
}