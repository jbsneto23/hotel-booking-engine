package services

import dtos.{CustomerInput, CustomerOutput}
import exceptions.ResourceNotFoundException
import repositories.CustomerRepository

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class CustomerService @Inject()(private val customerRepository: CustomerRepository)(implicit executionContext: ExecutionContext) {

  def getAll(): Future[Seq[CustomerOutput]] = {
    customerRepository.getAll().map(Customers => Customers.map(c => c))
  }

  def getById(id: Long): Future[CustomerOutput] = {
    customerRepository.getById(id).collect {
      case Some(c) => c
      case None => throw ResourceNotFoundException()
    }
  }

  def create(newCustomer: CustomerInput): Future[CustomerOutput] = {
    customerRepository.create(newCustomer).map(c => c)
  }

  def update(updatedCustomer: CustomerInput): Future[CustomerOutput] = {
    customerRepository.update(updatedCustomer).collect {
      case Some(c) => c
      case None => throw ResourceNotFoundException()
    }
  }

  def delete(id: Long): Future[Unit] = {
    customerRepository.delete(id).map { hasDeleted =>
      if (!hasDeleted) throw ResourceNotFoundException()
    }
  }

}
