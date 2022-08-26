package controllers

import dtos.{RoomInput, RoomOutput}
import play.api.i18n.I18nSupport
import play.api.libs.json.Json
import play.api.mvc.{AbstractController, AnyContent, ControllerComponents, Request}
import services.RoomService

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class RoomController @Inject()(val roomService: RoomService, val cc: ControllerComponents)(implicit executionContext: ExecutionContext) extends AbstractController(cc) with I18nSupport {

  implicit val roomOutputJsonFormat = Json.format[RoomOutput]
  implicit val roomInputJsonFormat = Json.format[RoomInput]

  def getAll() = Action.async { implicit request: Request[AnyContent] =>
    roomService.getAll().map(rooms => {
      Ok(Json.toJson(rooms))
    })
  }

  def getById(id: Long) = Action.async { implicit request: Request[AnyContent] =>
    roomService.getById(id).map(room => Ok(Json.toJson(room)))
  }

  def create() = Action.async { implicit request: Request[AnyContent] =>
    RoomInput.form.bindFromRequest().fold(
      formWithErrors => {
        Future.successful(BadRequest(formWithErrors.errorsAsJson))
      },
      newRoom => {
        roomService.create(newRoom).map(r => Created(Json.toJson(r)))
      }
    )
  }

  def update(id: Long) = Action.async { implicit request: Request[AnyContent] =>
    RoomInput.form.bindFromRequest().fold(
      formWithErrors => {
        Future.successful(BadRequest(formWithErrors.errorsAsJson))
      },
      updatedRoom => {
        roomService.update(updatedRoom.copy(id = Some(id))).map(room => Ok(Json.toJson(room)))
      }
    )
  }

  def delete(id: Long) = Action.async { implicit request: Request[AnyContent] =>
    roomService.delete(id).map(_ => NoContent)
  }

}
