package com.dinduks.ilpartdemain

import java.net.SocketTimeoutException

object Main {
  private val mailer = new Mailer

  def main(args: Array[String]) {
    try {
      val program = new Program(new Scraper, mailer)
      program.run(Config.app.itemsFileName, Config.app.processedItemsFileName,
        Config.app.delayMins, Config.app.to, Config.app.cc)
    } catch {
      case e: SocketTimeoutException =>
        e.printStackTrace()
        Thread.sleep(5 * 60 * 1000)
        main(args)
      case e: Throwable =>
        e.printStackTrace()
        mailer.sendErrorEmail(e, Config.app.to)
        Thread.sleep(5 * 60 * 1000)
        main(args)
    }
  }
}
