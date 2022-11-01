import error.ErrorInfo

import sttp.tapir._
import sttp.tapir.json.circe._
import sttp.tapir.generic.auto._
import sttp.model.StatusCode
import io.circe.generic.auto._

package object endpoints {
  private type DummyMessageEndpoint[S, I,E, O] = Endpoint[S, I, E, O, Any]
  type BaseEndpoint[I,E, O] = DummyMessageEndpoint[Unit, I,E, O]
  type SecureEndpoint[S, I,E, O] = DummyMessageEndpoint[S, I, E, O]
  val baseEndpoint: BaseEndpoint[Unit, Unit,Unit] =
    endpoint
      .in("api")
//
//  val baseSecureEndpoint: SecureEndpoint[String, Unit, Unit] =
//    endpoint
//      .in("api")
//      .securityIn(auth.bearer[String]())
}
