package dtos

import models.Room

import play.api.data._
import play.api.data.Forms._

case class RoomInput(id: Option[Long], title: String, description: String, adultCapacity: Int, childrenCapacity: Int, privateBathroom: Boolean)

object RoomInput {

  implicit def fromRoomInput(r: RoomInput): Room = Room(r.id.getOrElse(0), r.title, r.description, r.adultCapacity, r.childrenCapacity, r.privateBathroom)
  implicit def fromRoom(r: Room): RoomInput = RoomInput(Some(r.id), r.title, r.description, r.adultCapacity, r.childrenCapacity, r.privateBathroom)

  val form = Form (
    mapping(
      "id" -> optional(longNumber),
      "title" -> nonEmptyText,
      "description" -> nonEmptyText,
      "adultCapacity" -> number(min = 0),
      "childrenCapacity" -> number(min = 0),
      "privateBathroom" -> boolean
    )(RoomInput.apply)(RoomInput.unapply)
  )

}