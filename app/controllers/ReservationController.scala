package controllers

import javax.inject.Inject

import com.omertron.imdbapi.ImdbApi
import model._
import play.api.mvc.{Action, Controller}

import scala.util.Try

class ReservationController @Inject()() extends Controller {
  val imdbApi = new ImdbApi() // wrong place for the configuration

  val reservationService = new ReservationService(el => imdbApi.getFullDetails(el).getTitle)

  def registerTheMovie = Action { implicit request =>
    println(request.body)
    val json = request.body.asJson.get

    val imdbId: ImdbId = (json \ "imdbId").as[String]
    val seatsNumber: SeatsQuantity = (json \ "availableSeats").as[Int]
    val screenId: ScreenId = (json \ "screenId").as[String]

    handle( reservationService.registerTheMovie(imdbId, seatsNumber, screenId))
  }

  def reserveASeat = Action { implicit request =>
    val json = request.body.asJson.get

    val imdbId: ImdbId = (json \ "imdbId").as[String]
    val screenId: ScreenId = (json \ "screenId").as[String]

    handle( reservationService.reserveASeat(imdbId, screenId))
  }

  def getStateOfProjection(imdbId: ImdbId, screenId: ScreenId) = Action { implicit request =>
    handle(reservationService.getStateOfProjection(imdbId, screenId))
  }

  private def handle(result: Try[AnyRef]) =
    if(result.isFailure)
      throw result.failed.get
    else
      Ok(result.toString)
}
