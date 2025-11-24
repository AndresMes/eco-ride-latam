package edu.unimagdalena.passengerservice.mappers;

import edu.unimagdalena.passengerservice.dtos.requests.RatingDtoRequest;
import edu.unimagdalena.passengerservice.dtos.responses.RatingDtoResponse;
import edu.unimagdalena.passengerservice.entities.Rating;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface RatingMapper {


    RatingDtoResponse toRatingDtoResponse(Rating rating);
    Rating toEntity(RatingDtoRequest dtoRequest);
}