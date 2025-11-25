package edu.unimagdalena.passengerservice.entities;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table("passengers")
public class Passenger {

    @Id
    @Column("passenger_id")
    private Long passengerId;

    @Column("keycloak_sub")
    private String keycloakSub;

    @Column("name")
    private String name;

    @Column("email")
    private String email;

    @Column("rating_avg")
    private Double ratingAvg;

    @Column("created_at")
    private LocalDateTime createdAt;
}