package services

import dtos._
import exceptions.{ResourceNotFoundException, ValidationException}
import models.{Booking, Customer, Room}
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.when
import org.scalatest.concurrent.ScalaFutures
import org.scalatestplus.mockito.MockitoSugar
import org.scalatestplus.play.PlaySpec
import play.api.test.Helpers.stubControllerComponents
import repositories.{BookingRepository, CustomerRepository, RoomRepository}

import java.time.{LocalDate, LocalDateTime, LocalTime}
import scala.concurrent.Future

class BookingServiceSpec extends PlaySpec with ScalaFutures with MockitoSugar {

  implicit val executionContext = stubControllerComponents().executionContext

  val checkInDate = LocalDate.of(2022, 10, 10)
  val checkOutDate = LocalDate.of(2022, 11, 10)
  val checkInTime = LocalTime.of(16, 0)
  val checkOutTime = LocalTime.of(12, 0)

  "BookingService#getById" should {

    "return a booking object with id 1 when id 1 is provided and it exists in the repository" in {
      val customerRepository = mock[CustomerRepository]
      val roomRepository = mock[RoomRepository]
      val bookingRepository = mock[BookingRepository]

      val returnedCustomer = Customer(1, "Test", "Test", "test@test.com", "11111")
      val returnedRoom = Room(1, "Test Title", "Test Description", 1, 0, false)
      val returnedBooking = Booking(1, checkInDate, checkOutDate, checkInTime, checkOutTime, 1, 1, LocalDateTime.now())
      val returnedBookingWithCustomerAndRoom = (returnedBooking, returnedCustomer, returnedRoom)

      when(bookingRepository.getByIdWithCustomerAndRoom(1)).thenReturn(Future.successful(Some(returnedBookingWithCustomerAndRoom)))

      val bookingService = new BookingService(bookingRepository, customerRepository, roomRepository)
      val actualBookingOutputFuture = bookingService.getById(1)

      ScalaFutures.whenReady(actualBookingOutputFuture) { actualBookingOutput =>
        actualBookingOutput.id mustBe 1
        actualBookingOutput.checkInDate mustBe checkInDate
        actualBookingOutput.checkOutDate mustBe checkOutDate
        actualBookingOutput.checkInTime mustBe checkInTime
        actualBookingOutput.checkOutTime mustBe checkOutTime
        actualBookingOutput.customer.id mustBe 1
        actualBookingOutput.room.id mustBe 1
      }
    }

    "throw ResourceNotFoundException if an id is provided but it does not exists in the repository" in {
      val customerRepository = mock[CustomerRepository]
      val roomRepository = mock[RoomRepository]
      val bookingRepository = mock[BookingRepository]

      when(bookingRepository.getByIdWithCustomerAndRoom(1)).thenReturn(Future.successful(None))

      val bookingService = new BookingService(bookingRepository, customerRepository, roomRepository)
      val actualBookingOutputFuture = bookingService.getById(1)

      ScalaFutures.whenReady(actualBookingOutputFuture.failed) { e =>
        e mustBe a[ResourceNotFoundException]
      }
    }
  }

  "BookingService#getAll" should {

    "return a sequence with the bookings available in the repository" in {
      val customerRepository = mock[CustomerRepository]
      val roomRepository = mock[RoomRepository]
      val bookingRepository = mock[BookingRepository]

      val customer1 = Customer(1, "Test", "Test", "test@test.com", "11111")
      val room1 = Room(1, "Test Title", "Test Description", 1, 0, false)
      val booking1 = Booking(1, checkInDate, checkOutDate, checkInTime, checkOutTime, 1, 1, LocalDateTime.now())
      val completeBooking1 = (booking1, customer1, room1)

      val customer2 = Customer(2, "Test", "Test", "test@test.com", "22222")
      val room2 = Room(2, "Test Title", "Test Description", 2, 0, false)
      val booking2 = Booking(2, checkInDate, checkOutDate, checkInTime, checkOutTime, 2, 2, LocalDateTime.now())
      val completeBooking2 = (booking2, customer2, room2)

      when(bookingRepository.getAllWithCustomerAndRoom()).thenReturn(Future.successful(Seq(completeBooking1, completeBooking2)))

      val bookingService = new BookingService(bookingRepository, customerRepository, roomRepository)
      val actualBookingsOutputFuture = bookingService.getAll()

      ScalaFutures.whenReady(actualBookingsOutputFuture) { bookings =>
        bookings.size mustBe 2
        bookings(0).id mustBe 1
        bookings(0).customer.id mustBe 1
        bookings(0).room.id mustBe 1
        bookings(1).id mustBe 2
        bookings(1).customer.id mustBe 2
        bookings(1).room.id mustBe 2
      }
    }

    "return an empty sequence if no bookings are available in the repository" in {
      val customerRepository = mock[CustomerRepository]
      val roomRepository = mock[RoomRepository]
      val bookingRepository = mock[BookingRepository]

      when(bookingRepository.getAllWithCustomerAndRoom()).thenReturn(Future.successful(Seq()))

      val bookingService = new BookingService(bookingRepository, customerRepository, roomRepository)
      val actualBookingsOutputFuture = bookingService.getAll()

      ScalaFutures.whenReady(actualBookingsOutputFuture) { bookings =>
        bookings mustBe Seq()
      }
    }
  }

  "BookingService#create" should {

    "return new booking object when a valid input booking is provided" in {
      val customerRepository = mock[CustomerRepository]
      val returnedCustomer = Customer(1, "Test", "Test", "test@test.com", "11111")
      when(customerRepository.getById(1)).thenReturn(Future.successful(Some(returnedCustomer)))

      val roomRepository = mock[RoomRepository]
      val returnedRoom = Room(1, "Test Title", "Test Description", 1, 0, false)
      when(roomRepository.getById(1)).thenReturn(Future.successful(Some(returnedRoom)))

      val bookingRepository = mock[BookingRepository]

      when(bookingRepository.getByRoomInDateRange(1, checkInDate, checkOutDate)).thenReturn(Future.successful(Seq()))

      val returnedBooking = Booking(1, checkInDate, checkOutDate, checkInTime, checkOutTime, 1, 1, LocalDateTime.now())
      when(bookingRepository.create(any[Booking])).thenReturn(Future.successful(returnedBooking))

      val bookingService = new BookingService(bookingRepository, customerRepository, roomRepository)
      val newBookingInput = BookingInput(None, checkInDate, checkOutDate, checkInTime, checkOutTime, 1, 1, Some(LocalDateTime.now()))

      val actualBookingOutputFuture = bookingService.create(newBookingInput)

      ScalaFutures.whenReady(actualBookingOutputFuture) { actualBookingOutput =>
        actualBookingOutput.id mustBe 1
        actualBookingOutput.checkInDate mustBe checkInDate
        actualBookingOutput.checkOutDate mustBe checkOutDate
        actualBookingOutput.checkInTime mustBe checkInTime
        actualBookingOutput.checkOutTime mustBe checkOutTime
        actualBookingOutput.customer.id mustBe 1
        actualBookingOutput.room.id mustBe 1
      }
    }

    "throw ValidationException if an input booking without a valid customer is provided" in {
      val customerRepository = mock[CustomerRepository]
      when(customerRepository.getById(1)).thenReturn(Future.successful(None))

      val roomRepository = mock[RoomRepository]
      val returnedRoom = Room(1, "Test Title", "Test Description", 1, 0, false)
      when(roomRepository.getById(1)).thenReturn(Future.successful(Some(returnedRoom)))

      val bookingRepository = mock[BookingRepository]
      when(bookingRepository.getByRoomInDateRange(1, checkInDate, checkOutDate)).thenReturn(Future.successful(Seq()))

      val bookingService = new BookingService(bookingRepository, customerRepository, roomRepository)
      val newBookingInput = BookingInput(None, checkInDate, checkOutDate, checkInTime, checkOutTime, 1, 1, Some(LocalDateTime.now()))

      val actualBookingOutputFuture = bookingService.create(newBookingInput)

      ScalaFutures.whenReady(actualBookingOutputFuture.failed) { e =>
        e mustBe a[ValidationException]
      }
    }

    "throw ValidationException if an input booking without a valid room is provided" in {
      val customerRepository = mock[CustomerRepository]
      val returnedCustomer = Customer(1, "Test", "Test", "test@test.com", "11111")
      when(customerRepository.getById(1)).thenReturn(Future.successful(Some(returnedCustomer)))

      val roomRepository = mock[RoomRepository]
      when(roomRepository.getById(1)).thenReturn(Future.successful(None))

      val bookingRepository = mock[BookingRepository]
      when(bookingRepository.getByRoomInDateRange(1, checkInDate, checkOutDate)).thenReturn(Future.successful(Seq()))

      val bookingService = new BookingService(bookingRepository, customerRepository, roomRepository)
      val newBookingInput = BookingInput(None, checkInDate, checkOutDate, checkInTime, checkOutTime, 1, 1, Some(LocalDateTime.now()))

      val actualBookingOutputFuture = bookingService.create(newBookingInput)

      ScalaFutures.whenReady(actualBookingOutputFuture.failed) { e =>
        e mustBe a[ValidationException]
      }
    }

    "throw ValidationException if an input booking with unavailable dates is provided" in {
      val customerRepository = mock[CustomerRepository]
      val returnedCustomer = Customer(1, "Test", "Test", "test@test.com", "11111")
      when(customerRepository.getById(1)).thenReturn(Future.successful(Some(returnedCustomer)))

      val roomRepository = mock[RoomRepository]
      val returnedRoom = Room(1, "Test Title", "Test Description", 1, 0, false)
      when(roomRepository.getById(1)).thenReturn(Future.successful(Some(returnedRoom)))

      val bookingRepository = mock[BookingRepository]
      val returnedBooking = Booking(1, checkInDate, checkOutDate, checkInTime, checkOutTime, 1, 1, LocalDateTime.now())
      when(bookingRepository.getByRoomInDateRange(1, checkInDate, checkOutDate)).thenReturn(Future.successful(Seq(returnedBooking)))

      val bookingService = new BookingService(bookingRepository, customerRepository, roomRepository)
      val newBookingInput = BookingInput(None, checkInDate, checkOutDate, checkInTime, checkOutTime, 1, 1, Some(LocalDateTime.now()))

      val actualBookingOutputFuture = bookingService.create(newBookingInput)

      ScalaFutures.whenReady(actualBookingOutputFuture.failed) { e =>
        e mustBe a[ValidationException]
      }
    }

    "throw ValidationException if an input booking with available dates but with a check-in time that is less than 4 hours apart from the check-out that is done on the same day is provided" in {
      val newCheckInDate = LocalDate.of(2022, 11, 10)
      val newCheckOutDate = LocalDate.of(2022, 12, 10)
      val newCheckInTime = LocalTime.of(13, 0)
      val newCheckOutTime = LocalTime.of(12, 0)

      val customerRepository = mock[CustomerRepository]
      val returnedCustomer = Customer(1, "Test", "Test", "test@test.com", "11111")
      when(customerRepository.getById(1)).thenReturn(Future.successful(Some(returnedCustomer)))

      val roomRepository = mock[RoomRepository]
      val returnedRoom = Room(1, "Test Title", "Test Description", 1, 0, false)
      when(roomRepository.getById(1)).thenReturn(Future.successful(Some(returnedRoom)))

      val bookingRepository = mock[BookingRepository]
      val returnedBooking = Booking(1, checkInDate, checkOutDate, checkInTime, checkOutTime, 1, 1, LocalDateTime.now())
      when(bookingRepository.getByRoomInDateRange(1, newCheckInDate, newCheckOutDate)).thenReturn(Future.successful(Seq(returnedBooking)))

      val bookingService = new BookingService(bookingRepository, customerRepository, roomRepository)
      val newBookingInput = BookingInput(None, newCheckInDate, newCheckOutDate, newCheckInTime, newCheckOutTime, 1, 1, Some(LocalDateTime.now()))

      val actualBookingOutputFuture = bookingService.create(newBookingInput)

      ScalaFutures.whenReady(actualBookingOutputFuture.failed) { e =>
        e mustBe a[ValidationException]
      }
    }

  }

  "BookingService#delete" should {

    "delete the booking if an id is provided and it exists in the repository" in {
      val customerRepository = mock[CustomerRepository]
      val roomRepository = mock[RoomRepository]
      val bookingRepository = mock[BookingRepository]

      when(bookingRepository.delete(1)).thenReturn(Future.successful(true))

      val bookingService = new BookingService(bookingRepository, customerRepository, roomRepository)

      ScalaFutures.whenReady(bookingService.delete(1))(u => u)
    }

    "throw ResourceNotFoundException if an id is provided but it does not exists in the repository" in {
      val customerRepository = mock[CustomerRepository]
      val roomRepository = mock[RoomRepository]
      val bookingRepository = mock[BookingRepository]

      when(bookingRepository.delete(1)).thenReturn(Future.successful(false))

      val bookingService = new BookingService(bookingRepository, customerRepository, roomRepository)

      ScalaFutures.whenReady(bookingService.delete(1).failed) { e =>
        e mustBe a[ResourceNotFoundException]
      }
    }

  }

  "BookingService#update" should {

    "return an updated booking object when an input room is provided and it exists in the repository" in {
      val updatedCheckInDate = LocalDate.of(2022, 10, 11)
      val updatedCheckOutDate = LocalDate.of(2022, 12, 11)
      val updatedCheckInTime = LocalTime.of(14, 0)
      val updatedCheckOutTime = LocalTime.of(13, 0)

      val customerRepository = mock[CustomerRepository]
      val returnedCustomer = Customer(1, "Test", "Test", "test@test.com", "11111")
      when(customerRepository.getById(1)).thenReturn(Future.successful(Some(returnedCustomer)))

      val roomRepository = mock[RoomRepository]
      val returnedRoom = Room(1, "Test Title", "Test Description", 1, 0, false)
      when(roomRepository.getById(1)).thenReturn(Future.successful(Some(returnedRoom)))

      val bookingRepository = mock[BookingRepository]
      val existingBooking = Booking(1, checkInDate, checkOutDate, checkInTime, checkOutTime, 1, 1, LocalDateTime.now())
      when(bookingRepository.getByRoomInDateRange(1, updatedCheckInDate, updatedCheckOutDate)).thenReturn(Future.successful(Seq(existingBooking)))

      val returnedBooking = Booking(1, updatedCheckInDate, updatedCheckOutDate, updatedCheckInTime, updatedCheckOutTime, 1, 1, LocalDateTime.now())
      when(bookingRepository.update(any[Booking])).thenReturn(Future.successful(Some(returnedBooking)))

      val bookingService = new BookingService(bookingRepository, customerRepository, roomRepository)
      val updatedBookingInput = BookingInput(Some(1), updatedCheckInDate, updatedCheckOutDate, updatedCheckInTime, updatedCheckOutTime, 1, 1, Some(LocalDateTime.now()))

      val actualBookingOutputFuture = bookingService.update(updatedBookingInput)

      ScalaFutures.whenReady(actualBookingOutputFuture) { actualBookingOutput =>
        actualBookingOutput.id mustBe 1
        actualBookingOutput.checkInDate mustBe updatedCheckInDate
        actualBookingOutput.checkOutDate mustBe updatedCheckOutDate
        actualBookingOutput.checkInTime mustBe updatedCheckInTime
        actualBookingOutput.checkOutTime mustBe updatedCheckOutTime
        actualBookingOutput.customer.id mustBe 1
        actualBookingOutput.room.id mustBe 1
      }
    }

    "throw ResourceNotFoundException if an input booking is provided but it does not exists in the repository" in {
      val customerRepository = mock[CustomerRepository]
      val returnedCustomer = Customer(1, "Test", "Test", "test@test.com", "11111")
      when(customerRepository.getById(1)).thenReturn(Future.successful(Some(returnedCustomer)))

      val roomRepository = mock[RoomRepository]
      val returnedRoom = Room(1, "Test Title", "Test Description", 1, 0, false)
      when(roomRepository.getById(1)).thenReturn(Future.successful(Some(returnedRoom)))

      val bookingRepository = mock[BookingRepository]
      when(bookingRepository.getByRoomInDateRange(1, checkInDate, checkOutDate)).thenReturn(Future.successful(Seq()))

      when(bookingRepository.update(any[Booking])).thenReturn(Future.successful(None))

      val bookingService = new BookingService(bookingRepository, customerRepository, roomRepository)
      val updatedBookingInput = BookingInput(Some(1), checkInDate, checkOutDate, checkInTime, checkOutTime, 1, 1, Some(LocalDateTime.now()))

      val actualBookingOutputFuture = bookingService.update(updatedBookingInput)

      ScalaFutures.whenReady(actualBookingOutputFuture.failed) { e =>
        e mustBe a[ResourceNotFoundException]
      }
    }

    "throw ValidationException if an input booking without a valid customer is provided" in {
      val customerRepository = mock[CustomerRepository]
      when(customerRepository.getById(1)).thenReturn(Future.successful(None))

      val roomRepository = mock[RoomRepository]
      val returnedRoom = Room(1, "Test Title", "Test Description", 1, 0, false)
      when(roomRepository.getById(1)).thenReturn(Future.successful(Some(returnedRoom)))

      val bookingRepository = mock[BookingRepository]
      when(bookingRepository.getByRoomInDateRange(1, checkInDate, checkOutDate)).thenReturn(Future.successful(Seq()))

      val bookingService = new BookingService(bookingRepository, customerRepository, roomRepository)
      val updatedBookingInput = BookingInput(Some(1), checkInDate, checkOutDate, checkInTime, checkOutTime, 1, 1, Some(LocalDateTime.now()))

      val actualBookingOutputFuture = bookingService.update(updatedBookingInput)

      ScalaFutures.whenReady(actualBookingOutputFuture.failed) { e =>
        e mustBe a[ValidationException]
      }
    }

    "throw ValidationException if an input booking without a valid room is provided" in {
      val customerRepository = mock[CustomerRepository]
      val returnedCustomer = Customer(1, "Test", "Test", "test@test.com", "11111")
      when(customerRepository.getById(1)).thenReturn(Future.successful(Some(returnedCustomer)))

      val roomRepository = mock[RoomRepository]
      when(roomRepository.getById(1)).thenReturn(Future.successful(None))

      val bookingRepository = mock[BookingRepository]
      when(bookingRepository.getByRoomInDateRange(1, checkInDate, checkOutDate)).thenReturn(Future.successful(Seq()))

      val bookingService = new BookingService(bookingRepository, customerRepository, roomRepository)
      val updatedBookingInput = BookingInput(Some(1), checkInDate, checkOutDate, checkInTime, checkOutTime, 1, 1, Some(LocalDateTime.now()))

      val actualBookingOutputFuture = bookingService.create(updatedBookingInput)

      ScalaFutures.whenReady(actualBookingOutputFuture.failed) { e =>
        e mustBe a[ValidationException]
      }
    }

    "throw ValidationException if an input booking with unavailable dates is provided" in {
      val customerRepository = mock[CustomerRepository]
      val returnedCustomer = Customer(1, "Test", "Test", "test@test.com", "11111")
      when(customerRepository.getById(1)).thenReturn(Future.successful(Some(returnedCustomer)))

      val roomRepository = mock[RoomRepository]
      val returnedRoom = Room(1, "Test Title", "Test Description", 1, 0, false)
      when(roomRepository.getById(1)).thenReturn(Future.successful(Some(returnedRoom)))

      val bookingRepository = mock[BookingRepository]
      val returnedBooking = Booking(1, checkInDate, checkOutDate, checkInTime, checkOutTime, 1, 1, LocalDateTime.now())
      when(bookingRepository.getByRoomInDateRange(1, checkInDate, checkOutDate)).thenReturn(Future.successful(Seq(returnedBooking)))

      val bookingService = new BookingService(bookingRepository, customerRepository, roomRepository)
      val updatedBookingInput = BookingInput(Some(2), checkInDate, checkOutDate, checkInTime, checkOutTime, 1, 1, Some(LocalDateTime.now()))

      val actualBookingOutputFuture = bookingService.update(updatedBookingInput)

      ScalaFutures.whenReady(actualBookingOutputFuture.failed) { e =>
        e mustBe a[ValidationException]
      }
    }

    "throw ValidationException if an input booking with available dates but with a check-in time that is less than 4 hours apart from the check-out that is done on the same day is provided" in {
      val newCheckInDate = LocalDate.of(2022, 11, 10)
      val newCheckOutDate = LocalDate.of(2022, 12, 10)
      val newCheckInTime = LocalTime.of(13, 0)
      val newCheckOutTime = LocalTime.of(12, 0)

      val customerRepository = mock[CustomerRepository]
      val returnedCustomer = Customer(1, "Test", "Test", "test@test.com", "11111")
      when(customerRepository.getById(1)).thenReturn(Future.successful(Some(returnedCustomer)))

      val roomRepository = mock[RoomRepository]
      val returnedRoom = Room(1, "Test Title", "Test Description", 1, 0, false)
      when(roomRepository.getById(1)).thenReturn(Future.successful(Some(returnedRoom)))

      val bookingRepository = mock[BookingRepository]
      val returnedBooking = Booking(1, checkInDate, checkOutDate, checkInTime, checkOutTime, 1, 1, LocalDateTime.now())
      when(bookingRepository.getByRoomInDateRange(1, newCheckInDate, newCheckOutDate)).thenReturn(Future.successful(Seq(returnedBooking)))

      val bookingService = new BookingService(bookingRepository, customerRepository, roomRepository)
      val updatedBookingInput = BookingInput(Some(2), newCheckInDate, newCheckOutDate, newCheckInTime, newCheckOutTime, 1, 1, Some(LocalDateTime.now()))

      val actualBookingOutputFuture = bookingService.update(updatedBookingInput)

      ScalaFutures.whenReady(actualBookingOutputFuture.failed) { e =>
        e mustBe a[ValidationException]
      }
    }

  }

  "BookingService#occupancy" should {
    "return the sequence of room occupancies for a given day" in {
      val customerRepository = mock[CustomerRepository]
      val roomRepository = mock[RoomRepository]
      val bookingRepository = mock[BookingRepository]

      val room1 = Room(1, "Test Title", "Test Description", 1, 0, false)
      val customer1 = Customer(1, "Test", "Test", "test@test.com", "11111")
      val booking1 = Booking(1, checkInDate, checkOutDate, checkInTime, checkOutTime, 1, 1, LocalDateTime.now())

      val room2 = Room(2, "Test Title", "Test Description", 2, 2, true)

      val returnOccupancy = Seq((room1, Some((booking1, customer1, room1))), (room2, None))

      when(bookingRepository.getRoomsAndBookingsOnDate(checkInDate)).thenReturn(Future.successful(returnOccupancy))

      val bookingService = new BookingService(bookingRepository, customerRepository, roomRepository)

      val occupancyOutputSeqFuture = bookingService.occupancy(checkInDate)

      ScalaFutures.whenReady(occupancyOutputSeqFuture) { occupancyOutputSeq =>
        occupancyOutputSeq.size mustBe 2

        occupancyOutputSeq(0).room.id mustBe room1.id
        occupancyOutputSeq(0).status mustBe "Occupied"
        occupancyOutputSeq(0).booking.isDefined mustBe true

        occupancyOutputSeq(1).room.id mustBe room2.id
        occupancyOutputSeq(1).status mustBe "Available"
      }
    }
  }

  "BookingService#availability" should {
    "return room availability for a period of time" in {
      val customerRepository = mock[CustomerRepository]
      val roomRepository = mock[RoomRepository]
      val bookingRepository = mock[BookingRepository]

      val room1 = Room(1, "Test Title", "Test Description", 1, 0, false)
      val room2 = Room(2, "Test Title", "Test Description", 2, 2, true)

      when(bookingRepository.getRoomsWithoutBookingsInDateRange(checkInDate, checkOutDate)).thenReturn(Future.successful(Seq(room1, room2)))

      val bookingService = new BookingService(bookingRepository, customerRepository, roomRepository)

      val availabilityOutputSeqFuture = bookingService.availability(checkInDate, checkOutDate)

      ScalaFutures.whenReady(availabilityOutputSeqFuture) { availabilityOutput =>
        availabilityOutput.checkInDate mustBe checkInDate
        availabilityOutput.checkOutDate mustBe checkOutDate
        availabilityOutput.availability.size mustBe 2
        availabilityOutput.availability(0).id mustBe room1.id
        availabilityOutput.availability(1).id mustBe room2.id
      }
    }
  }

}
