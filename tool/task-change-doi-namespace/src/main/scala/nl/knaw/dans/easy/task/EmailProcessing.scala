
package nl.knaw.dans.easy.task

import nl.knaw.dans.easy.task.Email.Mail
import nl.knaw.dans.easy.task.Main._

import scala.collection.mutable
import scala.io.Source
import scala.util.{Success, Failure}

object EmailProcessing {

  val DEPOSITORS_INPUT_FILE = "/depositors.txt"
  val EMAIL_FROM = "info@dans.knaw.nl"
  val EMAIL_FROM_NAME = "Data Archiving and Networked Services"
  val EMAIL_CC = List("info@dans.knaw.nl")
  val EMAIL_SUBJECT = "DOI namespace changed"
  val EMAIL_PROLOG = "This email has been sent to you because...\nblah, blah, blah....\nblah, blah, blah....\n\nThe following datasets have been modified: "
  val EMAIL_EPILOG = "\n\nCheers,\nEASY TEAM"

  def process()(implicit settings: Settings, depositors : mutable.Map[String, String]) = {

    if (settings.testMode) log.info("IN TEST MODE => NO EMAILS WILL BE SENT!")
    if (!settings.sendEmails) log.info(s"sendEmails=${settings.sendEmails} => NO EMAILS WILL BE SENT!")
    if (!settings.testMode && settings.sendEmails) log.info("SENDING EMAILS\n")
    sendEmails
  }

  def sendEmails()(implicit settings: Settings, depositors : mutable.Map[String, String]) = {

    val emailAddresses = readDepositorEmailAddresses
    for ((depositor, changedDatasets) <- depositors){
      emailAddresses.get(depositor) match {
        case None =>
          log.warn(s"Email address was not found for the depositor $depositor; concerns datasets:$changedDatasets")
          settings.writer_2.println(s"Email address was not found for the depositor $depositor; concerns datasets:$changedDatasets")
        case Some(receiver) =>
          sendEmail(settings.testMode, settings.sendEmails, receiver, EMAIL_CC, EMAIL_SUBJECT, EMAIL_PROLOG + changedDatasets + EMAIL_EPILOG)
      }
    }
  }

  def readDepositorEmailAddresses() = {

    val stream = getClass.getResourceAsStream(DEPOSITORS_INPUT_FILE)
    Source.fromInputStream(stream).getLines.map(line => (line.split(" ").head, line.split(" ").last)).toMap
  }

  def sendEmail(testMode: Boolean, sendEmails: Boolean, receiver: String, cc: Seq[String], subject: String, message: String)(implicit settings: Settings) = {

    val receivers = List(receiver)
    val emailContents = s"from: $EMAIL_FROM $EMAIL_FROM_NAME - to: $receivers - cc: $cc\nsubject: $subject\nmessage: $message"
    log.info(s"An email to be sent:\n$emailContents")

    if (!testMode && sendEmails) {
      Email.send a new Mail(from = (EMAIL_FROM, EMAIL_FROM_NAME), to = receivers, cc = cc, subject = subject, message = message)
      match {
        case Failure(e) =>
          log.error(s"Error in sending email to $receivers: ${e.getCause}")
        case Success(_) =>
          log.info(s"An email has been sent to $receivers")
          settings.writer_2.println(s"An email has been sent to $receivers")
      }
    }
  }
}