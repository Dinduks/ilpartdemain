package com.dinduks.ilpartdemain

import java.net.URL

import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import us.codecraft.xsoup.Xsoup

import scala.collection.JavaConversions._
import scala.util.{Failure, Success, Try}

class Scraper {
  def getItemsInfo(url: URL): Seq[Item] = {
    val doc: Document = Jsoup.connect(url.toURI.toASCIIString).get
    val eventualContainer = Try(Xsoup.compile("//div[contains(@class, \"react-tabs__tab-panel\")]//ul")
      .evaluate(doc)
      .getElements
      .get(0))

    eventualContainer match {
      case Failure(_) => Nil
      case Success(container) =>
        container.children filter { element =>
          element.childNodeSize == 3
        } flatMap { element =>
          val linkTag = Option(element.getElementsByTag("a")).map(_.first)

          val title = Option(element.getElementsByAttributeValue("itemprop", "name").text).filterNot(_.isEmpty)
          val location = Option(element.getElementsByAttributeValue("itemprop", "availableAtOrFrom").text).filterNot(_.isEmpty)
          val link = linkTag.map(t => buildURL(t.attr("href")))
          val time = Option(element.getElementsByAttributeValue("itemprop", "availabilityStarts").text).filterNot(_.isEmpty)
          val price = Option(element.getElementsByAttributeValue("itemprop", "price").text).filterNot(_.isEmpty).map { price =>
            parsePrice(price.replaceAll("\\u00A0€( (C|H).C.)?", ""))
          }
          val image = None // TODO: images are not in the HTML

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
    if (url.startsWith("//")) new URL("https:" + url)
    else if (url.startsWith("http")) new URL(url)
    else new URL("https://www.leboncoin.fr" + url)
  }

  private def parsePrice(price: String): Long =
    parseLong(if (price.contains("-")) price.split("-")(1).trim else price)

  private def parseLong(s: String): Long =
    s.replace(" ", "").replace(",", "").toLong
}
