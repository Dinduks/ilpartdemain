package com.dinduks.ilpartdemain

import java.io._
import java.util.Date
import javax.inject.Inject

import Item._
import org.apache.commons.io.FileUtils

import scala.collection.JavaConversions._
import scala.io.Source

class Program @Inject() (scraper: Scraper, mailer: Mailer) {
  def run(watchedItemsFile: File, processedItemsFile: File, delay: Long, to: String, cc: Option[String]) {
    while (true) {
      val urls = getURLsOfItems(Source.fromFile(watchedItemsFile))
      val processedItems: Seq[String] = getProcessedItems(Source.fromFile(processedItemsFile))
      val itemsInfo = getItemsInfo(urls)(scraper)
      println(s"${new Date} — Processing ${itemsInfo.size} items(s)…")
      val newProcessedItems: Seq[String] = itemsInfo
        .filterNot(item => processedItems.contains(item.id))
        .map { item =>
          mailer.sendItemEmail(item, to, cc)
          item.id
        }

      FileUtils.writeLines(processedItemsFile, processedItems ++ newProcessedItems)

      Thread.sleep(delay)
    }
  }
}
