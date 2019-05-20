package com.pagero.services.codelist.restapi.actors

import akka.actor.{ActorSystem, Props}
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives.{path, pathPrefix, post, _}
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.directives.RouteDirectives
import akka.pattern.ask
import akka.stream.ActorMaterializer
import akka.util.Timeout
import com.google.protobuf2.ByteString
import com.pagero.servicecomm.ServiceClient
import com.pagero.servicecomm.context.MessageContext
import com.pagero.services.codelist.model.{CodeList, CodeListViewRequest, CodeLists, CodelistInfo, JsonSupport, StaticMappingRequest}
import com.pagero.services.codelist.restapi.converters.StaticmappingResponseConverter
import com.pagero.services.staticmapping.spec.CodelistSaveRequest

import scala.concurrent.Future
import scala.concurrent.duration._
import scala.language.postfixOps

object ServiceActor {
  def props(staticmappingClient: ServiceClient, routes: RouteDirectives) =
    Props(classOf[ServiceActor], staticmappingClient)
}

trait ServiceActor extends StaticmappingResponseConverter with JsonSupport {

  implicit val system: ActorSystem
  implicit val materializer: ActorMaterializer
  implicit val TIMEOUT = Timeout(5 seconds)

  def route(staticmappingClient: ServiceClient)(implicit context: MessageContext): Route = {
    pathPrefix(pm = "api" / "v1") {
      path(pm = "codelists") {
        get {
          parameters('id.as[String]){id =>
            val requestActor = system.actorOf(RequestActor.props(staticmappingClient))
            val request = CodeListViewRequest(id)
            val responseList = (requestActor ? request).mapTo[CodeLists]
            rejectEmptyResponse {
              complete(responseList)
            }

//            onSuccess(requestActor ? request) {
//              case successfulResult: CodeLists =>
//                complete(StatusCodes.OK -> successfulResult)
//            }

          }
        }
      } ~
      path(pm = "codelists") {
        post {
          entity(as[CodelistInfo]) { codelistInfo =>
            val requestActor = system.actorOf(RequestActor.props(staticmappingClient))
            val decodedPayload = new String(java.util.Base64.getDecoder.decode(codelistInfo.payload))

            val request = CodelistSaveRequest(Option(codelistInfo.codelistName), Option(codelistInfo.codelistDescription), codelistInfo.companies.toList,
              Option(ByteString.copyFrom(decodedPayload.getBytes("UTF-8"))))
            onSuccess(requestActor ? request) {
              case successfulResult: String =>
                complete(StatusCodes.OK -> successfulResult)
            }
          }
        }
      }

    }
  }

  /*private def completeResponse(requestActor: ActorRef, request: StaticMappingRequest): Route = {
    onSuccess(requestActor ? request) {
      case successfulResult: String =>
        complete(StatusCodes.OK -> successfulResult)
    }
  }*/

}
