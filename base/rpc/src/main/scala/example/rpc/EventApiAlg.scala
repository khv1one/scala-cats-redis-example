package example.rpc

import endpoints4s.algebra.Endpoints
import endpoints4s.algebra.circe.JsonEntitiesFromCodecs

trait EventApiAlg extends Endpoints with JsonEntitiesFromCodecs {

  val baseUrl: Path[Unit] = path / "api"

  def errorResponse: Response[String] = response(InternalServerError, jsonResponse[String])

  protected val pushEventApi: Endpoint[
    (String, String, String),
    Unit
  ] = {
    endpoint(
      get(baseUrl / "event" / "push" /? (qs[String]("message") & qs[String]("action") & qs[String]("group"))),
      ok(emptyResponse)
    )
  }

  protected val resultEventApi: Endpoint[
    (String, String),
    Unit
  ] = {
    endpoint(
      post(baseUrl / "event" / "result" /? (qs[String]("id") & qs[String]("status")), jsonRequest[Unit]),
      ok(emptyResponse)
    )
  }
}
