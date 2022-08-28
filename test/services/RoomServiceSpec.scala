package services

import dtos.{RoomInput, RoomOutput}
import exceptions.ResourceNotFoundException
import models.Room
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito._
import org.scalatest.concurrent.ScalaFutures
import org.scalatestplus.mockito.MockitoSugar
import org.scalatestplus.play._
import play.api.test.Helpers._
import repositories.RoomRepository

import scala.concurrent.Future

class RoomServiceSpec extends PlaySpec with ScalaFutures with MockitoSugar {

  implicit val executionContext = stubControllerComponents().executionContext

  "RoomService#create" should {
    "return new room object when input room  is provided" in {
      val roomRepository = mock[RoomRepository]
      val returnedRoom = Room(1, "Test Title", "Test Description", 1, 0, false)
      when(roomRepository.create(any[Room])).thenReturn(Future.successful(returnedRoom))

      val roomService = new RoomService(roomRepository)
      val newRoomInput = RoomInput(None, "Test Title", "Test Description", 1, 0, false)
      val actualRoomOutputFuture = roomService.create(newRoomInput)

      val expectedRoomOutput = RoomOutput(1, "Test Title", "Test Description", 1, 0, false)

      ScalaFutures.whenReady(actualRoomOutputFuture) { actualRoomOutput =>
        actualRoomOutput mustBe expectedRoomOutput
      }
    }
  }

  "RoomService#getById" should {

    "return some room object with id 1 when id 1 is provided and it exists in the repository" in {
      val roomRepository = mock[RoomRepository]
      val returnedRoom = Room(1, "Test Title", "Test Description", 1, 0, false)
      when(roomRepository.getById(1)).thenReturn(Future.successful(Some(returnedRoom)))

      val roomService = new RoomService(roomRepository)
      val actualRoomOutputFuture = roomService.getById(1)

      val expectedRoomOutput = RoomOutput(1, "Test Title", "Test Description", 1, 0, false)

      ScalaFutures.whenReady(actualRoomOutputFuture) { actualRoomOutput =>
        actualRoomOutput mustBe expectedRoomOutput
      }
    }

    "throw ResourceNotFoundException if an id is provided but it does not exists in the repository" in {
      val roomRepository = mock[RoomRepository]
      when(roomRepository.getById(any[Int])).thenReturn(Future.successful(None))

      val roomService = new RoomService(roomRepository)

      val futureRoom = roomService.getById(1)

      ScalaFutures.whenReady(futureRoom.failed) { e =>
        e mustBe a[ResourceNotFoundException]
      }
    }

  }

  "RoomService#getAll" should {

    "return a sequence with the rooms available in the repository" in {
      val roomRepository = mock[RoomRepository]
      val room1 = Room(1, "Test Title", "Test Description", 1, 0, false)
      val room2 = Room(2, "Test Title 2", "Test Description 2", 2, 0, false)
      when(roomRepository.getAll()).thenReturn(Future.successful(Seq(room1, room2)))

      val roomService = new RoomService(roomRepository)

      val expectedRoomOutput1 = RoomOutput(1, "Test Title", "Test Description", 1, 0, false)
      val expectedRoomOutput2 = RoomOutput(2, "Test Title 2", "Test Description 2", 2, 0, false)

      ScalaFutures.whenReady(roomService.getAll()) { rooms =>
        rooms mustBe Seq(expectedRoomOutput1, expectedRoomOutput2)
      }
    }

    "return an empty sequence if no rooms are available in the repository" in {
      val roomRepository = mock[RoomRepository]
      when(roomRepository.getAll()).thenReturn(Future.successful(Seq()))

      val roomService = new RoomService(roomRepository)

      ScalaFutures.whenReady(roomService.getAll()) { rooms =>
        rooms mustBe Seq()
      }
    }
  }

  "RoomService#update" should {

    "return an updated room object when an input room is provided and it exists in the repository" in {
      val roomRepository = mock[RoomRepository]
      val returnedRoom = Room(1, "Test Title", "Test Description", 1, 0, false)
      when(roomRepository.update(any[Room])).thenReturn(Future.successful(Some(returnedRoom)))

      val roomService = new RoomService(roomRepository)
      val updatedRoomInput = RoomInput(Some(1), "Test Title", "Test Description", 1, 0, false)
      val actualRoomOutputFuture = roomService.update(updatedRoomInput)

      val expectedRoomOutput = RoomOutput(1, "Test Title", "Test Description", 1, 0, false)

      ScalaFutures.whenReady(actualRoomOutputFuture) { actualRoomOutput =>
        actualRoomOutput mustBe expectedRoomOutput
      }
    }

    "throw ResourceNotFoundException if an input room is provided but it does not exists in the repository" in {
      val roomRepository = mock[RoomRepository]
      when(roomRepository.update(any[Room])).thenReturn(Future.successful(None))

      val roomService = new RoomService(roomRepository)
      val updatedRoomInput = RoomInput(Some(1), "Test Title", "Test Description", 1, 0, false)
      val actualRoomOutputFuture = roomService.update(updatedRoomInput)

      ScalaFutures.whenReady(actualRoomOutputFuture.failed) { e =>
        e mustBe a[ResourceNotFoundException]
      }
    }
  }

  "RoomService#delete" should {
    "delete the room if an id is provided and it exists in the repository" in {
      val roomRepository = mock[RoomRepository]
      when(roomRepository.delete(1)).thenReturn(Future.successful(true))

      val roomService = new RoomService(roomRepository)

      ScalaFutures.whenReady(roomService.delete(1))(u => u)
    }

    "throw ResourceNotFoundException if an id is provided but it does not exists in the repository" in {
      val roomRepository = mock[RoomRepository]
      when(roomRepository.delete(any[Int])).thenReturn(Future.successful(false))

      val roomService = new RoomService(roomRepository)

      ScalaFutures.whenReady(roomService.delete(1).failed) { e =>
        e mustBe a[ResourceNotFoundException]
      }
    }
  }

}
