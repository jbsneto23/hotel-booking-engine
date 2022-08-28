package controllers

import dtos._
import play.api.i18n.I18nSupport
import play.api.libs.json.Json
import play.api.mvc.{AbstractController, AnyContent, ControllerComponents, Request}
import services.BookingService

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class BookingController @Inject()(val bookingService: BookingService, val cc: ControllerComponents)(implicit executionContext: ExecutionContext) extends AbstractController(cc) with I18nSupport {

  implicit val customerOutputJsonFormat = Json.format[CustomerOutput]
  implicit val roomOutputJsonFormat = Json.format[RoomOutput]
  implicit val bookingOutputJsonFormat = Json.format[BookingOutput]
  implicit val occupancyOutputJsonFormat = Json.format[OccupancyOutput]
  implicit val availabilityOutputJsonFormat = Json.format[AvailabilityOutput]

  def getAll() = Action.async { implicit request: Request[AnyContent] =>
    bookingService.getAll().map(bookings => {
      Ok(Json.toJson(bookings))
    })
  }

  def getById(id: Long) = Action.async { implicit request: Request[AnyContent] =>
    bookingService.getById(id).map(booking => Ok(Json.toJson(booking)))
  }

  def create() = Action.async { implicit request: Request[AnyContent] =>
    BookingInput.form.bindFromRequest().fold(
      formWithErrors => {
        Future.successful(BadRequest(formWithErrors.errorsAsJson))
      },
      newBooking => {
        bookingService.create(newBooking).map(c => Created(Json.toJson(c)))
      }
    )
  }

  def update(id: Long) = Action.async { implicit request: Request[AnyContent] =>
    BookingInput.form.bindFromRequest().fold(
      formWithErrors => {
        Future.successful(BadRequest(formWithErrors.errorsAsJson))
      },
      updatedBooking => {
        bookingService.update(updatedBooking.copy(id = Some(id))).map(c => Ok(Json.toJson(c)))
      }
    )
  }

  def delete(id: Long) = Action.async { implicit request: Request[AnyContent] =>
    bookingService.delete(id).map(_ => NoContent)
  }

  def occupancy() = Action.async { implicit request: Request[AnyContent] =>
    OccupancyInput.form.bindFromRequest().fold(
      formWithErrors => {
        Future.successful(BadRequest(formWithErrors.errorsAsJson))
      },
      occupancyInput => {
        bookingService.occupancy(occupancyInput).map(o => Ok(Json.toJson(o)))
      }
    )
  }

  def availability() = Action.async { implicit request: Request[AnyContent] =>
    AvailabilityInput.form.bindFromRequest().fold(
      formWithErrors => {
        Future.successful(BadRequest(formWithErrors.errorsAsJson))
      },
      availabilityInput => {
        bookingService.availability(availabilityInput).map(o => Ok(Json.toJson(o)))
      }
    )
  }

}
