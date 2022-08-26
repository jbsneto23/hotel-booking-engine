package dtos

import models.Customer

case class CustomerOutput(id: Long, firstName: String, lastName: String, email: String, phone: String)

object CustomerOutput{
  implicit def fromCustomerOutput(c: CustomerOutput): Customer = Customer(c.id, c.firstName, c.lastName, c.email, c.phone)
  implicit def fromCustomer(c: Customer): CustomerOutput = CustomerOutput(c.id, c.firstName, c.lastName, c.email, c.phone)
}