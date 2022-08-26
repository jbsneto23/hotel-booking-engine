package repositories.slick

import models.Customer
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import repositories.CustomerRepository
import repositories.slick.tables.Customers
import slick.jdbc.JdbcProfile

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class SlickCustomerRepository @Inject()(protected val dbConfigProvider: DatabaseConfigProvider)(implicit executionContext: ExecutionContext) extends HasDatabaseConfigProvider[JdbcProfile] with CustomerRepository {

  import Customers._
  import profile.api._

  def getAll(): Future[Seq[Customer]] = db.run {
    customers.result
  }

  def getById(id: Long): Future[Option[Customer]] = db.run {
    customers.filter(_.id === id).result.headOption
  }

  def create(customer: Customer): Future[Customer] = db.run {
    (customers returning customers.map(_.id) into ((r, generatedId) => r.copy(id = generatedId))) += customer
  }

  def update(customer: Customer): Future[Option[Customer]] = db.run {
    customers.filter(_.id === customer.id).update(customer).map {
      case 0 => None
      case _ => Some(customer)
    }
  }

  def delete(id: Long): Future[Boolean] = db.run {
    customers.filter(_.id === id).delete.map {
      case 0 => false
      case _ => true
    }
  }
}
