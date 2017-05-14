package model

import org.scalatest.{FlatSpec, Matchers}

import scala.util.Success

class ReservationServiceSpec extends FlatSpec with Matchers{
  "Reservation service" should "pass the happy path" in {
    val suspect = new ReservationService()
    val mId = "1m"
    val sId = "1s"

    suspect.registerTheMovie(mId, "testTitle", 1, sId)
    suspect.reserveASeat(mId, sId) shouldEqual Success(SeatReserved)
    suspect.getStateOfProjection(mId, sId).get.reservedSeats shouldBe 1
    suspect.reserveASeat(mId, sId).isFailure shouldBe true
  }
}
