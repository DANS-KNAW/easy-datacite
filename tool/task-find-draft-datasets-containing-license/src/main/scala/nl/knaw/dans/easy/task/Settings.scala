package nl.knaw.dans.easy.task

import java.io.PrintWriter

import scala.xml.transform.RuleTransformer

/**
 * @param writer writes the output into a file
 * @param batchSize the number of pids to fetch in a batch, a token allows to read the next batch from fedora
 */

case class Settings(writer : PrintWriter,
                    batchSize: Int = 100)