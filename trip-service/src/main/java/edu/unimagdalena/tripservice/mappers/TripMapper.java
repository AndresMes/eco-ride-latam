package edu.unimagdalena.tripservice.mappers;

import edu.unimagdalena.tripservice.dtos.TripDtoRequest;
import edu.unimagdalena.tripservice.dtos.TripDtoResponse;
import edu.unimagdalena.tripservice.entities.Trip;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface TripMapper {

    TripDtoResponse toDtoResponse(Trip trip);
    Trip toEntity(TripDtoRequest dtoRequest);

    void updateTripFromDto(TripDtoRequest dtoRequest, @MappingTarget Trip trip);

}
