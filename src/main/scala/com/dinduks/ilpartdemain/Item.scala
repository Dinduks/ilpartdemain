package com.dinduks.ilpartdemain

case class Item(title: String, location: String, price: Long, time: String, link: java.net.URL) {
  def id: String = {
    link.toString.replaceAll("http.?://(www\\.)?leboncoin\\.fr/.*/", "").replaceAll("\\.htm.*", "")
  }

  override def toString: String = {
    s"Title: $title\nLocation: $location\nPrice: $price\nTime: $time\nLink: $link"
  }
}