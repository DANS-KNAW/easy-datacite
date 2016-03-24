package nl.knaw.dans.easy.task

import java.io.PrintWriter

import nl.knaw.dans.easy.DataciteService


/**
 *
 * @param testMode true/false
 * @param sendEmails true/false
 * @param updater stores a changed data stream, provide a dummy for a test mode
 * @param writer writes the changed dataset pids into a file
 * @param writer_2 writes information about the changed files in datasets
 */

case class Settings(testMode : Boolean,
                    sendEmails : Boolean,
                    updater: StreamUpdater,
                    ds : DataciteService,
                    writer : PrintWriter,
                    writer_2 : PrintWriter)