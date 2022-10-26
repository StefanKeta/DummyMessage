import cats.Applicative
import cats.implicits._
import org.http4s.HttpRoutes
import sttp.model.StatusCode
import sttp.tapir._
import sttp.tapir.server.ServerEndpoint
import sttp.tapir.server.http4s.Http4sServerInterpreter
package object base {
  type BaseEndpoint[I, O] = PublicEndpoint[I, Unit, O, Any]
  type SecureEndpoint[S, I, O] = Endpoint[String, I, Unit, O, Any]
  val baseEndpoint: BaseEndpoint[Unit, Unit] =
    endpoint
      .in("api")

  val baseSecureEndpoint: SecureEndpoint[String, Unit, Unit] =
    endpoint
      .in("api")
      .securityIn(auth.bearer[String]())

  val healthEndpoint: BaseEndpoint[Unit, StatusCode] = baseEndpoint.get
    .in("health")
    .out(statusCode)

  def healthRoute[F[_]: Applicative]() =
    healthEndpoint.serverLogic(_ => StatusCode.Ok.asRight[Unit].pure[F])

  def toRoutes[F[_]](routes: List[ServerEndpoint[Any, F]])(implicit
      http4sServerInterpreter: Http4sServerInterpreter[F]
  ): HttpRoutes[F] =
    http4sServerInterpreter.toRoutes(routes)
}
