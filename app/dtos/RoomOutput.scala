package dtos

import models.Room

case class RoomOutput(id: Long, title: String, description: String, adultCapacity: Int, childrenCapacity: Int, privateBathroom: Boolean)

object RoomOutput {
  implicit def fromRoomOutput(r: RoomOutput): Room = Room(r.id, r.title, r.description, r.adultCapacity, r.childrenCapacity, r.privateBathroom)
  implicit def fromRoom(r: Room): RoomOutput = RoomOutput(r.id, r.title, r.description, r.adultCapacity, r.childrenCapacity, r.privateBathroom)
}
