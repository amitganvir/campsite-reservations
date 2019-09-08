package com.campsitereservations;

import com.jayway.jsonpath.JsonPath;
import lombok.extern.log4j.Log4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.time.LocalDate;
import java.util.Date;
import java.util.logging.Logger;

import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.core.Is.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class CampsiteReservationsApplicationSuite2Tests {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void shouldGetAllAvailableBookingDates() throws Exception {

        String checkinDate = LocalDate.now().minusDays(10).toString();
        String checkoutDate = LocalDate.now().minusDays(5).toString();

        this.mockMvc.perform(get("/v1/api/available-dates?checkinDate="+checkinDate+"&checkoutDate="+ checkoutDate))
                .andExpect(status().isOk())
                .andExpect(jsonPath("message",
                        is("Failed to get dates for booking between "+checkinDate + " - " + checkoutDate)))
                .andExpect(jsonPath("availableDates", nullValue()));
    }

    @Test
    public void shouldAddEditDeleteReservationWithValidCheckinCheckoutDates() throws Exception {

        System.out.println("Execution start time of shouldAddEditDeleteReservationWithValidCheckinCheckoutDates " + new Date().getTime());

        LocalDate checkinDate = LocalDate.now().plusDays(10);
        LocalDate checkoutDate = LocalDate.now().plusDays(12);

        String inputParams = "firstName=Amit&lastName=Ganvir&email=aganvir@gmail.com&checkinDate=" +
                checkinDate + "&checkoutDate=" + checkoutDate;

        MvcResult addReservationResult = this.mockMvc.perform(post("/v1/api/add-reservation?" + inputParams))
                .andExpect(status().isOk())
                .andExpect(jsonPath("message", is("Campsite booked successfully")))
                .andExpect(jsonPath("reservationModel", notNullValue()))
                .andReturn();

        String reservationId = JsonPath.parse(addReservationResult.getResponse().getContentAsString())
                .read("reservationModel.reservationId");


        LocalDate newCheckinDate = checkinDate.minusDays(1);
        LocalDate newCheckoutDate = checkoutDate.minusDays(1);
        String updateParams = "firstName=Amit&lastName=Ganvir&email=aganvir@gmail.com&checkinDate=" +
                newCheckinDate + "&checkoutDate=" + newCheckoutDate + "&reservationId=" + reservationId;
        MvcResult updatedMvcResult = this.mockMvc.perform(put("/v1/api/update-reservation?" + updateParams))
                .andExpect(status().isOk())
                .andExpect(jsonPath("message", is("Campsite booking updated successfully")))
                .andExpect(jsonPath("reservationModel", notNullValue()))
                .andExpect(jsonPath("reservationModel.checkinDate", is(newCheckinDate.toString())))
                .andExpect(jsonPath("reservationModel.checkoutDate", is(newCheckoutDate.toString())))
                .andReturn();

        reservationId = JsonPath.parse(updatedMvcResult.getResponse().getContentAsString())
                .read("reservationModel.reservationId");

        String deleteParams = "?reservationId=" + reservationId;

        this.mockMvc.perform(delete("/v1/api/cancel-reservation" + deleteParams))
                .andExpect(status().isOk())
                .andExpect(jsonPath("message", is("Reservation cancelled successfully for reservation id : " + reservationId)));
    }

}
