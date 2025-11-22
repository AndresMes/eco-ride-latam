package edu.unimagdalena.passengerservice.mappers;

import edu.unimagdalena.passengerservice.dtos.requests.RatingDtoRequest;
import edu.unimagdalena.passengerservice.dtos.responses.RatingDtoResponse;
import edu.unimagdalena.passengerservice.entities.Rating;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface RatingMapper {


    @Mapping(target = "fromSub", source = "from.keycloakSub")
    @Mapping(target = "toId", source = "to.driverId")
    RatingDtoResponse toRatingDtoResponse(Rating rating);

    @Mapping(target = "from", ignore = true)
    @Mapping(target = "to", ignore = true)
    @Mapping(target = "ratingId", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    Rating toEntity(RatingDtoRequest dtoRequest);
}