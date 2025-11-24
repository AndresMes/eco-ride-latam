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
@Table("rating")
public class Rating {

    @Id
    @Column("rating_id")
    private Long ratingId;

    @Column("score")
    private Integer score;

    @Column("comment")
    private String comment;

    @Column("trip_id")
    private Long tripId;

    @Column("from_id")
    private Long fromId;

    @Column("to_id")
    private Long toId;

    @Column("created_at")
    private LocalDateTime createdAt;

}