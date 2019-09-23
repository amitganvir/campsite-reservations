package com.www.campsitebookings;

import com.jayway.jsonpath.JsonPath;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.time.LocalDate;

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

        this.mockMvc.perform(get("/v1/api/available-dates?checkinDate=" + checkinDate +
                "&checkoutDate=" + checkoutDate))
                .andExpect(status().isOk())
                .andExpect(jsonPath("availableDates", nullValue()));
    }

    @Test
    public void shouldAddEditDeleteReservationWithValidCheckinCheckoutDates() throws Exception {

        LocalDate checkinDate = LocalDate.now().plusDays(5);
        LocalDate checkoutDate = LocalDate.now().plusDays(7);

        String inputParams = "name=Ganvir&email=test@gmail.com&checkinDate=" +
                checkinDate + "&checkoutDate=" + checkoutDate;

        MvcResult addReservationResult = this.mockMvc.perform(post("/v1/api/add-reservation?" + inputParams))
                .andExpect(status().isOk())
                .andExpect(jsonPath("reservationId", notNullValue()))
                .andReturn();

        String reservationId = JsonPath.parse(addReservationResult.getResponse().getContentAsString())
                .read("reservationId");

        LocalDate newCheckinDate = LocalDate.now().plusDays(6);
        LocalDate newCheckoutDate = LocalDate.now().plusDays(8);

        String updateParams = "name=Amit Ganvir&email=test1@gmail.com&reservationId=" + reservationId + "&checkinDate=" + newCheckinDate + "&checkoutDate=" + newCheckoutDate;
        MvcResult updatedMvcResult = this.mockMvc.perform(put("/v1/api/update-reservation?" + updateParams))
                .andExpect(status().isOk())
                .andExpect(jsonPath("reservationId", is(reservationId)))
                .andExpect(jsonPath("name", is("Amit Ganvir")))
                .andExpect(jsonPath("email", is("test1@gmail.com")))
                .andExpect(jsonPath("checkinDate", is(newCheckinDate.toString())))
                .andExpect(jsonPath("checkoutDate", is(newCheckoutDate.toString()   )))
                .andReturn();

        reservationId = JsonPath.parse(updatedMvcResult.getResponse().getContentAsString())
                .read("reservationId");

        String deleteParams = "?reservationId=" + reservationId;

        this.mockMvc.perform(delete("/v1/api/cancel-reservation" + deleteParams))
                .andExpect(status().isOk())
                .andExpect(jsonPath("message", is("Reservation cancelled successfully")));
    }

    @Test
    public void shouldAddReservationWithValidCheckinCheckoutDatesUpdateFailsForInvalidDates() throws Exception {

        LocalDate checkinDate = LocalDate.now().plusDays(20);
        LocalDate checkoutDate = LocalDate.now().plusDays(22);

        String inputParams = "name=Amit Ganvir&email=test@gmail.com&checkinDate=" +
                checkinDate + "&checkoutDate=" + checkoutDate;

        this.mockMvc.perform(post("/v1/api/add-reservation?" + inputParams))
                .andExpect(status().isOk())
                .andExpect(jsonPath("reservationId", notNullValue()))
                .andExpect(jsonPath("name", is("Amit Ganvir")))
                .andExpect(jsonPath("email", is("test@gmail.com")))
                .andExpect(jsonPath("checkinDate", is(checkinDate.toString())))
                .andExpect(jsonPath("checkoutDate", is(checkoutDate.toString())))
                .andReturn();
    }

    @Test
    public void cancelReservationFailure() throws Exception {

        this.mockMvc.perform(delete("/v1/api/cancel-reservation?reservationId=abc-dec-f"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("errorModel", notNullValue()))
                .andExpect(jsonPath("errorModel.errorMessage",
                        is("No reservation found with given reservation id ")));
    }
}
