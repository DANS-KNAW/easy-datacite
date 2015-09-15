package nl.knaw.dans.easy.ta1

import scala.xml.Node

/**
 * Configuration of desired changes.
 *
 * @param updater stores a changed data stream or just logs an intended change if in test mode
 *        @example ''updater:'' `new CommandLineOptions(args).parse()`, defaults to test mode
 * @param batchSize the number of pids to fetch at once
 * @param datastreamId id of a datastream of a fedora object to change, for possible values examine
 *                     a [[http://teasy.dans.knaw.nl:8080/fedora/objects/ID/datastreams sample]],
 *                     fill in the ID for a representative object
 * @param objectQueries queries to retrieve fedora objects to change
 *            @example ''objectQueries:'' `List("pid~easy-dataset:*", "pid~easy-f*:*")` for
 *                     datasets, files, folders and form definitions or `List("creator~*SMGI")` for
 *                     datasets containing the text "SMGI" in the creator field.
 *                     See help for 'specific fields' on
 *                     [[http://teasy.dans.knaw.nl/fedora/objects fedora/objects]],
 *                     the 'fields to display' (and more easy-specific values?) go before the
 *                     condition ("~" in the examples) in a query.
 */
abstract case class Settings(updater: Updater = Updater.get(testMode = true),
                             batchSize: Int = 100,
                             objectQueries: List[String],
                             datastreamId: String) {
  /**
   * Changes the content of a datastream.
   *
   * @param pid fedora id of the object to change
   * @param content old content of the XML datastream
   * @return new  content of the XML datastream
   */
  def transform(pid: String, content: Node): Seq[Node]
}
