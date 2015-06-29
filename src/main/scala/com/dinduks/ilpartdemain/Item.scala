package com.dinduks.ilpartdemain

import java.net.URL

case class Item(title: String,
                location: String,
                price: Long,
                time: String,
                link: URL,
                thumbnailURL: Option[URL] = None) {
  def id: String = {
    link.toString.replaceAll("http.?://(www\\.)?leboncoin\\.fr/.*/", "").replaceAll("\\.htm.*", "")
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
      |    <li>Link: $link</li>
      |  </ul>
      |</body>
      |</html>
    """.stripMargin.trim
}
