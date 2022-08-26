package repositories

import com.google.inject.ImplementedBy
import models.Room
import repositories.slick.SlickRoomRepository

import scala.concurrent.Future

@ImplementedBy(classOf[SlickRoomRepository])
trait RoomRepository {
  def getAll(): Future[Seq[Room]]

  def getById(id: Long): Future[Option[Room]]

  def create(room: Room): Future[Room]

  def update(room: Room): Future[Option[Room]]

  def delete(id: Long): Future[Boolean]
}
