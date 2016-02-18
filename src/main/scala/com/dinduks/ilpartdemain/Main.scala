package com.dinduks.ilpartdemain

import java.io._
import java.net.{SocketTimeoutException, URL}
import java.util.Date
import javax.inject.Inject

import org.apache.commons.io.FileUtils
import org.apache.commons.mail.{HtmlEmail, DefaultAuthenticator}

import scala.collection.JavaConversions._
import scala.io.Source
import scala.util.Try

object Main {
  val scraper = new Scraper
  val program = new Program(scraper)

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
      program.run(new File(itemsFileName), new File(processedItemsFileName), delay, to, cc)
    } catch {
      case e: SocketTimeoutException =>
        e.printStackTrace()
        Thread.sleep(5 * 60 * 1000)
        main(args)
      case e: Throwable =>
        e.printStackTrace()
        program.sendErrorEmail(e, to)
        Thread.sleep(5 * 60 * 1000)
        main(args)
    }
  }
}

class Program @Inject() (scraper: Scraper) {
  def run(watchedItemsFile: File, processedItemsFile: File, delay: Long, to: String, cc: Option[String]) {
    while (true) {
      val urls = getURLsOfItems(Source.fromFile(watchedItemsFile))
      val processedItems = getProcessedItems(Source.fromFile(processedItemsFile))
      val itemsInfo = getItemsInfo(urls)
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

  def getItemsInfo(urls: Seq[URL]) = urls.flatMap(scraper.getItemsInfo).toSet.toList

  def getURLsOfItems(source: Source): Seq[URL] = source
    .getLines
    .filter(line => !line.trim.isEmpty && !line.startsWith("#"))
    .map(new URL(_))
    .toList

  def getProcessedItems(source: Source) = source.getLines.toSet.toList

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
