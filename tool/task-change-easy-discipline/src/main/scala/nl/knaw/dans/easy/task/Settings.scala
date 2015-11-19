package nl.knaw.dans.easy.task

import java.io.PrintWriter

import scala.xml.transform.RuleTransformer

/**
 *
 * @param testMode true/false
 * @param updater stores a changed data stream, provide a dummy for a test mode
 * @param writer writes the changed dataset pids into a file
 */

case class Settings(testMode : Boolean,
                    pidsfile : String,
                    odis : String,
                    ndis : String,
                    updater: StreamUpdater,
                    writer : PrintWriter) {

  override def toString: String =
    s"Settings: test=$testMode,  pidsfile=$pidsfile, odis=$odis, ndis=$ndis"
}