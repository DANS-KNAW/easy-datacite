
package nl.knaw.dans.easy.task

import nl.knaw.dans.easy.task.Email.Mail
import nl.knaw.dans.easy.task.Main._

import scala.collection.mutable
import scala.io.Source
import scala.util.{Failure, Success}

object EmailProcessing {

  val DEPOSITORS_INPUT_FILE = "/depositors.txt"
  val EMAIL_FROM = "info@dans.knaw.nl"
  val EMAIL_FROM_NAME = "Data Archiving and Networked Services"
  val EMAIL_CC = List("info@dans.knaw.nl")
  val EMAIL_SUBJECT = "Attentie: verkeerde voorvoegsels in de DOI's in de EASY-deposit confirmation e-mails van september 2015"

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
          sendEmail(settings.testMode, settings.sendEmails, receiver, EMAIL_CC, EMAIL_SUBJECT, genEmailMessage(changedDatasets))
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

  def genEmailMessage(datasets: String): String = {
    s"""Geachte data-depositor,

      Een van de voornaamste diensten voor het duurzaam toegankelijk maken van digitale onderzoeksdata is het toekennen van een unieke identificatie aan de dataset: de zogeheten Persistent Identifier.
      Datasets in EASY worden voorzien van een DOI (Digital Object Identifier). De DOI wordt automatisch toegekend bij het deponeren, na het versturen (submitten) van de dataset in EASY wordt deze direct met u gedeeld in de ontvangstbevestiging (deposit confirmation).

      Alle DOI’s in EASY hebben hetzelfde begin: ‘http://dx.doi.org/10.17026/dans-’. De rest van de link is per dataset verschillend en uniek.

      Tot onze spijt is het recentelijk aan het licht gekomen dat er over de maand september 2015 een fout in de configuratie van de DOI-generator zat. Hierdoor zijn in deze maand verkeerde DOI’s met u gedeeld in de ontvangstbevestiging.
      De fout heeft geen betrekking op het unieke gedeelte van de DOI, maar op het vaste gedeelte. Hier is het foutieve nummer 10.5072 komen te staan in plaats van het juiste nummer 10.17026.
      De referenties in EASY zijn allemaal automatisch hersteld. Heeft u echter zelf inmiddels op basis van het verkeerde nummer referenties naar uw dataset gebruikt, dan moeten wij u helaas vragen om deze referenties zelf te achterhalen en te corrigeren.
      Onze oprechte excuses voor het ongemak. We hebben maatregelen genomen om er voor te zorgen dat de fout zich niet opnieuw voor kan doen.

      Voor u heeft het betrekking op de volgende datasets: $datasets

      Neem voor nadere informatie contact op met chris.baars@dans.knaw.nl"""
  }
}
