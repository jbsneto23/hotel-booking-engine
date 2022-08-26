package models

case class Room(id: Long, title: String, description: String, adultCapacity: Int = 1, childrenCapacity: Int = 0, privateBathroom: Boolean = false)
