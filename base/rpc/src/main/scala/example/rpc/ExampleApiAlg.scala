package example.rpc

import endpoints4s.algebra.Endpoints
import endpoints4s.algebra.circe.JsonEntitiesFromCodecs

trait ExampleApiAlg extends Endpoints with JsonEntitiesFromCodecs {

  val baseUrl = path / "api"

  //def errorResponse: Response[String] = response(InternalServerError, response[String])

  protected val exampleApi: Endpoint[String, Unit] = {
    endpoint(
      get(baseUrl / "example_api" /? qs[String]("q1")),
      ok(jsonResponse[Unit])
    )
  }
}
