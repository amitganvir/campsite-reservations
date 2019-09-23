package com.www.campsitebookings.db;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Data
@Builder
@Entity
@Table(name = "CampsiteAvailability")
@NoArgsConstructor
@AllArgsConstructor
public class CampsiteAvailability {

    @Id
    private LocalDate date;

    @ManyToOne(cascade = CascadeType.ALL, targetEntity = Reservation.class)
    @JoinColumn(name = "reservationId")
    private Reservation reservation;

    @Version
    @NotNull
    private Long version;
}
