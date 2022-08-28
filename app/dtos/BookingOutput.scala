package dtos

import models.{Booking, Customer, Room}

import java.time.{LocalDate, LocalDateTime, LocalTime}

case class BookingOutput(id: Long, checkInDate: LocalDate, checkOutDate: LocalDate, checkInTime: LocalTime, checkOutTime: LocalTime, customer: CustomerOutput, room: RoomOutput, created: LocalDateTime)

object BookingOutput {
  implicit def fromBookingOutput(b: BookingOutput): Booking = Booking(b.id, b.checkInDate, b.checkOutDate, b.checkInTime, b.checkOutTime, b.customer.id, b.room.id, b.created)
  implicit def fromBookingWithCustomerAndRoom(b: (Booking, Customer, Room)): BookingOutput = BookingOutput(b._1.id, b._1.checkInDate, b._1.checkOutDate, b._1.checkInTime, b._1.checkOutTime, b._2, b._3, b._1.created)
}