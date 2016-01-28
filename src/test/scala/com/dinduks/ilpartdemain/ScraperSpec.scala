package com.dinduks.ilpartdemain

import java.net.URL

import org.scalatest._
import org.scalatest.matchers.ShouldMatchers

class ScraperSpec extends FunSpec with ShouldMatchers {
  describe("Scraper") {
    describe("buildURL") {
      it("should add the protocol to URL's that don't contain the protocols") {
        val url = Scraper.buildURL("//foo.bar/baz")
        url.getProtocol should be("http")
        url.getHost should be ("foo.bar")
        url.getPath should be("/baz")
      }

      it("should do nothing when the URL contains the protocol") {
        val url = Scraper.buildURL("https://foo.bar/baz")
        url.getProtocol should be("https")
        url.getHost should be("foo.bar")
        url.getPath should be("/baz")
      }
    }
  }
}