package services

import dtos.{CustomerInput, CustomerOutput}
import exceptions.ResourceNotFoundException
import models.Customer
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito._
import org.scalatest.concurrent.ScalaFutures
import org.scalatestplus.mockito.MockitoSugar
import org.scalatestplus.play._
import play.api.test.Helpers._
import repositories.CustomerRepository

import scala.concurrent.Future

class CustomerServiceSpec extends PlaySpec with ScalaFutures with MockitoSugar {

  implicit val executionContext = stubControllerComponents().executionContext

  "CustomerService#create" should {
    "return new customer object when input customer  is provided" in {
      val customerRepository = mock[CustomerRepository]
      val returnedCustomer = Customer(1, "Test", "Test", "test@test.com", "11111")
      when(customerRepository.create(any[Customer])).thenReturn(Future.successful(returnedCustomer))

      val customerService = new CustomerService(customerRepository)
      val newCustomerInput = CustomerInput(None, "Test", "Test", "test@test.com", "11111")
      val actualCustomerOutputFuture = customerService.create(newCustomerInput)

      val expectedCustomerOutput = CustomerOutput(1, "Test", "Test", "test@test.com", "11111")

      actualCustomerOutputFuture map { actualCustomerOutput => actualCustomerOutput mustBe expectedCustomerOutput }
    }
  }

  "CustomerService#getById" should {

    "return some customer object with id 1 when id 1 is provided and it exists in the repository" in {
      val customerRepository = mock[CustomerRepository]
      val returnedCustomer = Customer(1, "Test", "Test", "test@test.com", "11111")
      when(customerRepository.getById(1)).thenReturn(Future.successful(Some(returnedCustomer)))

      val customerService = new CustomerService(customerRepository)
      val actualCustomerOutputFuture = customerService.getById(1)

      val expectedCustomerOutput = Some(CustomerOutput(1, "Test", "Test", "test@test.com", "11111"))

      actualCustomerOutputFuture map { actualCustomerOutput => actualCustomerOutput mustBe expectedCustomerOutput }
    }

    "throw ResourceNotFoundException if an id is provided but it does not exists in the repository" in {
      val customerRepository = mock[CustomerRepository]
      when(customerRepository.getById(any[Int])).thenReturn(Future.successful(None))

      val customerService = new CustomerService(customerRepository)

      val futureCustomer = customerService.getById(1)

      ScalaFutures.whenReady(futureCustomer.failed) { e =>
        e mustBe a[ResourceNotFoundException]
      }
    }

  }

  "CustomerService#getAll" should {

    "return a sequence with the customers available in the repository" in {
      val customerRepository = mock[CustomerRepository]
      val customer1 = Customer(1, "Test", "Test", "test@test.com", "11111")
      val customer2 = Customer(2, "Test 2", "Test 2", "test2@test.com", "22222")
      when(customerRepository.getAll()).thenReturn(Future.successful(Seq(customer1, customer2)))

      val customerService = new CustomerService(customerRepository)

      val expectedCustomerOutput1 = CustomerOutput(1, "Test", "Test", "test@test.com", "11111")
      val expectedCustomerOutput2 = CustomerOutput(2, "Test 2", "Test 2", "test2@test.com", "22222")

      ScalaFutures.whenReady(customerService.getAll()) { customers =>
        customers mustBe Seq(expectedCustomerOutput1, expectedCustomerOutput2)
      }
    }

    "return an empty sequence if no customers are available in the repository" in {
      val customerRepository = mock[CustomerRepository]
      when(customerRepository.getAll()).thenReturn(Future.successful(Seq()))

      val customerService = new CustomerService(customerRepository)

      ScalaFutures.whenReady(customerService.getAll()) { customers =>
        customers mustBe Seq()
      }
    }

  }

  "CustomerService#update" should {

    "return an updated customer object when an input customer is provided and it exists in the repository" in {
      val customerRepository = mock[CustomerRepository]
      val returnedCustomer = Customer(1, "Test", "Test", "test@test.com", "11111")
      when(customerRepository.update(any[Customer])).thenReturn(Future.successful(Some(returnedCustomer)))

      val customerService = new CustomerService(customerRepository)
      val updatedCustomerInput = CustomerInput(Some(1), "Test", "Test", "test@test.com", "11111")
      val actualCustomerOutputFuture = customerService.update(updatedCustomerInput)

      val expectedCustomerOutput = CustomerOutput(1, "Test", "Test", "test@test.com", "11111")

      actualCustomerOutputFuture map { actualCustomerOutput => actualCustomerOutput mustBe expectedCustomerOutput }
    }

    "throw ResourceNotFoundException if an input customer is provided but it does not exists in the repository" in {
      val customerRepository = mock[CustomerRepository]
      when(customerRepository.update(any[Customer])).thenReturn(Future.successful(None))

      val customerService = new CustomerService(customerRepository)
      val updatedCustomerInput = CustomerInput(Some(1), "Test", "Test", "test@test.com", "11111")
      val actualCustomerOutputFuture = customerService.update(updatedCustomerInput)

      ScalaFutures.whenReady(actualCustomerOutputFuture.failed) { e =>
        e mustBe a[ResourceNotFoundException]
      }
    }

  }

  "CustomerService#delete" should {
    "delete the customer if an id is provided and it exists in the repository" in {
      val customerRepository = mock[CustomerRepository]
      when(customerRepository.delete(1)).thenReturn(Future.successful(true))

      val customerService = new CustomerService(customerRepository)

      ScalaFutures.whenReady(customerService.delete(1))(u => u)
    }

    "throw ResourceNotFoundException if an id is provided but it does not exists in the repository" in {
      val customerRepository = mock[CustomerRepository]
      when(customerRepository.delete(any[Int])).thenReturn(Future.successful(false))

      val customerService = new CustomerService(customerRepository)

      ScalaFutures.whenReady(customerService.delete(1).failed) { e =>
        e mustBe a[ResourceNotFoundException]
      }
    }

  }


}
