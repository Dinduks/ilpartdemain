package com.dinduks.ilpartdemain

import java.net.URL

import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.select.Elements
import us.codecraft.xsoup.Xsoup

import scala.collection.JavaConversions._
import scala.util.{Success, Failure, Try}

object Scraper {
  def getItemsInfo(url: URL): Seq[Item] = {
    val doc: Document = Jsoup.connect(url.toURI.toASCIIString).get
    val triedContainer = Try(Xsoup.compile("//div[contains(@class, \"list-lbc\")]")
      .evaluate(doc)
      .getElements
      .get(0))

    triedContainer match {
      case Failure(_) => Nil
      case Success(container) =>
        container.children filterNot { element =>
          element.classNames.contains("clear") || element.classNames.contains("oas")
        } flatMap { element =>
          val title = Option(element.getElementsByClass("title").text).filterNot(_.isEmpty)
          val location = Option(element.getElementsByClass("placement").text).filterNot(_.isEmpty)
          val link = Option(element.attr("href")).filterNot(_.isEmpty).map(buildURL)
          val time = Option(element.getElementsByClass("date").text).filterNot(_.isEmpty)
          val price = Option(element.getElementsByClass("price").text).filterNot(_.isEmpty).map { price =>
            parsePrice(price.replaceAll("\\u00A0€", ""))
          }
          val image = element.getElementsByTag("img").headOption.map(tag => buildURL(tag.attr("src")))

          link.map { link =>
            new Item(title.getOrElse("_"),
              location.getOrElse("_"),
              price.getOrElse(-1L),
              time.getOrElse("_"),
              link,
              image)
          }
        }
    }
  }

  def buildURL(url: String): URL = {
    if (url.startsWith("//")) new URL("http:" + url)
    else new URL(url)
  }

  private def parsePrice(price: String): Long =
    parseLong(if (price.contains("-")) price.split("-")(1).trim else price)

  private def parseLong(s: String): Long =
    s.replace(" ", "").replace(",", "").toLong
}