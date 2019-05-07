package com.pagero.services.codelist.restapi.actors

import akka.actor.{Actor, Props}
import akka.event.slf4j.SLF4JLogging
import com.pagero.servicecomm.ServiceClient
import com.pagero.servicecomm.context.MessageContext
//import com.pagero.services.codelist.model.StaticMappingRequest
import com.pagero.services.staticmapping.spec.{CodelistSaveRequest, StaticMappingResponse, StaticMappingRequest}

import scala.concurrent.Await
import scala.concurrent.duration._

object RequestActor {
  def props(staticmappingClient: ServiceClient)(implicit ctx: MessageContext) = Props(classOf[RequestActor], staticmappingClient)
}

class RequestActor(staticmappingClient: ServiceClient) extends Actor with SLF4JLogging {

  private val TIMEOUT = 40.seconds

  override def receive: PartialFunction[Any, Unit] = {

    case request: StaticMappingRequest =>
      log.info(s"Returning Name")
      sender() ! "Test"
    case request: CodelistSaveRequest =>
      //sender() ! "Codelist Saved test"
    Await.result(staticmappingClient.request(request), TIMEOUT) match {
      case response: StaticMappingResponse =>
        log.info(s"No. of types")
        sender() ! "Codelist Saved"
    }

  }
}
