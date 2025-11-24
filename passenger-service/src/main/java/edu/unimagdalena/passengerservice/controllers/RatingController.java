package edu.unimagdalena.passengerservice.controllers;

import edu.unimagdalena.passengerservice.dtos.requests.RatingDtoRequest;
import edu.unimagdalena.passengerservice.dtos.responses.RatingAvgDtoResponse;
import edu.unimagdalena.passengerservice.dtos.responses.RatingDtoResponse;
import edu.unimagdalena.passengerservice.services.RatingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/ratings")
public class RatingController {

    private final RatingService ratingService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<RatingDtoResponse> toRateDriver(@Valid @RequestBody RatingDtoRequest dtoRequest){
        return ratingService.toRateDriver(dtoRequest);
    }

    @GetMapping("/driver/{driverId}")
    public Flux<RatingDtoResponse> findRatingsByDriver(@PathVariable Long driverId){
        return ratingService.findRatingsByDriver(driverId);
    }

    @GetMapping("/trip/{tripId}")
    public Flux<RatingDtoResponse> findRatingsByTrip(@PathVariable Long tripId){
        return ratingService.findRatingsByTrip(tripId);
    }

    @GetMapping("/{driverId}/average")
    public Mono<RatingAvgDtoResponse> calculateAverageRating(@PathVariable Long driverId){
        return ratingService.calculateRatingDriver(driverId);
    }
}