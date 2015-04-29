package com.dinduks.ilpartdemain

import java.net.URL

import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.select.Elements
import us.codecraft.xsoup.Xsoup

import scala.collection.JavaConversions._

object Scraper {
  def getItemsInfo(url: URL): Seq[Item] = {
    val doc: Document = Jsoup.connect(url.toURI.toASCIIString).get
    val elements: Elements = Xsoup.compile("//div[contains(@class, \"list-lbc\")]")
      .evaluate(doc)
      .getElements.get(0)
      .children

    elements
      .filterNot( element =>
        element.classNames.contains("clear") || element.classNames.contains("oas")
      )
      .map { element =>
        val title = Option(element.getElementsByClass("title").text).filterNot(_.isEmpty)
        val location = Option(element.getElementsByClass("placement").text).filterNot(_.isEmpty)
        val link = Option(element.attr("href")).filterNot(_.isEmpty).map(new URL(_))
        val price = Option(element.getElementsByClass("price").text).filterNot(_.isEmpty).map { price =>
          parsePrice(price.replaceAll("\\u00A0€", ""))
        }
        (title, location, price, link) match {
          case (Some(t), Some(lo), Some(p), Some(li)) => Some(new Item(t, lo, p, li))
          case _ => None
        }
      }
      .flatten
  }

  private def parsePrice(price: String): Long =
    parseLong(if (price.contains("-")) price.split("-")(1).trim else price)

  private def parseLong(s: String): Long =
    s.replace(" ", "").replace(",", "").toLong
}