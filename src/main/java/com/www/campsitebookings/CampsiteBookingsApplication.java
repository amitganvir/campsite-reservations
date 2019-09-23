package com.www.campsitebookings;

import com.www.campsitebookings.db.CampsiteAvailability;
import com.www.campsitebookings.db.repository.CampsiteRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@SpringBootApplication
public class CampsiteBookingsApplication {

    public static void main(String[] args) {
        SpringApplication.run(CampsiteBookingsApplication.class, args);
    }


    @Bean
    CommandLineRunner commandLineRunner(CampsiteRepository campsiteRepository) {


        return args -> {

            LocalDate currentDate = LocalDate.now();
            LocalDate endDate = LocalDate.now().plusDays(31);

            List<CampsiteAvailability> dates = new ArrayList<>();

            while (currentDate.isBefore(endDate)) {
                dates.add(CampsiteAvailability.builder().date(currentDate).build());
                currentDate = currentDate.plusDays(1);
            }

            campsiteRepository.saveAll(dates);

        };
    }

}
