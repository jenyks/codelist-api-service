package com.pagero.services.codelist.restapi.actors

import akka.actor.{Actor, Props}
import akka.event.slf4j.SLF4JLogging
import com.pagero.servicecomm.ServiceClient
import com.pagero.servicecomm.context.MessageContext
import com.pagero.services.codelist.model.{CodeList, CodeListViewRequest, CodeLists, JsonSupport, StaticMappingRequest}
import com.pagero.services.staticmapping.spec.{CodelistSaveRequest, StaticMappingResponse}

import scala.concurrent.Await
import scala.concurrent.duration._

object RequestActor {
  def props(staticmappingClient: ServiceClient)(implicit ctx: MessageContext) = Props(classOf[RequestActor], staticmappingClient)
}

class RequestActor(staticmappingClient: ServiceClient) extends Actor with SLF4JLogging with JsonSupport{

  private val TIMEOUT = 40.seconds

  override def receive: PartialFunction[Any, Unit] = {
    case request: StaticMappingRequest =>
      log.info(s"Returning Name")
      sender() ! request.name
    case request: CodelistSaveRequest =>
      //sender() ! "Codelist Saved test"
    Await.result(staticmappingClient.request(request), TIMEOUT) match {
      case response: StaticMappingResponse =>
        log.info(s"Returning Codelist save response")
        sender() ! "Codelist Saved"
    }
    case request : CodeListViewRequest =>
        sender() ! CodeLists(retriveCodeList(request.companiId))
  }

  def retriveCodeList (id : String) : List[CodeList] = {
        val c1 = CodeList("1","Company 1","Company 1 Codelist 1","description 1")
        val c2 = CodeList("1","Company 1","Company 1 Codelist 2","description 2")
        val c3 = CodeList("2","Company 2","Company 2 Codelist 1","description 3")
        val c4 = CodeList("2","Company 2","Company 2 Codelist 2","description 4")
        val c5 = CodeList("2","Company 2","Company 2 Codelist 3","description 5")
        val list1 : List[CodeList] = List(c1,c2)
        val list2 : List[CodeList] = List(c3,c4,c5)
    id match {
      case "1" => list1
      case "2" => list2
    }
  }




}
