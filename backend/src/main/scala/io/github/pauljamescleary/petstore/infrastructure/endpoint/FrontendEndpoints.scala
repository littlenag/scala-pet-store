package io.github.pauljamescleary.petstore.infrastructure.endpoint

import cats.data.{NonEmptyList, OptionT}
import cats.effect.{ContextShift, Effect}
import org.http4s.CacheDirective.`no-cache`
import org.http4s._
import org.http4s.dsl.Http4sDsl
import org.http4s.headers.{`Cache-Control`, `Content-Type`}
import org.log4s.getLogger
import scalatags.Text.TypedTag

import scala.concurrent.ExecutionContext
import scala.language.higherKinds

class FrontendEndpoints[F[_]: Effect: ContextShift] extends Http4sDsl[F] {

  private[this] val logger = getLogger

  /* Needed for service composition via |+| */
  import cats.implicits._

  import org.http4s.server.staticcontent.webjarService
  import org.http4s.server.staticcontent.WebjarService.{WebjarAsset, Config}

  val F = implicitly[Effect[F]]

  val supportedStaticExtensions = List(".html", ".js", ".map", ".css", ".png", ".ico")

  val ec = ExecutionContext.Implicits.global

  def getResource(pathInfo: String) = F.delay(getClass.getResource(pathInfo))

  // only allow js assets
  def isJsAsset(asset: WebjarAsset): Boolean = {
    asset.asset.endsWith(".js") || asset.asset.endsWith(".css")
  }

  def allowAsset(asset: WebjarAsset) = true

  val jsScript = "resources/scala-pet-store-frontend-fastopt.js"
  val jsDeps = "resources/scala-pet-store-frontend-jsdeps.js"

  val indexHTML: TypedTag[String] = {
    import scalatags.Text.all._

    // navbar, header footer
    html(
      head(
        link(rel := "stylesheet", href := "/webjars/bootstrap/3.3.6/dist/css/bootstrap.css"),
        script(`type`:= "text/javascript", src := jsDeps)
      ),
      body(
        div(id := "root"),
        div(id := "text")(
          p("some text")
        ),
        //script(`type`:= "text/javascript", src := jsDeps),
        script(`type`:= "text/javascript", src := jsScript)
      )
    )
  }

  val indexHtmlEndpoint: HttpRoutes[F] =
    HttpRoutes.of[F] {
      case GET -> Root =>
        Ok(indexHTML().render)
          .map(
            _.withContentType(`Content-Type`(MediaType.text.html, Charset.`UTF-8`))
              .putHeaders(`Cache-Control`(NonEmptyList.of(`no-cache`())))
          )
    }

  val webjars: HttpRoutes[F] = webjarService(
    Config(
      filter = isJsAsset,
      blockingExecutionContext = ec
    )
  )

  val resourcesEndpoint: HttpRoutes[F] =
    HttpRoutes.of[F] {
      case req @ GET -> "webjars" /: path =>
        logger.info(s"webjars: $path")
        webjars(req.withPathInfo(path.toString))
            .fold(NotFound())(F.pure)
            .flatten

      case req @ GET -> "resources" /: path if supportedStaticExtensions.exists(req.pathInfo.endsWith) =>
        StaticFile.fromResource[F](path.toString, ec, req.some)
            .orElse(OptionT.liftF(getResource(path.toString)).flatMap(StaticFile.fromURL[F](_, ec, req.some)))
            .map(_.putHeaders(`Cache-Control`(NonEmptyList.of(`no-cache`()))))
            .fold(NotFound())(F.pure)
            .flatten
    }

  val endpoints: HttpRoutes[F] = resourcesEndpoint <+> indexHtmlEndpoint
}

object FrontendEndpoints {
  def endpoints[F[_]: Effect: ContextShift](): HttpRoutes[F] =
    new FrontendEndpoints[F].endpoints
}
