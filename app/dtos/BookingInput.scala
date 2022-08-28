package dtos

import models.Booking
import play.api.data.Form
import play.api.data.Forms._

import java.time.{LocalDate, LocalDateTime, LocalTime}

case class BookingInput(id: Option[Long], checkInDate: LocalDate, checkOutDate: LocalDate, checkInTime: LocalTime, checkOutTime: LocalTime, customerId: Long, roomId: Long, created: Option[LocalDateTime])

object BookingInput {
  implicit def fromBookingInput(b: BookingInput): Booking = Booking(b.id.getOrElse(0), b.checkInDate, b.checkOutDate, b.checkInTime, b.checkOutTime, b.customerId, b.roomId, b.created.getOrElse(LocalDateTime.now()))
  implicit def fromBooking(b: Booking): BookingInput = BookingInput(Some(b.id), b.checkInDate, b.checkOutDate, b.checkInTime, b.checkOutTime, b.customerId, b.roomId, Some(b.created))

  val form = Form(
    mapping(
      "id" -> optional(longNumber),
      "checkInDate" -> localDate("yyyy-MM-dd"),
      "checkOutDate" -> localDate("yyyy-MM-dd"),
      "checkInTime" -> localTime("HH:mm"),
      "checkOutTime" -> localTime("HH:mm"),
      "customerId" -> longNumber,
      "roomId" -> longNumber,
      "created" -> optional(localDateTime("yyyy-MM-dd HH:mm:ss"))
    )(BookingInput.apply)(BookingInput.unapply).verifying(
      "error.checkinDate",
      booking => booking.checkInDate.isBefore(booking.checkOutDate)
    )
  )
}