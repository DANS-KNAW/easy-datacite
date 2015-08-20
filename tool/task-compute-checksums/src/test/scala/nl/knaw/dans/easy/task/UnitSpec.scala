package nl.knaw.dans.easy.task

import java.io.ByteArrayInputStream
import java.net.URL

import nl.knaw.dans.easy.lib.NiceFedoraRestApi
import org.slf4j.helpers.SubstituteLogger

abstract class UnitSpec extends nl.knaw.dans.easy.lib.UnitSpec
{
  /** prevents blocking a build when deasy and/or teasy is not avaialable */
  def assumeFedoraIsAvailable(fedora: NiceFedoraRestApi): Unit = {
    val fedoraIsAvailable = try {
      val connection = new URL(fedora.baseUrl).openConnection()
      connection.setConnectTimeout(2000)
      connection.setReadTimeout(2000)
      connection.getInputStream
      true
    } catch {
      case _: Exception => false
    }
    assume(fedoraIsAvailable, s"${fedora.baseUrl} not available")
  }

  def toInputStream(s: String): ByteArrayInputStream = {
    new ByteArrayInputStream(s.getBytes)
  }

  def captureError: (StringBuilder, SubstituteLogger {def error(s: String): Unit}) = {
    val logged = new StringBuilder()
    val logger = new SubstituteLogger("mock") {
      override def error(s: String) = {
        logged.append(s"$s\n")
      }
    }
    (logged, logger)
  }

  def captureInfo: (StringBuilder, SubstituteLogger {def info(s: String): Unit}) = {
    val logged = new StringBuilder()
    val logger = new SubstituteLogger("mock") {
      override def info(s: String) = {
        logged.append(s"$s\n")
      }
    }
    (logged, logger)
  }

  def captureErrorsAndInfos: (
    StringBuilder, 
    StringBuilder, SubstituteLogger {
      def info(s: String): Unit; 
      def error(s: String): Unit
    }) = {
    val errors = new StringBuilder()
    val infos = new StringBuilder()
    val logger = new SubstituteLogger("mock") {
      override def error(s: String) = {
        errors.append(s"$s\n")
      }

      override def info(s: String) = {
        infos.append(s"$s\n")
      }
    }
    (errors, infos, logger)
  }}
