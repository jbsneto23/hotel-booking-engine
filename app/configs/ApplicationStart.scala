package configs

import com.google.inject.AbstractModule
import dtos.RoomInput
import services.RoomService

import javax.inject._
import scala.concurrent.ExecutionContext

@Singleton
class ApplicationStart @Inject() (roomService: RoomService)(implicit executionContext: ExecutionContext) {
  private val logger = play.api.Logger("application")
  roomService.create(RoomInput(None, "Simple Bedroom", "Simple bedroom with a single bed.", 1, 0, false))
  roomService.create(RoomInput(None, "Simple Suite", "Simple suite with a couple bed and a private bathroom.", 2, 0, true))
  roomService.create(RoomInput(None, "Triple Room", "Room with three single beds and a private bathroom.", 3, 0, true))
  roomService.create(RoomInput(None, "Family Room", "Suite for a family with a couple bed, two single beds and a private bathroom.", 2, 2, true))
  logger.info("Starting with test data")
}

class StartModule extends AbstractModule {
  override def configure() = {
    bind(classOf[ApplicationStart]).asEagerSingleton()
  }
}
