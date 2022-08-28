package repositories

import com.google.inject.ImplementedBy
import models.{Booking, Customer, Room}
import repositories.slick.SlickBookingRepository

import java.time.LocalDate
import scala.concurrent.Future

@ImplementedBy(classOf[SlickBookingRepository])
trait BookingRepository {
  def getAll(): Future[Seq[Booking]]

  def getAllWithCustomerAndRoom(): Future[Seq[(Booking, Customer, Room)]]

  /**
   * Get bookings for a specific room that have check-in or check-out within a time window.
   *
   * @param roomId Room id
   * @param startDate Window start date
   * @param endDate Window end date (inclusive)
   * @return Sequence of bookings found
   */
  def getByRoomInDateRange(roomId: Long, startDate: LocalDate, endDate: LocalDate): Future[Seq[Booking]]

  /**
   * Get all rooms and their bookings on a specific date.
   * @param date Date to search
   * @return Sequence of rooms and their bookings (if any) on the given date
   */
  def getRoomsAndBookingsOnDate(date: LocalDate): Future[Seq[(Room, Option[(Booking, Customer, Room)])]]

  /**
   * Get all rooms that do not have bookings within a time window.
   *
   * @param startDate Window start date
   * @param endDate Window end date (not inclusive)
   * @return Sequence of rooms without bookings found
   */
  def getRoomsWithoutBookingsInDateRange(startDate: LocalDate, endDate: LocalDate): Future[Seq[Room]]

  def getById(id: Long): Future[Option[Booking]]

  def getByIdWithCustomerAndRoom(id: Long): Future[Option[(Booking, Customer, Room)]]

  def create(booking: Booking): Future[Booking]

  def update(booking: Booking): Future[Option[Booking]]

  def delete(id: Long): Future[Boolean]
}
