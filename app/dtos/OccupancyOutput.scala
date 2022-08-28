package dtos

import models.{Booking, Customer, Room}

import java.time.LocalDate

case class OccupancyOutput(room: RoomOutput, date: LocalDate, status: String, booking: Option[BookingOutput])

object OccupancyOutput {
  implicit def fromRoomWithBooking(rb: (Room, LocalDate, Option[(Booking, Customer, Room)])): OccupancyOutput = OccupancyOutput(rb._1, rb._2, if (rb._3.isDefined) "Occupied" else "Available", rb._3.map(b => b))
}
