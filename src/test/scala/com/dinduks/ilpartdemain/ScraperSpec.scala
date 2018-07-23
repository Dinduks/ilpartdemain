package com.dinduks.ilpartdemain

import java.net.URL

import org.scalatest._
import org.scalatest.matchers.ShouldMatchers

class ScraperSpec extends FunSpec with ShouldMatchers {
  val scraper = new Scraper

  describe("Scraper") {
    describe("buildURL") {
      it("should add the protocol to URL's that don't contain the protocols") {
        val url = scraper.buildURL("//foo.bar/baz")
        url.getProtocol should be("https")
        url.getHost should be ("foo.bar")
        url.getPath should be("/baz")
      }

      it("should add the host name to URL's without host") {
        val url = scraper.buildURL("/baz")
        url.getProtocol should be("https")
        url.getHost should be ("www.leboncoin.fr")
        url.getPath should be("/baz")
      }

      it("should do nothing when the URL is absolute") {
        val url = scraper.buildURL("https://foo.bar/baz")
        url.getProtocol should be("https")
        url.getHost should be("foo.bar")
        url.getPath should be("/baz")
      }
    }
  }
}