package com.pagero.services.codelist.exception

case class NotAuthorizedException(message: Option[String] = None,
                                  cause: Throwable = null) extends Exception(message.orNull, cause)
