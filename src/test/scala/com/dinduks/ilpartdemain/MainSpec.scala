package com.dinduks.ilpartdemain

import org.scalatest._
import org.scalatest.matchers.ShouldMatchers

import scala.io.Source

class MainSpec extends FunSpec with ShouldMatchers {
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

      val l = Main.getURLsOfItems(source)
      l(0).toString should be("http://foo")
      l(1).toString should be("http://baz")
      l(2).toString should be("http://herp")
      l(3).toString should be("http://derp")
    }

    describe("getProcessedItems") {
      val source = Source.fromString("url0\nurl0")

      val items = Main.getProcessedItems(source)
      items.size should be(1)
      items.headOption should be(Some("url0"))
    }
  }
}
