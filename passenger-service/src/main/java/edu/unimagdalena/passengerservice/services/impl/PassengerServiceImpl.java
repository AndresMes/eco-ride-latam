package edu.unimagdalena.passengerservice.services.impl;

import edu.unimagdalena.passengerservice.dtos.requests.PassengerDtoRequest;
import edu.unimagdalena.passengerservice.dtos.responses.PassengerDtoResponse;
import edu.unimagdalena.passengerservice.dtos.responses.PassengerInfoDtoResponse;
import edu.unimagdalena.passengerservice.entities.Passenger;
import edu.unimagdalena.passengerservice.exceptions.PassengerAlreadyExistsException;
import edu.unimagdalena.passengerservice.exceptions.notFound.PassengerNotFoundException;
import edu.unimagdalena.passengerservice.mappers.PassengerMapper;
import edu.unimagdalena.passengerservice.repositories.PassengerRepository;
import edu.unimagdalena.passengerservice.services.PassengerService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class PassengerServiceImpl implements PassengerService {

    private final PassengerRepository passengerRepository;
    private final PassengerMapper passengerMapper;

    @Override
    public Mono<PassengerDtoResponse> findPassengerById(Long passengerId) {
        return passengerRepository.findById(passengerId)
                .switchIfEmpty(Mono.error(new PassengerNotFoundException("Passenger with ID: "+passengerId+" not found")))
                .map(passengerMapper::toPassengerDtoResponse);
    }

    @Override
    public Mono<PassengerInfoDtoResponse> findPassengerByKeycloakSub(String keycloakSub) {
        return passengerRepository.findByKeycloakSub(keycloakSub)
                .switchIfEmpty(Mono.error(new PassengerNotFoundException("Passenger with kyecloak sub: " + keycloakSub + " not found")))
                .map(passengerMapper::toInfoPassengerDtoResponse);
    }

    @Override
    public Mono<PassengerDtoResponse> savePassenger(String keycloakSub, PassengerDtoRequest dtoRequest) {

        if (dtoRequest == null) {
            return Mono.error(new IllegalArgumentException("Request cannot be null"));
        }

        return passengerRepository.findByKeycloakSub(keycloakSub)
                .flatMap(existing -> {
                    return Mono.<Passenger>error(
                            new PassengerAlreadyExistsException(
                                    "Passenger with keycloak sub: " + keycloakSub + " already exists"
                            )
                    );
                })
                .switchIfEmpty(
                        Mono.defer(() -> {
                            Passenger entity = passengerMapper.toEntity(dtoRequest);
                            entity.setKeycloakSub(keycloakSub);
                            entity.setRatingAvg(5.0);
                            entity.setCreatedAt(LocalDateTime.now());

                            return passengerRepository.save(entity)
                                    .onErrorMap(ex -> {
                                        return new PassengerAlreadyExistsException(
                                                "Error creating passenger: " + ex.getMessage()
                                        );
                                    });
                        })
                )
                .map(passengerMapper::toPassengerDtoResponse);
    }

    @Override
    public Mono<PassengerDtoResponse> updatePassenger(String keycloakSub, PassengerDtoRequest dtoRequest) {

        if (dtoRequest == null) {
            return Mono.error(new IllegalArgumentException("Request cannot be null"));
        }

        return passengerRepository.findByKeycloakSub(keycloakSub)
                .switchIfEmpty(
                        Mono.error(new PassengerNotFoundException(
                                "Passenger with keycloak sub: " + keycloakSub + " not found"
                        ))
                )
                .flatMap(existingPassenger -> {
                    existingPassenger.setName(dtoRequest.name());
                    existingPassenger.setEmail(dtoRequest.email());

                    return passengerRepository.save(existingPassenger)
                            .onErrorMap(ex -> {
                                return new RuntimeException(
                                        "Error updating passenger: " + ex.getMessage()
                                );
                            });
                })
                .map(passengerMapper::toPassengerDtoResponse);
    }

    @Override
    public Flux<PassengerDtoResponse> findAll() {
        return passengerRepository.findAll()
                .map(passengerMapper::toPassengerDtoResponse);
    }

}
