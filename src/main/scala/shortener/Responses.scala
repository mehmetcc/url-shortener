package shortener

import infra.Pair
import io.circe.Encoder
import io.circe.generic.semiauto.deriveEncoder

case class ErrorResponse(message: String)

object ErrorResponse {
  implicit def encoder: Encoder.AsObject[ErrorResponse] = deriveEncoder[ErrorResponse]
}

case class CreateLinkResponse(key: Pair.Key)

object CreateLinkResponse {
  implicit def encoder: Encoder.AsObject[CreateLinkResponse] = deriveEncoder[CreateLinkResponse]
}

case class RetrieveLinkResponse(key: Pair.Key, link: Pair.Value)

object RetrieveLinkResponse {
  implicit def encoder: Encoder.AsObject[RetrieveLinkResponse] = deriveEncoder[RetrieveLinkResponse]
}
