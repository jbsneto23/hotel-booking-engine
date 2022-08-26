package controllers

import dtos.{CustomerInput, CustomerOutput}
import play.api.i18n.I18nSupport
import play.api.libs.json.Json
import play.api.mvc.{AbstractController, AnyContent, ControllerComponents, Request}
import services.CustomerService

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class CustomerController @Inject()(val customerService: CustomerService, val cc: ControllerComponents)(implicit executionContext: ExecutionContext) extends AbstractController(cc) with I18nSupport {

  implicit val customerOutputJsonFormat = Json.format[CustomerOutput]
  implicit val customerInputJsonFormat = Json.format[CustomerInput]

  def getAll() = Action.async { implicit request: Request[AnyContent] =>
    customerService.getAll().map(customers => {
      Ok(Json.toJson(customers))
    })
  }

  def getById(id: Long) = Action.async { implicit request: Request[AnyContent] =>
    customerService.getById(id).map(customer => Ok(Json.toJson(customer)))
  }

  def create() = Action.async { implicit request: Request[AnyContent] =>
    CustomerInput.form.bindFromRequest().fold(
      formWithErrors => {
        Future.successful(BadRequest(formWithErrors.errorsAsJson))
      },
      newCustomer => {
        customerService.create(newCustomer).map(c => Created(Json.toJson(c)))
      }
    )
  }

  def update(id: Long) = Action.async { implicit request: Request[AnyContent] =>
    CustomerInput.form.bindFromRequest().fold(
      formWithErrors => {
        Future.successful(BadRequest(formWithErrors.errorsAsJson))
      },
      updatedCustomer => {
        customerService.update(updatedCustomer.copy(id = Some(id))).map(customer => Ok(Json.toJson(customer)))
      }
    )
  }

  def delete(id: Long) = Action.async { implicit request: Request[AnyContent] =>
    customerService.delete(id).map(_ => NoContent)
  }

}
