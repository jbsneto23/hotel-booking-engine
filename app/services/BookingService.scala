package services

import dtos._
import exceptions.{ResourceNotFoundException, ValidationException}
import models._
import repositories.{BookingRepository, CustomerRepository, RoomRepository}

import java.time.LocalDate
import java.time.temporal.ChronoUnit
import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class BookingService @Inject()(private val bookingRepository: BookingRepository,
                               private val customerRepository: CustomerRepository,
                               private val roomRepository: RoomRepository)(implicit executionContext: ExecutionContext) {

  def getAll(): Future[Seq[BookingOutput]] = {
    bookingRepository.getAllWithCustomerAndRoom().map(bookings => bookings.map(b => b))
  }

  def getById(id: Long): Future[BookingOutput] = {
    bookingRepository.getByIdWithCustomerAndRoom(id).collect {
      case Some(b) => b
      case None => throw ResourceNotFoundException()
    }
  }

  def delete(id: Long): Future[Unit] = {
    bookingRepository.delete(id).map { hasDeleted =>
      if (!hasDeleted) throw ResourceNotFoundException()
    }
  }

  def create(newBooking: BookingInput): Future[BookingOutput] = {
    checkAvailability(newBooking).flatMap { _ =>
      (getCustomerById(newBooking.customerId)
        .zip(getRoomById(newBooking.roomId)))
        .zipWith(bookingRepository.create(newBooking)) { (customerAndRoom, booking) =>
          (booking, customerAndRoom._1, customerAndRoom._2)
        }
    }
  }

  private def checkAvailability(bookingInput: BookingInput, excludingId: Option[Long] = None): Future[Unit] = {
    bookingRepository.getByRoomInDateRange(bookingInput.roomId, bookingInput.checkInDate, bookingInput.checkOutDate).map { bookings =>
      (if (excludingId.isDefined) bookings.filter(b => b.id != excludingId.get) else bookings)
        .sortBy(_.checkOutDate)(Ordering[LocalDate].reverse).headOption.map { booking =>
        if (bookingInput.checkInDate.isEqual(booking.checkOutDate)) {
          if (bookingInput.checkInTime.isBefore(booking.checkOutTime.plus(4, ChronoUnit.HOURS)))
            throw ValidationException("Room is only available from " + booking.checkOutTime.plus(4, ChronoUnit.HOURS))
        } else throw ValidationException("Room is not available on these dates")
      }
    }
  }

  private def getCustomerById(customerId: Long): Future[Customer] = {
    customerRepository.getById(customerId).map(customer => customer.getOrElse(throw ValidationException(s"Customer with id $customerId does not exists")))
  }

  private def getRoomById(roomId: Long): Future[Room] = {
    roomRepository.getById(roomId).map(room => room.getOrElse(throw ValidationException(s"Room with id $roomId does not exists")))
  }

  def update(updatedBooking: BookingInput): Future[BookingOutput] = {
    checkAvailability(updatedBooking, updatedBooking.id).flatMap { _ =>
      (getCustomerById(updatedBooking.customerId)
        .zip(getRoomById(updatedBooking.roomId)))
        .zipWith(bookingRepository.update(updatedBooking)) { (customerAndRoom, booking) =>
          booking match {
            case Some(b) => (b, customerAndRoom._1, customerAndRoom._2)
            case _ => throw ResourceNotFoundException()
          }
        }
    }
  }

  def occupancy(occupancyDate: OccupancyInput): Future[Seq[OccupancyOutput]] = {
    bookingRepository.getRoomsAndBookingsOnDate(occupancyDate).map { roomsAndBookings =>
      roomsAndBookings.map {
        case (room, booking) => (room, occupancyDate.date, booking)
      }
    }
  }

  def availability(input: AvailabilityInput): Future[AvailabilityOutput] = {
    bookingRepository.getRoomsWithoutBookingsInDateRange(input.checkInDate, input.checkOutDate).map { rooms =>
      (input.checkInDate, input.checkOutDate, rooms)
    }
  }

}
