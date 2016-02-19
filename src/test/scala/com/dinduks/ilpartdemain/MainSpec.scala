package com.dinduks.ilpartdemain

import java.net.URL

import com.google.inject.Guice
import net.codingwell.scalaguice.InjectorExtensions._
import org.scalatest._
import org.scalatest.matchers.ShouldMatchers

import scala.io.Source

class MainSpec extends FunSpec with ShouldMatchers {
  val injector = Guice.createInjector()

  private implicit val scraper = injector.instance[Scraper]
  private val mailer = injector.instance[Mailer]

  val program = new Program(scraper, mailer)

  describe("Main") {
    describe("readSearchURLs") {
      val source = Source.fromString(
        """
          |
          |http://foo
          |# http://bar
          |http://baz
          |#
          |   http://herp
          |http://derp
          |
        """.stripMargin)

      val l = Item.getURLsOfItems(source)
      l(0).toString should be("http://foo")
      l(1).toString should be("http://baz")
      l(2).toString should be("http://herp")
      l(3).toString should be("http://derp")
    }

    describe("getProcessedItems") {
      val source = Source.fromString("url0\nurl0")

      val items = Item.getProcessedItems(source)
      items.size should be(1)
      items.headOption should be(Some("url0"))
    }

    describe("getItemsInfo") {
      class MyScaper extends Scraper {
        override def getItemsInfo(url: URL) = Item("", "", -1, "", null) :: Nil
      }

      val urls = new URL("http://foo.com") :: new URL("http://bar.com") :: Nil
      val itemsInfo = Item.getItemsInfo(urls)(new MyScaper)
      itemsInfo.size should be(1)
    }
  }
}
