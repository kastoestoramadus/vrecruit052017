package model

import scala.util.{Failure, Success, Try}

class ReservationService ( titleGetter: ImdbId => String) {

  private var movies = Set[MovieProjection]()
  private var freeSeats = Map[ProjectionId, SeatsQuantity]()

  def registerTheMovie(imdbId: ImdbId, availableSeats: SeatsQuantity, screenId: ScreenId): Try[MovieProjection] = {
    val newEl = MovieProjection(imdbId: ImdbId, availableSeats: SeatsQuantity, screenId: ScreenId)
    if(freeSeats.contains(newEl.projectionId))
      Failure(MovieAlreadyRegistered)
    else {
      movies += newEl // FIXME not atomic
      freeSeats += newEl.projectionId -> newEl.availableSeats
      Success(newEl)
    }
  }

  def reserveASeat(imdbId: ImdbId, screenId: ScreenId): Try[SeatReserved.type] = {
    val id: ProjectionId = imdbId+screenId
    if(freeSeats.contains(id)) {
      val quantity = freeSeats(id)
      if (quantity > 0) {
        freeSeats += id -> (quantity - 1) // FIXME not atomic
        Success(SeatReserved)
      } else Failure(NoSeatsLeft)
    } else {
      Failure(NoSuchMovieProjection)
    }
  }

  def getStateOfProjection(imdbId: ImdbId, screenId: ScreenId): Try[StateOfProjection] = {
    movies.find(el => el.imdbId == imdbId && el.screenId == screenId)
      .map{ movie =>
        import movie.availableSeats
        Success(
          StateOfProjection(imdbId, screenId, titleGetter(imdbId),
            availableSeats, availableSeats - freeSeats(movie.projectionId))
        )
      }.getOrElse(Failure(NoSuchMovieProjection))
  }
}

case class StateOfProjection(imdbId: ImdbId, screenId: ScreenId,
                             movieTitle: String, availableSeats: SeatsQuantity, reservedSeats: SeatsQuantity)

case object NoSeatsLeft extends RuntimeException
case object NoSuchMovieProjection extends RuntimeException

// may have separate id and the date
case class MovieProjection(imdbId: ImdbId, availableSeats: SeatsQuantity, screenId: ScreenId) {
  def projectionId: ProjectionId = imdbId + screenId
}
case object MovieAlreadyRegistered extends RuntimeException
case object SeatReserved // should have a ref to customer