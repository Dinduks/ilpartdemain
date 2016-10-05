package com.dinduks.ilpartdemain

import com.typesafe.config.ConfigFactory

object Config {
  private val config = ConfigFactory.load()

  object email {
    val subjectPrefix = config.getString("email.subjectPrefix")
    object from {
      val name  = config.getString("email.from.name")
      val email = config.getString("email.from.email")
    }
    object smtp {
      val host = config.getString("email.smtp.host")
      val port = config.getInt("email.smtp.port")
      val ssl  = config.getBoolean("email.smtp.ssl")
      val username = config.getString("email.smtp.username")
      val password = config.getString("email.smtp.password")
    }
  }

  object app {
    val itemsFileName = config.getString("app.items.filename")
    val processedItemsFileName = config.getString("app.processed.items.filename")
    val delayMins = config.getInt("app.delay_minutes")
    val to = config.getString("app.to")
    val cc = Option(config.getString("app.cc")).filter(_.nonEmpty)
  }
}
