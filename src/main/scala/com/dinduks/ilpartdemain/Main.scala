package com.dinduks.lesuperboncoin

import java.io.File
import java.net.URL
import java.util.Date

import org.apache.commons.io.FileUtils
import org.apache.commons.mail.DefaultAuthenticator
import org.apache.commons.mail.SimpleEmail

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
      case e: Throwable =>
        sendErrorEmail(e, to)
        main(args)
    }
  }

  private def run(itemsFile: File, processedItemsFile: File, delay: Long, to: String, cc: Option[String]) {
    val urls = readSearchURLs(itemsFile)
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
    sendEmail(subject, item.toString, to: String, cc: Option[String])
  }

  private def sendErrorEmail(e: Throwable, to: String): Unit = {
    val subject = s"${Config.email.subjectPrefix} Exception thrown"
    sendEmail(subject, e.getStackTrace.toString, to: String)
  }

  private def sendEmail(subject: String, body: String, to: String, cc: Option[String] = None) {
    val email = new SimpleEmail
    email.setHostName(Config.email.smtp.host)
    email.setSmtpPort(Config.email.smtp.port)
    email.setAuthenticator(new DefaultAuthenticator(Config.email.smtp.username, Config.email.smtp.password))
    email.setSSL(true)
    email.setFrom(Config.email.from.email, Config.email.from.name)
    email.setSubject(subject)
    email.setMsg(body)
    email.addTo(to)
    cc.foreach(email.addCc)
    email.send
  }

  private def readSearchURLs(file: File): Seq[URL] = Source
    .fromFile(file)
    .getLines()
    .map(line => new URL(line.split("#")(0)))
    .toList
}
