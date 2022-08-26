package configs

import com.google.inject.AbstractModule
import dtos.{CustomerInput, RoomInput}
import services._

import javax.inject._
import scala.concurrent.ExecutionContext

@Singleton
class ApplicationStart @Inject() (roomService: RoomService, customerService: CustomerService)(implicit executionContext: ExecutionContext) {
  private val logger = play.api.Logger("application")

  // Rooms Test Data
  roomService.create(RoomInput(None, "Simple Bedroom", "Simple bedroom with a single bed.", 1, 0, false))
  roomService.create(RoomInput(None, "Simple Suite", "Simple suite with a couple bed and a private bathroom.", 2, 0, true))
  roomService.create(RoomInput(None, "Triple Room", "Room with three single beds and a private bathroom.", 3, 0, true))
  roomService.create(RoomInput(None, "Family Room", "Suite for a family with a couple bed, two single beds and a private bathroom.", 2, 2, true))

  // Customers Test Data
  customerService.create(CustomerInput(None, "John", "Doe", "johndoe@test.com", "1111111"))
  customerService.create(CustomerInput(None, "Jane", "Doe", "janedoe@test.com", "2222222"))

  logger.info("Starting with test data")
}

class StartModule extends AbstractModule {
  override def configure() = {
    bind(classOf[ApplicationStart]).asEagerSingleton()
  }
}
