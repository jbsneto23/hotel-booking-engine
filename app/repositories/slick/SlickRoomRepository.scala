package repositories.slick

import models.Room
import repositories.slick.tables.Rooms
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import repositories.RoomRepository
import slick.jdbc.JdbcProfile

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class SlickRoomRepository @Inject()(protected val dbConfigProvider: DatabaseConfigProvider)(implicit executionContext: ExecutionContext) extends HasDatabaseConfigProvider[JdbcProfile] with RoomRepository {

  import profile.api._
  import Rooms._

  def getAll(): Future[Seq[Room]] = db.run {
    rooms.result
  }

  def getById(id: Long): Future[Option[Room]] = db.run {
    rooms.filter(_.id === id).result.headOption
  }

  def create(room: Room): Future[Room] = db.run {
    (rooms returning rooms.map(_.id) into ((r, generatedId) => r.copy(id = generatedId))) += room
  }

  def update(room: Room): Future[Option[Room]] = db.run {
    rooms.filter(_.id === room.id).update(room).map {
      case 0 => None
      case _ => Some(room)
    }
  }

  def delete(id: Long): Future[Boolean] = db.run {
    rooms.filter(_.id === id).delete.map {
      case 0 => false
      case _ => true
    }
  }

}
