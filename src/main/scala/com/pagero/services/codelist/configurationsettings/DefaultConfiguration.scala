package com.pagero.services.codelist.configurationsettings

import com.typesafe.config.ConfigFactory

trait DefaultConfiguration extends Configuration {
  override lazy val config = ConfigFactory.load()

  lazy val servicePort = config.getInt("http.port")
}
