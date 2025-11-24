package edu.unimagdalena.passengerservice.mappers;

import edu.unimagdalena.passengerservice.dtos.requests.DriverDtoRequest;
import edu.unimagdalena.passengerservice.dtos.responses.DriverDtoResponse;
import edu.unimagdalena.passengerservice.entities.DriverProfile;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface DriverMapper {

    @Mapping(target = "passengerId", source = "passengerId")
    DriverDtoResponse toDriverDto(DriverProfile driver);

    DriverProfile toEntity(DriverDtoRequest dtoRequest);


}
