package edu.unimagdalena.passengerservice.repositories;

import edu.unimagdalena.passengerservice.entities.Rating;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;


public interface RatingRepository extends JpaRepository<Rating, Long> {

    List<Rating> findByTo_DriverId(Long toId);

    List<Rating> findByTripId(Long tripId);

    @Query("SELECT AVG(r.score) FROM Rating r WHERE r.to.driverId = :driverId")
    Double calculateAverageRating(Long driverId);

    boolean existsByTripIdAndFrom_PassengerId(Long tripId, Long fromPassengerId);

}
