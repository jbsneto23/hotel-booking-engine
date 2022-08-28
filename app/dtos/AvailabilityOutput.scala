package dtos

import models.Room

import java.time.LocalDate

case class AvailabilityOutput(checkInDate: LocalDate, checkOutDate: LocalDate, availability: Seq[RoomOutput])

object AvailabilityOutput {
  implicit def fromAvailabilityData(data: (LocalDate, LocalDate, Seq[Room])): AvailabilityOutput = AvailabilityOutput(data._1, data._2, data._3.map(r => r))
}

