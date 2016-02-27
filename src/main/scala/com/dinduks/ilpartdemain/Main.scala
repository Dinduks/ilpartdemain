package com.dinduks.ilpartdemain

import java.io._
import java.net.SocketTimeoutException

import scala.util.Try

object Main {
  private val mailer = new Mailer

  def main(args: Array[String]) {
    if (args.length < 2) {
      System.err.println("Error: Not enough arguments.")
      return
    }

    val itemsFileName          = args(0)
    val processedItemsFileName = args(1)
    val delay                  = args(2).toLong * 1000 * 60
    val to                     = args(3)
    val cc                     = Try(args(4)).map(Option(_)).getOrElse(None)

    try {
      val program = new Program(new Scraper, mailer)
      program.run(itemsFileName, processedItemsFileName, delay, to, cc)
    } catch {
      case e: SocketTimeoutException =>
        e.printStackTrace()
        Thread.sleep(5 * 60 * 1000)
        main(args)
      case e: Throwable =>
        e.printStackTrace()
        mailer.sendErrorEmail(e, to)
        Thread.sleep(5 * 60 * 1000)
        main(args)
    }
  }
}
