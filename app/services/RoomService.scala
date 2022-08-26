package services

import dtos.{RoomInput, RoomOutput}
import exceptions.ResourceNotFoundException
import repositories.RoomRepository

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class RoomService @Inject()(private val roomRepository: RoomRepository)(implicit executionContext: ExecutionContext) {

  def getAll(): Future[Seq[RoomOutput]] = {
    roomRepository.getAll().map(rooms => rooms.map(r => r))
  }

  def getById(id: Long): Future[RoomOutput] = {
    roomRepository.getById(id).collect {
      case Some(r) => r
      case None => throw ResourceNotFoundException()
    }
  }

  def create(newRoom: RoomInput): Future[RoomOutput] = {
    roomRepository.create(newRoom).map(r => r)
  }

  def update(updatedRoom: RoomInput): Future[RoomOutput] = {
    roomRepository.update(updatedRoom).collect {
      case Some(r) => r
      case None => throw ResourceNotFoundException()
    }
  }

  def delete(id: Long): Future[Unit] = {
    roomRepository.delete(id).map { hasDeleted =>
      if (!hasDeleted) throw ResourceNotFoundException()
    }
  }

}
