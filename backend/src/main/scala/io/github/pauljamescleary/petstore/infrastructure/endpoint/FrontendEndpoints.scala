package io.github.pauljamescleary.petstore.infrastructure.endpoint

import cats.data.{NonEmptyList, OptionT}
import cats.effect.Effect
import io.circe.generic.auto._
import io.circe.syntax._
import org.http4s.CacheDirective.`no-cache`
import org.http4s._
import org.http4s.circe._
import org.http4s.dsl.Http4sDsl
import org.http4s.headers.`Cache-Control`
import org.log4s.getLogger

import scala.language.higherKinds

class FrontendEndpoints[F[_]: Effect] extends Http4sDsl[F] {

  private[this] val logger = getLogger

  /* Need Instant Json Encoding */
  import io.circe.java8.time._

  /* Needed for service composition via |+| */
  import cats.implicits._

  import io.github.pauljamescleary.petstore.Server.appContextShift

  import org.http4s.server.staticcontent.webjarService
  import org.http4s.server.staticcontent.WebjarService.{WebjarAsset, Config}

  def getResource(pathInfo: String) = implicitly[Effect[F]].delay(getClass.getResource(pathInfo))

  // only allow js assets
  def isJsAsset(asset: WebjarAsset): Boolean = asset.asset.endsWith(".js")

  def allowAsset(asset: WebjarAsset) = true

  val supportedStaticExtensions = List(".html", ".js", ".map", ".css", ".png", ".ico")

  // http://localhost:8080/webjars/bootstrap/3.3.7/dist/js/bootstrap.js
  // library // version // asset
  val webjars = {
    webjarService[F](
      Config(
        filter = allowAsset
      )
    ).orNotFound
  }

  def placeOrderEndpoint: HttpRoutes[F] =
    HttpRoutes.of[F] {
      case req @ GET -> "webjars" /: path =>
        logger.info(s"webjars: $path")
        webjars.run(req.withPathInfo(path.toString))

      case req @ GET -> "resources" /: path if supportedStaticExtensions.exists(req.pathInfo.endsWith) =>
        StaticFile.fromResource[F](path.toString, req.some)
          .orElse(OptionT.liftF(getResource(path.toString)).flatMap(StaticFile.fromURL[F](_, req.some)))
          .map(_.putHeaders(`Cache-Control`(NonEmptyList.of(`no-cache`()))))
          .fold(NotFound())(F.pure)
          .flatten
    }

  def endpoints(): HttpRoutes[F] = placeOrderEndpoint
}

object FrontendEndpoints {
  def endpoints[F[_]: Effect](): HttpRoutes[F] =
    new FrontendEndpoints[F].endpoints()
}
