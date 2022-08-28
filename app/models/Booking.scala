package models

import java.time.{LocalDate, LocalDateTime, LocalTime}

case class Booking(id: Long, checkInDate: LocalDate, checkOutDate: LocalDate, checkInTime: LocalTime, checkOutTime: LocalTime, customerId: Long, roomId: Long, created: LocalDateTime)
