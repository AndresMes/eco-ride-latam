package edu.unimagdalena.passengerservice.controllers;

import edu.unimagdalena.passengerservice.dtos.requests.RatingDtoRequest;
import edu.unimagdalena.passengerservice.dtos.responses.RatingAvgDtoResponse;
import edu.unimagdalena.passengerservice.dtos.responses.RatingDtoResponse;
import edu.unimagdalena.passengerservice.services.RatingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/ratings")
public class RatingController {

    private final RatingService ratingService;

    @PostMapping
    public ResponseEntity<RatingDtoResponse> toRateDriver(@Valid @RequestBody RatingDtoRequest dtoRequest){
        return ResponseEntity.status(HttpStatus.CREATED).body(ratingService.toRateDriver(dtoRequest));
    }

    @GetMapping("/driver/{driverId}")
    public ResponseEntity<List<RatingDtoResponse>> findRatingsByDriver(@PathVariable Long driverId){
        return ResponseEntity.ok(ratingService.findRatingsByDriver(driverId));
    }

    @GetMapping("/trip/{tripId}")
    public ResponseEntity<List<RatingDtoResponse>> findRatingsByTrip(@PathVariable Long tripId){
        return ResponseEntity.ok(ratingService.findRatingsByTrip(tripId));
    }

    @GetMapping("/{driverId}/average")
    public ResponseEntity<RatingAvgDtoResponse> calculateAvarageRating(@PathVariable Long driverId){
        return ResponseEntity.ok(ratingService.calculateRatingDriver(driverId));
    }
 }
