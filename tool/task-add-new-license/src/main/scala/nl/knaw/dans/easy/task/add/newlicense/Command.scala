/**
 * Copyright (C) 2016-2017 DANS - Data Archiving and Networked Services (info@dans.knaw.nl)
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
package nl.knaw.dans.easy.task.add.newlicense

import java.io.{ByteArrayInputStream, ByteArrayOutputStream, OutputStream}

import com.yourmediashelf.fedora.client.FedoraClient
import resource.Using

import scala.io.Source
import nl.knaw.dans.easy.task.add.newlicense.{CommandLineOptions => cmd}
import nl.knaw.dans.easy.license.LicenseCreator
import nl.knaw.dans.easy.license
import org.slf4j.LoggerFactory

import scala.language.postfixOps
import scala.util.{Failure, Success, Try}

object Command {
  val log = LoggerFactory.getLogger(getClass)

  def main(args: Array[String]): Unit = {
    log.debug("Starting command line interface")

    try {
      implicit val (parameters, outputFile) = cmd.parse(args)
      if (!parameters.doUpdate) log.info(s"Running in testmode; NO CHANGES WILL BE MADE")
      for {
        reader <- Using.fileInputStream(parameters.pidsfile)
        writer <- Using.fileWriter()(outputFile)
      } Source.fromInputStream(reader)
        .getLines()
        .toStream
        .map(datasetId => (datasetId, processLine(datasetId)))
        .foreach {
          case (datasetId, Success(_)) => if (parameters.doUpdate) writer.write(s"[UPDATED] $datasetId is successful\n")
                                          else writer.write(s"[TEST MODE] $datasetId is successful\n")
          case (datasetId, Failure(e)) => writer.write(s"[FAILED] ${e.getMessage}\n")
        }
    }
    catch {
      case e: Throwable => log.error("An error was caught in main:", e)
    }

    def processLine(datasetId : String)(implicit p : Parameters): Try[Unit] = {
      resource.managed(new ByteArrayOutputStream)
        .acquireAndGet(outputStream => {
          for {
            _ <- createLicense(datasetId)(outputStream)
            _ <- ingestLicense(datasetId, outputStream)
          } yield ()
        })
    }

  }

  def createLicense(datasetId: String)(outputStream: OutputStream)(implicit p: Parameters): Try[Unit] = Try {
    if (!p.doUpdate)
      log.info(s"[TEST MODE] Creating license of $datasetId")
    else
      log.info(s"Creating license of $datasetId")
    LicenseCreator(license.Parameters(p.licenseResource, datasetId, false, p.fedoraClient, p.ldapContext))
      .createLicense(outputStream)
      .toBlocking
      .toList
  }

  def ingestLicense(datasetId: String, outputStream: ByteArrayOutputStream)(implicit p: Parameters): Try[Unit] = Try {
    if (!p.doUpdate)
      log.info(s"[TEST MODE] Skip Ingesting license of $datasetId")
    else {
      log.info(s"Ingesting license of $datasetId")
      FedoraClient.addDatastream(datasetId, "DATASET_LICENSE")
        .controlGroup("M")
        .versionable(true)
        .mimeType("application/pdf")
        .dsLabel("license.pdf")
        .content(new ByteArrayInputStream(outputStream.toByteArray))
        .checksumType("SHA-1")
        .execute(p.fedoraClient)
    }

  }
}
