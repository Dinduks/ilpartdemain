package com.dinduks.ilpartdemain

import java.io._
import java.net.URL
import java.util.Date
import javax.inject.Inject

import Item._
import org.apache.commons.io.FileUtils

import scala.collection.JavaConversions._
import scala.io.Source

class Program @Inject() (scraper: Scraper, mailer: Mailer) {
  def run(watchedItemsFilename: String, processedItemsFilename: String,
          delayMins: Long, to: String, cc: Option[String]) {
    while (true) {
      val urlsSource = Source.fromFile(watchedItemsFilename)
      val processedItemsSource = Source.fromFile(processedItemsFilename)

      val urls: Seq[URL] = getURLsOfItems(urlsSource)
      val processedItems: Seq[String] = getProcessedItems(processedItemsSource)

      val itemsInfo = getItemsInfo(urls)(scraper)
      println(s"${new Date} — Processing ${itemsInfo.size} items(s)…")
      val newProcessedItems: Seq[String] = itemsInfo
        .filterNot(item => processedItems.contains(item.id))
        .map { item =>
          mailer.sendItemEmail(item, to, cc)
          item.id
        }

      FileUtils.writeLines(new File(processedItemsFilename), processedItems ++ newProcessedItems)

      urlsSource.close()
      processedItemsSource.close()

      Thread.sleep(delayMins * 1000 * 60)
    }
  }
}
