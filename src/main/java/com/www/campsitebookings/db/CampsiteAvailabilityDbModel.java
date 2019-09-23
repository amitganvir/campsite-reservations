package com.www.campsitebookings.db;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CampsiteAvailabilityDbModel {
    private LocalDate date;
    private Long version;
}
