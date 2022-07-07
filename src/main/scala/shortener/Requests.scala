package shortener

import infra.Pair
import io.circe.Decoder
import io.circe.generic.semiauto.deriveDecoder

case class CreateLinkRequest(key: Pair.Key)

object CreateLinkRequest {
  implicit val decoder: Decoder[CreateLinkRequest] = deriveDecoder[CreateLinkRequest]
}