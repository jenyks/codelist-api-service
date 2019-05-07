package com.pagero.services.codelist.restapi

import akka.actor.ActorSystem
import akka.event.slf4j.SLF4JLogging
import akka.http.scaladsl.Http
import akka.http.scaladsl.Http.ServerBinding
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives.{authenticateOAuth2Async, complete, handleExceptions}
import akka.http.scaladsl.server.directives.Credentials
import akka.http.scaladsl.server.{ExceptionHandler, Route}
import akka.stream.ActorMaterializer
import com.pagero.servicecomm.ServiceClient
import com.pagero.servicecomm.context.MessageContext
import com.pagero.services.codelist.restapi.actors.ServiceActor
import com.pagero.services.codelist.exception.NotAuthorizedException
import com.pagero.services.codelist.security.CorsSupport

import scala.concurrent.Future

object RestServer extends ServiceActor with CorsSupport with SLF4JLogging {

  implicit val system = ActorSystem("my-system")
  implicit val materializer = ActorMaterializer()
  implicit val executionContext = system.dispatcher

  def startServer(address: String, port: Int, serviceClient: ServiceClient): Future[ServerBinding] = {
    val routes = Route.seal {
      handleErrors {
        corsHandler {
          authenticateOAuth2Async(realm = "codelist-api", tokenChecker()) { implicit ctx =>
            route(serviceClient)
          }
        }
      }
    }
    Http().bindAndHandle(routes, address, port)
  }

  private val exceptionHandler = ExceptionHandler {
    case e: Exception =>
      log.error(e.getMessage)
      complete(StatusCodes.InternalServerError)
  }

  def tokenChecker()(credentials: Credentials): Future[Option[MessageContext]] = {
    log.info("Credentials provided: " + credentials.toString)
    credentials match {
      case Credentials.Provided(token) =>
        Future {
          Option(MessageContext().withToken(token))
        }
      case _ =>
        log.error("The Bearer is missing.")
        Future.failed(NotAuthorizedException(Option("The Bearer is missing.")))
    }
  }

  private val handleErrors = handleExceptions(exceptionHandler)
}
