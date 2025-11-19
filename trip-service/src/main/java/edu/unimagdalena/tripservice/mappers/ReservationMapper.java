package edu.unimagdalena.tripservice.mappers;

import edu.unimagdalena.tripservice.dtos.requests.ReservationDtoRequest;
import edu.unimagdalena.tripservice.dtos.responses.ReservationCreatedDtoResponse;
import edu.unimagdalena.tripservice.dtos.responses.ReservationDtoResponse;
import edu.unimagdalena.tripservice.entities.Reservation;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ReservationMapper {

    @Mapping(target = "tripId", source = "trip.tripId")
    ReservationDtoResponse toDtoResponse(Reservation reservation);

    ReservationCreatedDtoResponse toCreatedDtoResponse(Reservation reservation);

    @Mapping(target = "trip", ignore = true)
    Reservation toEntity(ReservationDtoRequest dtoRequest);
}
