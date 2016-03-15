package com.dinduks.ilpartdemain

import java.net.URL

import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import us.codecraft.xsoup.Xsoup

import scala.collection.JavaConversions._
import scala.util.{Success, Failure, Try}

class Scraper {
  def getItemsInfo(url: URL): Seq[Item] = {
    val doc: Document = Jsoup.connect(url.toURI.toASCIIString).get
    val eventualContainer = Try(Xsoup.compile("//ul[contains(@class, \"tabsContent\")]")
      .evaluate(doc)
      .getElements
      .get(0))

    eventualContainer match {
      case Failure(_) => Nil
      case Success(container) =>
        container.children filterNot { element =>
          element.classNames.contains("apn-na") || element.classNames.contains("oas")
        } flatMap { element =>
          val title = Option(element.getElementsByClass("item_title").text).filterNot(_.isEmpty)
          val location = Option(element.getElementsByClass("item_supp")(1).text).filterNot(_.isEmpty)
          val link = Option(element.getElementsByClass("list_item").attr("href")).map(buildURL)
          val time = Option(element.getElementsByClass("item_supp")(2).text).filterNot(_.isEmpty)
          val price = Option(element.getElementsByClass("item_price").text).filterNot(_.isEmpty).map { price =>
            parsePrice(price.replaceAll("\\u00A0€", ""))
          }
          val image = for {
            tag <- element.getElementsByClass("item_imagePic").headOption
            subTag <- tag.children.headOption
          } yield buildURL("https:" + subTag.attr("data-imgSrc"))

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