package exceptions.handler

import exceptions.ResourceNotFoundException
import play.api.http.HttpErrorHandler
import play.api.libs.json.Json
import play.api.mvc.Results.{InternalServerError, NotFound, Status}
import play.api.mvc.{RequestHeader, Result}

import java.time.LocalDateTime
import scala.concurrent.Future

class ExceptionsHandler extends HttpErrorHandler {

  case class ErrorResponse(status: Int, datetime: LocalDateTime, error: String)

  implicit val errorJsonFormat = Json.format[ErrorResponse]

  def onClientError(request: RequestHeader, statusCode: Int, message: String): Future[Result] = {
    Future.successful(
      Status(statusCode)(Json.toJson(ErrorResponse(statusCode, LocalDateTime.now(), "A client error occurred: " + message)))
    )
  }

  def onServerError(request: RequestHeader, exception: Throwable): Future[Result] = {
    Future.successful(
      exception match {
        case e: ResourceNotFoundException => NotFound(Json.toJson(ErrorResponse(play.api.http.Status.NOT_FOUND, LocalDateTime.now(), "Resource not found")))
        case e => {
          e.printStackTrace()
          InternalServerError(Json.toJson(ErrorResponse(play.api.http.Status.INTERNAL_SERVER_ERROR, LocalDateTime.now(), "A server error occurred: " + exception.getMessage)))
        }
      }
    )
  }
}
