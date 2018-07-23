package com.dinduks.ilpartdemain

import java.net.URL

import scala.io.Source

case class Item(title: String,
                location: String,
                price: Long,
                time: String,
                link: URL,
                thumbnailURL: Option[URL] = None) {
  def id: String = {
    link.toString.replaceAll("http.?://(www\\.)?leboncoin\\.fr/[a-z_]*/", "").replaceAll("\\.htm.*", "")
  }

  override def toString: String = {
    s"Title: $title\nLocation: $location\nPrice: $price\nTime: $time\nLink: $link"
  }

  def toEmail: String = s"""
      |<html>
      |<body>
      |  ${thumbnailURL.map(url => s"""<img src="$url" alt="Thumbnail">""").getOrElse("")}
      |  <ul>
      |    <li>Title: $title</li>
      |    <li>Location: $location</li>
      |    <li>Price: ${if (price >= 0) price else "N/A"}</li>
      |    <li>Time: $time</li>
      |    <li>Link: <a href="$link">$link</a></li>
      |  </ul>
      |</body>
      |</html>
    """.stripMargin.trim
}

object Item {
  def getItemsInfo(urls: Seq[URL])(implicit scraper: Scraper) = urls.flatMap(scraper.getItemsInfo).distinct

  def getURLsOfItems(source: Source): Seq[URL] = source
    .getLines
    .filter(line => !line.trim.isEmpty && !line.startsWith("#"))
    .map(new URL(_))
    .toSeq

  def getProcessedItems(source: Source): Seq[String] = source.getLines.toList.distinct
}
