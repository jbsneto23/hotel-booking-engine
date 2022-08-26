package repositories.slick.tables

import models.Customer
import slick.jdbc.H2Profile.api._

class Customers(tag: Tag) extends Table[Customer](tag, "customer"){
  def id               = column[Long]("id_customer", O.PrimaryKey, O.AutoInc)
  def firstName        = column[String]("first_name")
  def lastName         = column[String]("last_name")
  def email            = column[String]("email")
  def phone            = column[String]("phone")

  def * = (id, firstName, lastName, email, phone).mapTo[Customer]
}

object Customers {
  val customers = TableQuery[Customers]
}
