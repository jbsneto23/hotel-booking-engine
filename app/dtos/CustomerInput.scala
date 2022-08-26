package dtos

import models.Customer
import play.api.data.Form
import play.api.data.Forms._

case class CustomerInput(id: Option[Long], firstName: String, lastName: String, email: String, phone: String)

object CustomerInput {

  implicit def fromCustomerInput(c: CustomerInput): Customer = Customer(c.id.getOrElse(0), c.firstName, c.lastName, c.email, c.phone)
  implicit def fromCustomer(c: Customer): CustomerInput = CustomerInput(Some(c.id), c.firstName, c.lastName, c.email, c.phone)

  val form = Form(
    mapping(
      "id" -> optional(longNumber),
      "firstName" -> nonEmptyText,
      "lastName" -> nonEmptyText,
      "email" -> email,
      "phone" -> text.verifying("error.phone", p => ("""[0-9 ]+""".r).matches(p))
    )(CustomerInput.apply)(CustomerInput.unapply)
  )

}
