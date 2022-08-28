package repositories.slick

import models.{Booking, Customer, Room}
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import repositories.BookingRepository
import repositories.slick.tables.{Bookings, Customers, Rooms}
import slick.jdbc.JdbcProfile

import java.time.LocalDate
import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class SlickBookingRepository @Inject()(protected val dbConfigProvider: DatabaseConfigProvider)(implicit executionContext: ExecutionContext) extends HasDatabaseConfigProvider[JdbcProfile] with BookingRepository {

  import Bookings._
  import Customers._
  import Rooms._
  import profile.api._

  def getAll(): Future[Seq[Booking]] = db.run {
    bookings.result
  }

  def getAllWithCustomerAndRoom(): Future[Seq[(Booking, Customer, Room)]] = db.run {
    bookings
      .join(customers).on(_.customerId === _.id)
      .join(rooms).on(_._1.roomId === _.id)
      .map { case ((booking, customer), room) => (booking, customer, room) }.result
  }

  def getByRoomInDateRange(roomId: Long, startDate: LocalDate, endDate: LocalDate): Future[Seq[Booking]] = db.run {
    bookings.filter(b => b.roomId === roomId && ((b.checkInDate >= startDate && b.checkInDate <= endDate) || (b.checkOutDate >= startDate && b.checkOutDate <= endDate))).result
  }

  def getById(id: Long): Future[Option[Booking]] = db.run {
    bookings.filter(_.id === id).result.headOption
  }

  def getByIdWithCustomerAndRoom(id: Long): Future[Option[(Booking, Customer, Room)]] = db.run {
    bookings
      .filter(_.id === id)
      .join(customers).on(_.customerId === _.id)
      .join(rooms).on(_._1.roomId === _.id)
      .map { case ((booking, customer), room) => (booking, customer, room) }.result.headOption
  }

  def getRoomsAndBookingsOnDate(date: LocalDate): Future[Seq[(Room, Option[(Booking, Customer, Room)])]] = db.run {
    bookings
      .filter(b => b.checkInDate <= date && b.checkOutDate > date)
      .join(customers).on(_.customerId === _.id)
      .joinRight(rooms).on(_._1.roomId === _.id)
      .map { case (bookingAndCustomer, room) => (room, bookingAndCustomer.map(bc => (bc._1, bc._2, room))) }.result
  }

  def getRoomsWithoutBookingsInDateRange(startDate: LocalDate, endDate: LocalDate): Future[Seq[Room]] = db.run {
    bookings
      .filter(b => (b.checkInDate >= startDate && b.checkInDate < endDate) || (b.checkOutDate > startDate && b.checkOutDate < endDate))
      .joinRight(rooms).on(_.roomId === _.id)
      .filter(bookingAndRoom => bookingAndRoom._1.isEmpty)
      .map { case (_, room) => room }.result
  }

  def create(booking: Booking): Future[Booking] = db.run {
    (bookings returning bookings.map(_.id) into ((r, generatedId) => r.copy(id = generatedId))) += booking
  }

  def update(booking: Booking): Future[Option[Booking]] = db.run {
    bookings.filter(_.id === booking.id).update(booking).map {
      case 0 => None
      case _ => Some(booking)
    }
  }

  def delete(id: Long): Future[Boolean] = db.run {
    bookings.filter(_.id === id).delete.map {
      case 0 => false
      case _ => true
    }
  }
}
