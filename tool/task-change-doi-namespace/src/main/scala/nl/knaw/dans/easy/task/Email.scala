/*****************************************************************************************************************************

Borrowed from 'Sending mails fluently in Scala' by Marius Soutier (https://gist.github.com/mariussoutier/3436111).

  Example usages:

    send a new Mail (
    from = ("john.smith@mycompany.com", "John Smith"),
    to = "boss@mycompany.com",
    cc = "hr@mycompany.com",
    subject = "Import stuff",
    message = "Dear Boss..."
  )

  send a new Mail (
    from = "john.smith@mycompany.com" -> "John Smith",
    to = Seq("dev@mycompany.com", "marketing@mycompany.com"),
    subject = "Our New Strategy (tm)",
    message = "Please find attach the latest strategy document.",
    richMessage = "Here's the <blink>latest</blink> <strong>Strategy</strong>..."
  )

  send a new Mail (
    from = "john.smith@mycompany.com" -> "John Smith",
    to = "dev@mycompany.com" :: "marketing@mycompany.com" :: Nil,
    subject = "Our 5-year plan",
    message = "Here is the presentation with the stuff we're going to for the next five years.",
    attachment = new java.io.File("/home/boss/important-presentation.ppt")
  )

 *****************************************************************************************************************************/

package nl.knaw.dans.easy.task

import scala.util.Try

object Email {

  sealed abstract class MailType
  case object Plain extends MailType
  case object Rich extends MailType
  case object MultiPart extends MailType

  case class Mail(
                   from: (String, String), // (email -> name)
                   to: Seq[String],
                   cc: Seq[String] = Seq.empty,
                   bcc: Seq[String] = Seq.empty,
                   subject: String,
                   message: String,
                   richMessage: Option[String] = None,
                   attachment: Option[(java.io.File)] = None
                   )

  object send {
    def a(mail: Mail) = Try {
      import org.apache.commons.mail._

      val format =
        if (mail.attachment.isDefined) MultiPart
        else if (mail.richMessage.isDefined) Rich
        else Plain

      val commonsMail: Email = format match {
        case Plain => new SimpleEmail().setMsg(mail.message)
        case Rich => new HtmlEmail().setHtmlMsg(mail.richMessage.get).setTextMsg(mail.message)
        case MultiPart => {
          val attachment = new EmailAttachment()
          attachment.setPath(mail.attachment.get.getAbsolutePath)
          attachment.setDisposition(EmailAttachment.ATTACHMENT)
          attachment.setName(mail.attachment.get.getName)
          new MultiPartEmail().attach(attachment).setMsg(mail.message)
        }
      }

      commonsMail.setHostName("127.0.0.1")
      commonsMail.setSmtpPort(25);

      // Can't add these via fluent API because it produces exceptions
      mail.to foreach (commonsMail.addTo(_))
      mail.cc foreach (commonsMail.addCc(_))
      mail.bcc foreach (commonsMail.addBcc(_))

      commonsMail.
        setFrom(mail.from._1, mail.from._2).
        setSubject(mail.subject).
        send()
    }
  }
}