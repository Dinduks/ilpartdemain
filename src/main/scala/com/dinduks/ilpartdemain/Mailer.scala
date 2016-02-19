package com.dinduks.ilpartdemain

import java.io._

import org.apache.commons.mail._

class Mailer {
  def sendItemEmail(item: Item, to: String, cc: Option[String]): Unit = {
    val subject = s"${Config.email.subjectPrefix} New item: ${item.title}"
    sendEmail(subject, item.toEmail.toString, to: String, cc: Option[String])
  }

  def sendErrorEmail(e: Throwable, to: String): Unit = {
    val subject = s"${Config.email.subjectPrefix} Exception thrown"
    sendEmail(subject, e, to: String)
  }

  def sendEmail(subject: String, body: String, to: String, cc: Option[String] = None) {
    val email = new HtmlEmail
    email.setCharset("utf-8")
    email.setHostName(Config.email.smtp.host)
    email.setSmtpPort(Config.email.smtp.port)
    email.setAuthenticator(new DefaultAuthenticator(Config.email.smtp.username, Config.email.smtp.password))
    email.setSSL(Config.email.smtp.ssl)
    email.setFrom(Config.email.from.email, Config.email.from.name)
    email.setSubject(subject)
    email.setMsg(body)
    email.addTo(to)
    cc.foreach(email.addCc)
    email.send
  }

  private implicit def getStackTrace(t: Throwable): String = {
    val sw = new StringWriter()
    t.printStackTrace(new PrintWriter(sw))
    sw.toString
  }
}