package nl.knaw.dans.easy.pid

import java.io.File
import org.apache.commons.io.FileUtils._
import scala.util.Try
import java.io.IOException
import scala.util.Success
import scala.util.Failure

sealed trait SeedStorage {
  def write(seed: Long): Try[Unit]

  def check(seed: Long): Try[Boolean]

  def read: Try[Long]
}

case class FileBasedSeedStorage(file: File) extends SeedStorage {

  override def write(seed: Long): Try[Unit] =
    try {
      Success(writeStringToFile(file, seed.toString))
    } catch {
      case e: IOException => Failure(e)
    }

  override def check(seed: Long): Try[Boolean] =
    read match {
      case Success(s) => Success(s == seed)
      case Failure(e) => Failure(e)
    }

  override def read: Try[Long] =
    try {
      Success(java.lang.Long.parseLong(readFileToString(file)))
    } catch {
      case e: NumberFormatException => Failure(e)
    }

}