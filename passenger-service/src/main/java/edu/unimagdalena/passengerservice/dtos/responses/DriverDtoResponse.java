package edu.unimagdalena.passengerservice.dtos.responses;

import edu.unimagdalena.passengerservice.enums.VerificationStatus;

public record DriverDtoResponse(
        Long driverId,
        String licenseNo,
        String carPlate,
        VerificationStatus verificationStatus,
        Long passengerId
) {
}
