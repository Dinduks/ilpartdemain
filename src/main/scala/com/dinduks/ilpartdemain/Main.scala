package com.dinduks.ilpartdemain

import java.io.{PrintWriter, StringWriter, File}
import java.net.{SocketTimeoutException, URL}
import java.util.Date

import org.apache.commons.io.FileUtils
import org.apache.commons.mail.{HtmlEmail, DefaultAuthenticator}

import scala.collection.JavaConversions._
import scala.io.Source
import scala.util.Try

object Main {
  def main(args: Array[String]) {
    if (args.length < 2) {
      System.err.println("Error: Not enough arguments.")
      return
    }

    val itemsFileName          = args(0)
    val processedItemsFileName = args(1)
    val delay                  = args(2).toLong * 1000 * 60
    val to                     = args(3)
    val cc                     = Try(args(4)).map(Option(_)).getOrElse(None)

    try {
      run(new File(itemsFileName), new File(processedItemsFileName), delay, to, cc)
    } catch {
      case e: SocketTimeoutException =>
        e.printStackTrace()
        Thread.sleep(5 * 60 * 1000)
        main(args)
      case e: Throwable              =>
        e.printStackTrace()
        sendErrorEmail(e, to)
        Thread.sleep(5 * 60 * 1000)
        main(args)
    }
  }

  private def run(itemsFile: File, processedItemsFile: File, delay: Long, to: String, cc: Option[String]) {
    val urls = readSearchURLs(Source.fromFile(itemsFile))
    while (true) {
      val processedItems = Source.fromFile(processedItemsFile).getLines().toList
      val itemsInfo = urls.flatMap(Scraper.getItemsInfo)
      println(s"${new Date} — Processing ${itemsInfo.size} items(s)…")
      val newProcessedItems = itemsInfo
        .filterNot(item => processedItems.contains(item.id))
        .map { item =>
          sendItemEmail(item, to, cc)
          item.id
        }

      FileUtils.writeLines(processedItemsFile, processedItems ++ newProcessedItems)

      Thread.sleep(delay)
    }
  }

  private def sendItemEmail(item: Item, to: String, cc: Option[String]): Unit = {
    val subject = s"${Config.email.subjectPrefix} New item: ${item.title}"
    sendEmail(subject, item.toEmail.toString, to: String, cc: Option[String])
  }

  private def sendErrorEmail(e: Throwable, to: String): Unit = {
    val subject = s"${Config.email.subjectPrefix} Exception thrown"
    sendEmail(subject, getStackTrace(e), to: String)
  }

  private def sendEmail(subject: String, body: String, to: String, cc: Option[String] = None) {
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

  def getStackTrace(t: Throwable): String = {
    val sw = new StringWriter()
    t.printStackTrace(new PrintWriter(sw))
    sw.toString
  }

  def readSearchURLs(source: Source): Seq[URL] = source
    .getLines()
    .filter(line => !line.trim.isEmpty && !line.startsWith("#"))
    .map(new URL(_))
    .toList
}
