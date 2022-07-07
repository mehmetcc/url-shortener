package shortener

import infra.Pair
import io.circe.parser._
import io.circe.syntax._
import zhttp.http._
import zio.ZIO

object App {
  def apply(): Http[Service, Nothing, Any, Response] = Http.collectZIO {
    case Method.GET -> !! / "link" / key    => retrieveLink(key)
    case req @ (Method.POST -> !! / "link") => createLink(req.asInstanceOf[Request])
  }

  private def retrieveLink(key: Pair.Key) = Service
    .retrieveLink(key)
    .fold(
      success = value => Response.json(RetrieveLinkResponse(key, value).asJson.toString),
      failure = error => Response.json(ErrorResponse(error.getMessage).asJson.toString).setStatus(Status.NotFound)
    )

  private def createLink(request: Request) = request.bodyAsString
    .orElseFail(new Throwable("Error occurred while reading json body!"))
    .flatMap(json =>
      ZIO
        .fromEither(decode[CreateLinkRequest](json))
        .orElseFail(new Throwable("Error occurred while parsing json!"))
    )
    .flatMap(result =>
      Service.createLink(result.key).orElseFail(new Throwable("Error occurred while creating a link!"))
    )
    .fold(
      success = result => Response.json(CreateLinkResponse(result).asJson.toString),
      failure =
        error => Response.json(ErrorResponse(error.getMessage).asJson.toString).setStatus(Status.InternalServerError)
    )
}
