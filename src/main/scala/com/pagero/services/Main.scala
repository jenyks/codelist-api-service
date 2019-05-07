package com.pagero.services

import akka.actor.Cancellable
import akka.event.slf4j.SLF4JLogging
import com.pagero.service.ServiceApplication
import com.pagero.services.codelist.configurationsettings.DefaultConfiguration
import com.pagero.services.codelist.restapi.RestServer
import com.pagero.services.codelist_api.CodelistApiServiceInfo
import com.pagero.services.staticmapping.spec.StaticmappingSpec

import scala.concurrent.duration._
import scala.language.postfixOps

object Main extends ServiceApplication with CodelistApiServiceInfo with SLF4JLogging with DefaultConfiguration {

  import scala.concurrent.ExecutionContext.Implicits.global

  val requestPrefetch = 10
  val messagePrefetch = 10

  val address = "0.0.0.0"
  val ENV_HOSTNAME = "PUBLIC_HOSTNAME"
  val ENV_PORT = "PUBLIC_PORT_8080"
  val HEART_BEAT_INITIAL_DELAY = 1 second
  val MONITORING_SERVICE_ROLE = "api/v1_codelists"

  val staticmappingClient = createClient(StaticmappingSpec)

  private var heartbeats: Option[Cancellable] = None

  override def init(): Unit = {
    initRestService()
    initHeartbeat()
  }

  override def shutdown(): Unit = {
    shutdownHeartbeat()
    super.shutdown()
  }

  private def initRestService(): Unit = {
    RestServer.startServer(address, servicePort, staticmappingClient)
  }

  private def initHeartbeat(): Unit = {

    heartbeats = Option(system.scheduler.schedule(HEART_BEAT_INITIAL_DELAY, heartbeatInterval seconds) {
      val host = System.getenv(ENV_HOSTNAME)
      val port = System.getenv(ENV_PORT)
      if (publishToEtcd)
        monitoredServiceAPI.refreshFrontendRole(instanceId,
          MONITORING_SERVICE_ROLE,
          host,
          port,
          heartbeatInterval.seconds + 5.seconds)
    })
  }

  private def shutdownHeartbeat(): Unit = {

    heartbeats foreach (_.cancel())
  }
}