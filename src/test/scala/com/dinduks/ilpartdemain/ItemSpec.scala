package com.dinduks.ilpartdemain

import java.net.URL

import org.scalatest._
import org.scalatest.matchers.ShouldMatchers

class ItemSpec extends FunSpec with ShouldMatchers {
  describe("Item") {
    describe("id") {
      it("should return a correctly formatted id") {
        val items = List(
          new Item("", "", -1L, "", new URL("http://www.leboncoin.fr/foo-bar/0123456789.htm?ca=12_s")),
          new Item("", "", -1L, "", new URL("http://leboncoin.fr/foo-bar/0123456789.htm?ca=12_s")),
          new Item("", "", -1L, "", new URL("https://www.leboncoin.fr/foo-bar/0123456789.htm?ca=12_s")),
          new Item("", "", -1L, "", new URL("https://leboncoin.fr/foo-bar/0123456789.htm?ca=12_s")),
          new Item("", "", -1L, "", new URL("https://www.leboncoin.fr/foo-bar/0123456789.htm")),
          new Item("", "", -1L, "", new URL("https://leboncoin.fr/foo-bar/0123456789.htm"))
        )

        items.foreach(item => item.id should be("0123456789"))
      }
    }
  }
}