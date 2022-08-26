package repositories.slick.tables

import models.Room
import slick.jdbc.H2Profile.api._

class Rooms(tag: Tag) extends Table[Room](tag, "room") {
  def id               = column[Long]("id_room", O.PrimaryKey, O.AutoInc)
  def title            = column[String]("title")
  def description      = column[String]("description")
  def adultCapacity    = column[Int]("adult_capacity")
  def childrenCapacity = column[Int]("children_capacity")
  def privateBathroom  = column[Boolean]("private_bathroom")

  def * = (id, title, description, adultCapacity, childrenCapacity, privateBathroom).mapTo[Room]
}

object Rooms {
  val rooms = TableQuery[Rooms]
}