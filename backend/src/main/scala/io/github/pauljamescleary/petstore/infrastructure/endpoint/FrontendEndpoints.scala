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

  // only allow js/font/image assets
  def isPublicAsset(asset: WebjarAsset): Boolean = {
    val extensions = Seq(".js", ".css", ".map", ".ttf", ".woff", ".woff2", ".eot", ".svg", ".png")
    extensions.exists(ext => asset.asset.endsWith(ext))
  }

  def allowAsset(asset: WebjarAsset) = true

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
        script(`type`:= "text/javascript", src := "/webjars/scala-pet-store/0.0.1-SNAPSHOT/shared-bundle.js"),
        script(`type`:= "text/javascript", src := "/webjars/scala-pet-store/0.0.1-SNAPSHOT/app-bundle.js")
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
      filter = isPublicAsset,
      blockingExecutionContext = ec
    )
  )

  val resourcesEndpoint: HttpRoutes[F] =
    HttpRoutes.of[F] {
      case req @ GET -> "fonts" /: path =>
        logger.info(s"font: $path")
        logger.info(s"webjar path: ${"/scala-pet-store/0.0.1-SNAPSHOT/fonts" + path.toString}")
        webjars(req.withPathInfo("/scala-pet-store/0.0.1-SNAPSHOT/fonts" + path.toString))
          .fold(NotFound())(F.pure)
          .flatten

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