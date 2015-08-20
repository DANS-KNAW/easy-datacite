package nl.knaw.dans.easy

import java.io.InputStream

import scala.io.Source

package object task {

  def precalculatedReader(input: InputStream): Iterator[Array[String]] = {
    // TODO log and skip the rows with less than three fields (theoretically)
    Source.fromInputStream(input).getLines().map(_.split("\t"))
  }
}
