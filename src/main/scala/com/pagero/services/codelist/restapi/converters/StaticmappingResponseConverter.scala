package com.pagero.services.codelist.restapi.converters

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import com.pagero.services.codelist.model.CodelistInfo
import spray.json.DefaultJsonProtocol

trait StaticmappingResponseConverter extends SprayJsonSupport with DefaultJsonProtocol {
  implicit val codeListInfo = jsonFormat4(CodelistInfo)
}
