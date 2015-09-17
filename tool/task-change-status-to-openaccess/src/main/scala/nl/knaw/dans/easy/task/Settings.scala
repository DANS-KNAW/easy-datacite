package nl.knaw.dans.easy.task

import java.io.PrintWriter

import scala.xml.transform.RuleTransformer

/**
 *
 * @param testMode true/false
 * @param rightsHolder name of the Rights Holder (wildcards also allowed)
 * @param updater stores a changed data stream, provide a dummy for a test mode
 * @param writer writes the changed dataset pids into a file
 * @param batchSize the number of pids to fetch in a batch, a token allows to read the next batch from fedora
 */

case class Settings(testMode : Boolean,
                    rightsHolder : String,
                    updater: StreamUpdater,
                    writer : PrintWriter,
                    batchSize: Int = 100)