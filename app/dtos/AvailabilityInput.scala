package dtos

import play.api.data.Form
import play.api.data.Forms.{localDate, mapping}

import java.time.LocalDate

case class AvailabilityInput(checkInDate: LocalDate, checkOutDate: LocalDate)

object AvailabilityInput {
  implicit def fromDateTuple(dates: (LocalDate, LocalDate)): AvailabilityInput = AvailabilityInput(dates._1, dates._2)
  implicit def fromAvailabilityInput(availabilityInput: AvailabilityInput): (LocalDate, LocalDate) = (availabilityInput.checkInDate, availabilityInput.checkOutDate)

  val form = Form(
    mapping(
      "checkInDate" -> localDate("yyyy-MM-dd"),
      "checkOutDate" -> localDate("yyyy-MM-dd")
    )(AvailabilityInput.apply)(AvailabilityInput.unapply).verifying(
      "error.checkinDate",
      availability => availability.checkInDate.isBefore(availability.checkOutDate)
    )
  )
}
