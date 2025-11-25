package edu.unimagdalena.passengerservice.mappers;

import edu.unimagdalena.passengerservice.dtos.requests.PassengerDtoRequest;
import edu.unimagdalena.passengerservice.dtos.responses.PassengerDtoResponse;
import edu.unimagdalena.passengerservice.dtos.responses.PassengerInfoDtoResponse;
import edu.unimagdalena.passengerservice.entities.Passenger;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PassengerMapper {

    PassengerDtoResponse toPassengerDtoResponse(Passenger passenger);

    PassengerInfoDtoResponse toInfoPassengerDtoResponse(Passenger passenger);

    Passenger toEntity(PassengerDtoRequest dtoRequest);
}
