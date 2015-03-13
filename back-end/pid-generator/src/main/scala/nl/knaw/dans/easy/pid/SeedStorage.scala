package nl.knaw.dans.easy.pid

import java.io.File
import org.apache.commons.io.FileUtils._

sealed trait SeedStorage {
  def write(seed: Long): Unit

  def check(seed: Long): Boolean

  def read: Long
}

case class FileBasedSeedStorage(file: File) extends SeedStorage {

  override def write(seed: Long): Unit = writeStringToFile(file, seed.toString)

  override def check(seed: Long): Boolean = read == seed

  override def read: Long = java.lang.Long.parseLong(readFileToString(file))

}