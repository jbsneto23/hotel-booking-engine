package repositories

import com.google.inject.ImplementedBy
import models.Customer
import repositories.slick.SlickCustomerRepository

import scala.concurrent.Future

@ImplementedBy(classOf[SlickCustomerRepository])
trait CustomerRepository {
  def getAll(): Future[Seq[Customer]]

  def getById(id: Long): Future[Option[Customer]]

  def create(customer: Customer): Future[Customer]

  def update(customer: Customer): Future[Option[Customer]]

  def delete(id: Long): Future[Boolean]
}
