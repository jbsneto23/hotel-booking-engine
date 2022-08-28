package dtos

import play.api.data.Form
import play.api.data.Forms.{localDate, mapping}

import java.time.LocalDate

case class OccupancyInput(date: LocalDate)

object OccupancyInput {
  implicit def fromLocalDate(date: LocalDate): OccupancyInput = OccupancyInput(date)
  implicit def fromOccupancyInput(occupancyInput: OccupancyInput): LocalDate = occupancyInput.date

  val form = Form(
    mapping(
      "date" -> localDate("yyyy-MM-dd")
    )(OccupancyInput.apply)(OccupancyInput.unapply)
  )
}
