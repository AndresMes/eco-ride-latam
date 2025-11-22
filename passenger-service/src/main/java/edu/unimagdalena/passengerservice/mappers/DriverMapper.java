package edu.unimagdalena.passengerservice.mappers;

import edu.unimagdalena.passengerservice.dtos.requests.DriverDtoRequest;
import edu.unimagdalena.passengerservice.dtos.responses.DriverDtoResponse;
import edu.unimagdalena.passengerservice.entities.DriverProfile;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface DriverMapper {

    @Mapping(target = "passengerId", source = "passenger.passengerId")
    DriverDtoResponse toDriverDto(DriverProfile driver);

    @Mapping(target = "passenger", ignore = true)
    DriverProfile toEntity(DriverDtoRequest dtoRequest);


}
