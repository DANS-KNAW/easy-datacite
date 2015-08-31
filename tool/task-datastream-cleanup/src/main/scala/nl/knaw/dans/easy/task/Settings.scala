package nl.knaw.dans.easy.task

import scala.xml.transform.RuleTransformer

/**
 *
 * @param streamId id of datastream of a fedora object
 * @param idPatterns patterns of fedora objects IDs,
 *                   eg: easy-dataset:*, easy-f*:*
 * @param updater stores a changed data stream,
 *                provide a dummy for a test mode
 * @param transformer changes the content of an XML datastream
 *                    NB: redundant for RemoveDataStreams but not worth the trouble to differentiate
 * @param batchSize the number of pids to fetch at once,
 *                  a token allows to read the next batch from fedora
 */
case class Settings(streamId: String,
                    idPatterns: List[String],
                    updater: StreamUpdater,
                    transformer: RuleTransformer,
                    batchSize: Int = 100)
