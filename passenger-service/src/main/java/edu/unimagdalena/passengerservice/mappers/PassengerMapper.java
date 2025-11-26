package edu.unimagdalena.passengerservice.mappers;

import edu.unimagdalena.passengerservice.dtos.requests.PassengerDtoRequest;
import edu.unimagdalena.passengerservice.dtos.responses.PassengerDtoResponse;
import edu.unimagdalena.passengerservice.dtos.responses.PassengerInfoDtoResponse;
import edu.unimagdalena.passengerservice.entities.Passenger;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PassengerMapper {

    PassengerDtoResponse toPassengerDtoResponse(Passenger passenger);

    PassengerInfoDtoResponse toInfoPassengerDtoResponse(Passenger passenger);

    @Mapping(source = "name", target = "name")
    @Mapping(source = "email", target="email")
    Passenger toEntity(PassengerDtoRequest dtoRequest);
}
