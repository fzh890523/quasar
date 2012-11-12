/*
 *  ____    ____    _____    ____    ___     ____ 
 * |  _ \  |  _ \  | ____|  / ___|  / _/    / ___|        Precog (R)
 * | |_) | | |_) | |  _|   | |     | |  /| | |  _         Advanced Analytics Engine for NoSQL Data
 * |  __/  |  _ <  | |___  | |___  |/ _| | | |_| |        Copyright (C) 2010 - 2013 SlamData, Inc.
 * |_|     |_| \_\ |_____|  \____|   /__/   \____|        All Rights Reserved.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the 
 * GNU Affero General Public License as published by the Free Software Foundation, either version 
 * 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; 
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See 
 * the GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License along with this 
 * program. If not, see <http://www.gnu.org/licenses/>.
 *
 */
package com.precog.ingest
package service

import kafka._

import com.precog.daze._
import com.precog.common.{ Path, Event, EventMessage }
import com.precog.common.security._

import org.specs2.mutable.Specification
import org.specs2.specification._
import org.scalacheck.Gen._

import akka.actor.ActorSystem
import akka.dispatch.{ Future, ExecutionContext, Await }
import akka.util.Duration

import org.joda.time._

import org.streum.configrity.Configuration
import org.streum.configrity.io.BlockFormat

import scalaz.{Success, NonEmptyList}
import scalaz.Scalaz._

import blueeyes.concurrent.test._

import blueeyes.core.data._
import blueeyes.bkka.AkkaDefaults
import blueeyes.core.service.test.BlueEyesServiceSpecification
import blueeyes.core.http.HttpResponse
import blueeyes.core.http.HttpStatus
import blueeyes.core.http.HttpStatusCodes._
import blueeyes.core.http.MimeType
import blueeyes.core.http.MimeTypes
import blueeyes.core.http.MimeTypes._

import blueeyes.json._

trait TestAPIKeys {
  import TestAPIKeyManager._
  val TestAPIKey = testUID
  val TrackingAPIKey = usageUID
  val ExpiredAPIKey = expiredUID
}

trait TestIngestService extends BlueEyesServiceSpecification with IngestService with TestAPIKeys with AkkaDefaults with MongoAPIKeyManagerComponent {
  val asyncContext = defaultFutureDispatch

  val config = """
    security {
      test = true
      mongo {
        mock = true
        servers = [localhost]
        database = test
      }
    }
  """

  override val configuration = "services { ingest { v1 { " + config + " } } }"

  def usageLoggingFactory(config: Configuration) = new ReportGridUsageLogging(TrackingAPIKey) 

  val messaging = new CollectingMessaging

  def queryExecutorFactory(config: Configuration) = new NullQueryExecutor {
    lazy val actorSystem = ActorSystem("ingestServiceSpec")
    implicit lazy val executionContext = ExecutionContext.defaultExecutionContext(actorSystem)
  }

  def eventStoreFactory(config: Configuration): EventStore = {
    val defaultAddresses = NonEmptyList(MailboxAddress(0))

    val routeTable = new ConstantRouteTable(defaultAddresses)

    new KafkaEventStore(new EventRouter(routeTable, messaging), 0)
  }

  override def apiKeyManagerFactory(config: Configuration) = TestAPIKeyManager.testAPIKeyManager[Future]

  implicit def jValueToFutureJValue = new Bijection[JValue, Future[JValue]] {
    def apply(x: JValue) = Future(x)
    def unapply(f: Future[JValue]) = Await.result(f, Duration(500, "millis"))
  }

  def track[A](
      contentType: MimeType,
      sync: Boolean = true,
      apiKey: Option[String] = Some(TestAPIKey),
      path: String = "unittest"
    )(data: A)(implicit
      bi: Bijection[A, Future[JValue]],
      bi2: Bijection[A, ByteChunk]): Future[(HttpResponse[JValue], List[Event])] = {
    val svc = service.contentType[A](contentType).path(if (sync) "/sync/fs/" else "/async/fs/")
    messaging.messages.clear()
    for {
      response <- apiKey.map(svc.query("apiKey", _)).getOrElse(svc).post[A](path)(data)
      content <- response.content map (a => bi(a) map (Some(_))) getOrElse Future(None)
    } yield {
      (response.copy(content = content), messaging.messages.toList collect {
        case EventMessage(_, event) => event
      })
    }

  }

  override implicit val defaultFutureTimeouts: FutureTimeouts = FutureTimeouts(20, Duration(1, "second"))
  val shortFutureTimeouts = FutureTimeouts(5, Duration(50, "millis"))
}


