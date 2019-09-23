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
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class CampsiteReservationsConcurrencyTest {

    @Autowired
    private MockMvc mockMvc;

    private ConcurrentHashMap<String, String> successfulBookings = new ConcurrentHashMap<>();
    private ConcurrentHashMap<String, String> failureBookings = new ConcurrentHashMap<>();


    public void reserveCampsite(String bookCampsiteRequest) throws Exception {

        MvcResult addReservationResult = this.mockMvc
                .perform(post("/v1/api/add-reservation?" + bookCampsiteRequest))
                .andExpect(status().isOk())
                .andReturn();

        String reservationId = JsonPath.parse(addReservationResult.getResponse().getContentAsString())
                .read("reservationId");


        if (reservationId == null) {
            String errorMessage = JsonPath.parse(addReservationResult.getResponse().getContentAsString())
                    .read("errorModel.errorMessage");
            failureBookings.put(bookCampsiteRequest, errorMessage);
            return;
        }


        if ( !StringUtils.isEmpty(reservationId)) {
            System.out.println("~~~~~~~~~~~~~~~ ReservationId : " + reservationId + " " + bookCampsiteRequest.toString());
            String checkinDate = JsonPath.parse(addReservationResult.getResponse().getContentAsString())
                    .read("checkinDate");
            String checkoutDate = JsonPath.parse(addReservationResult.getResponse().getContentAsString())
                    .read("checkoutDate");

            successfulBookings.put(bookCampsiteRequest, "checkinDate="+ checkinDate + " - checkoutDate=" + checkoutDate);
        } else {
            System.out.println("Empty Reservation id for : " + bookCampsiteRequest);
        }
    }

    @Test
    public void concurrentTests() throws Exception {

        List<String> requestList = getCreateRequests(0);
        requestList.addAll(getCreateRequests(20));
        requestList.addAll(getCreateRequests(40));
        requestList.addAll(getCreateRequests(60));

        final ExecutorService executorService = Executors.newFixedThreadPool(40);

        for (String request : requestList) {

            executorService.submit(() -> {

                int tryCount = 0;
                boolean success = false;

                while (!success && tryCount < 5) {
                    try {
                        reserveCampsite(request);
                        success = true;
                    } catch (Exception e) {
                        /*
                            Failed sporadically even before putting request to controller.
                            This was because of ArrayIndexOutOfBound Exception in spring framework.
                            Added retry to cover it.
                         */
                        e.printStackTrace();
                        tryCount++;
                    }
                }
            });
        }

        executorService.awaitTermination(10, TimeUnit.SECONDS);

        this.mockMvc
                .perform(post("/v1/api/get-all-reservations"));

        assertEquals(10, successfulBookings.size());
        assertEquals(30, failureBookings.size());
    }


    private List<String> getCreateRequests(int count) {

        List<String> requests = new ArrayList<>();
        LocalDate checkinDate = LocalDate.now().plusDays(1);
        LocalDate currentCheckoutDate = checkinDate;
        LocalDate checkoutDate = LocalDate.now().plusDays(31);

        while (checkinDate.isBefore(checkoutDate) && currentCheckoutDate.isBefore(checkoutDate)) {

            currentCheckoutDate = checkinDate.plusDays(2);
            requests.add(getInputMessage(count, checkinDate.toString(), currentCheckoutDate.toString()));
            checkinDate = currentCheckoutDate.plusDays(1);
            count++;
        }

        return requests;
    }

    private String getInputMessage(int count, String checkinDate, String checkoutDate) {
        String params = "name=inputLastName&email=inputEmail&checkinDate=inputCheckinDate" +
                "&checkoutDate=inputCheckoutDate";

        String name = "Amit Ganvir" + count;
        String email = count+ "test@test.com";

        return params.replace("inputName", name)
                .replace("inputEmail", email)
                .replace("inputCheckinDate", checkinDate)
                .replace("inputCheckoutDate", checkoutDate);
    }
}