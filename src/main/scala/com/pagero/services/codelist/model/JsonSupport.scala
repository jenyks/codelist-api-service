package com.pagero.services.codelist.model

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import spray.json.DefaultJsonProtocol._

trait JsonSupport extends SprayJsonSupport {

  implicit val codeListJson = jsonFormat4(CodeList)
  implicit val codeListsJson = jsonFormat1(CodeLists)

}
