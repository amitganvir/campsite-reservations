package com.www.campsitebookings.db;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@Entity
@Table(name = "Reservation")
@AllArgsConstructor
@NoArgsConstructor
public class Reservation {

    @Id
    private String reservationId;
    private String name;
    private String email;

    @OneToMany(cascade = CascadeType.ALL, targetEntity = CampsiteAvailability.class, mappedBy = "reservation")
    private List<CampsiteAvailability> campsiteAvailabilities;
}
