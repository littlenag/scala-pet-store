package io.github.pauljamescleary.petstore.infrastructure.endpoint

import java.net.URL

import io.github.pauljamescleary.BuildInfo
import cats.data.{NonEmptyList, OptionT}
import cats.effect.{ContextShift, Effect}
import org.http4s.CacheDirective.`no-cache`
import org.http4s.{HttpRoutes, _}
import org.http4s.dsl.Http4sDsl
import org.http4s.headers.{`Cache-Control`, `Content-Type`}
import org.log4s.getLogger
import scalatags.Text.TypedTag

import scala.concurrent.ExecutionContext
import scala.language.higherKinds

class FrontendEndpoints[F[_]: Effect: ContextShift] extends Http4sDsl[F] {

  private[this] val logger = getLogger

  /* Needed for service composition via <+> */
  import cats.implicits._

  import org.http4s.server.staticcontent.webjarService
  import org.http4s.server.staticcontent.WebjarService.{WebjarAsset, Config}

  val F = implicitly[Effect[F]]

  val publicAssetExtensions = List(".html", ".js", ".css", ".map", ".ttf", ".woff", ".woff2", ".eot", ".svg", ".png", ".ico")

  val ec = ExecutionContext.Implicits.global

  def getResource(pathInfo: String): F[URL] = F.delay(getClass.getResource(pathInfo))

  val indexHTML: TypedTag[String] = {
    import scalatags.Text.all._
    import scalatags.Text.tags2.title

    html(
      head(
        meta(charset := "UTF-8"),
        title("Scala Pet Store")
      ),
      body(
        // This div is where our SPA is rendered.
        div(`class` := "app-container", id := "root"),
        script(`type`:= "text/javascript", src := s"/webjars/${BuildInfo.name}/${BuildInfo.version}/shared-bundle.js"),
        script(`type`:= "text/javascript", src := s"/webjars/${BuildInfo.name}/${BuildInfo.version}/app-bundle.js")
      )
    )
  }

  val webjars: HttpRoutes[F] = webjarService[F](
    Config(
      // only allow js/font/image assets
      filter = (asset: WebjarAsset) => publicAssetExtensions.exists(ext => asset.asset.endsWith(ext)),
      blockingExecutionContext = ec
    )
  )

  val endpoints: HttpRoutes[F] =
    HttpRoutes.of[F] {
      // serves our index.html that starts our SPA
      case GET -> Root =>
        Ok(indexHTML().render)
          .map(
            _.withContentType(`Content-Type`(MediaType.text.html, Charset.`UTF-8`))
              .putHeaders(`Cache-Control`(NonEmptyList.of(`no-cache`())))
          )

      case req @ GET -> "fonts" /: path =>
        logger.info(s"font: $path")
        webjars(req.withPathInfo(s"/${BuildInfo.name}/${BuildInfo.version}/fonts" + path.toString))
          .fold(NotFound())(F.pure)
          .flatten

      case req @ GET -> "webjars" /: path =>
        logger.info(s"webjars: $path")
        webjars(req.withPathInfo(path.toString))
            .fold(NotFound())(F.pure)
            .flatten

      case req @ GET -> "resources" /: path if publicAssetExtensions.exists(req.pathInfo.endsWith) =>
        StaticFile.fromResource[F](path.toString, ec, req.some)
            .orElse(OptionT.liftF(getResource(path.toString)).flatMap(StaticFile.fromURL[F](_, ec, req.some)))
            .map(_.putHeaders(`Cache-Control`(NonEmptyList.of(`no-cache`()))))
            .fold(NotFound())(F.pure)
            .flatten
    }
}

object FrontendEndpoints {
  def endpoints[F[_]: Effect: ContextShift](): HttpRoutes[F] =
    new FrontendEndpoints[F].endpoints
}