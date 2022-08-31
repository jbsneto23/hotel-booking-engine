package repositories.slick.tables

import models.Booking
import slick.jdbc.H2Profile.api._

import java.time.{LocalDate, LocalDateTime, LocalTime}

class Bookings(tag: Tag) extends Table[Booking](tag, "booking") {
  def id = column[Long]("id_booking", O.PrimaryKey, O.AutoInc)
  def checkInDate = column[LocalDate]("check_in_date")
  def checkOutDate = column[LocalDate]("check_out_date")
  def checkInTime = column[LocalTime]("check_in_time")
  def checkOutTime = column[LocalTime]("check_out_time")
  def customerId = column[Long]("id_customer")
  def roomId = column[Long]("id_room")
  def created = column[LocalDateTime]("created", O.Default(LocalDateTime.now()))

  def customer = foreignKey("fk_customer", customerId, Customers.customers)(_.id, onDelete = ForeignKeyAction.Cascade)
  def room = foreignKey("fk_room", roomId, Rooms.rooms)(_.id, onDelete = ForeignKeyAction.Cascade)

  def * = (id, checkInDate, checkOutDate, checkInTime, checkOutTime, customerId, roomId, created).mapTo[Booking]
}

object Bookings {
  val bookings = TableQuery[Bookings]
}
