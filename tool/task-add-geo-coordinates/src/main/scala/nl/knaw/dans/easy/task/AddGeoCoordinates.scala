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

import org.apache.commons.csv.{CSVFormat, CSVParser, CSVRecord}
import org.slf4j.LoggerFactory

import scala.collection.JavaConverters._
import scala.collection.JavaConversions.asScalaBuffer
import scala.io.Source
import scala.xml.transform.{RewriteRule, RuleTransformer}
import scala.xml.{Elem, Node, NodeSeq}

object AddGeoCoordinates {
  val log = LoggerFactory.getLogger(getClass)

  def main(args: Array[String]): Unit = {
    log.debug("Starting command line interface")
    implicit val settings: Settings = CommandLineOptions.parse(args)

    log.info(s"Running with $settings")
    if (settings.testMode){
      log.info(s"Running in testmode; NO CHANGES WILL BE MADE")
    }

    val fixer = new FedoraObjectFixer(addCoordinatesToEmdTransformer)

    getCsvRecords(settings.csvFilename).foreach(fixer.fix)

    settings.writer.close()
    log.info("Done all.")
  }

  def getCsvRecords(pidsFile : String) : List[CSVRecord] = {
    val rawContent = Source.fromFile(pidsFile).mkString
    val parser = CSVParser.parse(rawContent, CSVFormat.RFC4180)

    parser.getRecords
      .filter(hasPid)
      .filter(paramsContainOnlyDigits)
      .filter(hasAllParams)
      .toList
  }

  def hasPid(paramsRecord: CSVRecord) = {
    if (paramsRecord.size() > 0 && !paramsRecord.get(0).trim().isEmpty) {
      true
    } else {
      log.info(s"invalid record: ${paramsRecord.toString}, no Pid in first column")
      false
    }
  }

  def hasAllParams(paramsRecord: CSVRecord) = {
    val minNumberOfColumns = 7 // one for the pid and six for the coordinates
    val numberOfColumns = paramsRecord.size()
    if (numberOfColumns >= minNumberOfColumns) {
      true
    } else {
      log.info(s"invalid record: ${paramsRecord.toString}, needs at least $minNumberOfColumns columns found $numberOfColumns")
      false
    }
  }

  /**
    * Check coordinates: should be empty or contain only digits
    * Note that x and y coordinates could further be validated for RD ranges, but we don't
    */
  def paramsContainOnlyDigits(paramsRecord: CSVRecord) = {
    paramsRecord.asScala
      .drop(1)
      .map(_.trim)
      .zipWithIndex
      .map { case (c, i) =>
        if (c.forall(_.isDigit)) true
        else {
          log.info(s"invalid record: ${paramsRecord.toString}, column ${i + 2} contains not only digits")
          false
        }
      }
      .forall(identity)
  }

  val addCoordinatesToEmdTransformer = new XmlDatastreamTransformer {
    override def getStreamId = "EMD"
    def constructSpatial(paramsRecord: CSVRecord) : Node = {
      // assume we have valid RD coordinates here, validation must be done before calling this function
      val x = paramsRecord.get(1).trim
      val y = paramsRecord.get(2).trim

      if (x.nonEmpty && y.nonEmpty ) {
        // point
        <eas:spatial>
          <eas:point eas:scheme="RD">
            <eas:x>{x}</eas:x>
            <eas:y>{y}</eas:y>
          </eas:point>
        </eas:spatial>
      } else {
        // if we miss a point coordinate it must be a box
        val minx = paramsRecord.get(3).trim
        val maxx = paramsRecord.get(4).trim
        val miny = paramsRecord.get(5).trim
        val maxy = paramsRecord.get(6).trim
        <eas:spatial>
          <eas:box eas:scheme="RD">
            <eas:north>{maxy}</eas:north>
            <eas:east>{maxx}</eas:east>
            <eas:south>{miny}</eas:south>
            <eas:west>{minx}</eas:west>
          </eas:box>
        </eas:spatial>
      }
    }

    override def transform(paramsRecord: CSVRecord, n : Node) : NodeSeq = {
      log.info(s"transforming  ${paramsRecord.get(0)}/$getStreamId with record: ${paramsRecord.toString}")
      val newSpatial = constructSpatial(paramsRecord)
      new RuleTransformer(new RewriteRule {
        override def transform(n: Node): Seq[Node] = n match {
          case Elem(prefix, "coverage", attribs, scope, existingChildren @ _*) =>
            Elem(prefix, "coverage", attribs, scope, false, existingChildren ++ newSpatial : _*)
          // Note that the more readable version below does not work because it messes up the namespace bindings
          // case <emd:coverage>{existingChildren @ _*}</emd:coverage> => <emd:coverage>{existingChildren}{newSpatial}</emd:coverage>
          case other => other
        }
      }).transform(n)
    }
  }

}
