package com.pagero.services.codelist.configurationsettings

import com.typesafe.config.Config

trait Configuration {
  def config: Config
}
