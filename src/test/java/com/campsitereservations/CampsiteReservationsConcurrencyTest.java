package com.campsitereservations;

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
import java.util.concurrent.*;

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

        String message = JsonPath.parse(addReservationResult.getResponse().getContentAsString()).read("message");
        System.out.println(message);

        if (message.contains("Campsite booking failed, Please check error details")) {
            String errorMessage = JsonPath.parse(addReservationResult.getResponse().getContentAsString())
                    .read("errorDetails.errorMessage");
            failureBookings.put(bookCampsiteRequest, errorMessage);
            return;
        }

        String reservationId = JsonPath.parse(addReservationResult.getResponse().getContentAsString())
                .read("reservationModel.reservationId");

        if ( !StringUtils.isEmpty(reservationId)) {
            String checkinDate = JsonPath.parse(addReservationResult.getResponse().getContentAsString())
                    .read("reservationModel.checkinDate");
            String checkoutDate = JsonPath.parse(addReservationResult.getResponse().getContentAsString())
                    .read("reservationModel.checkoutDate");

            if (successfulBookings.containsKey(bookCampsiteRequest)) {
                System.out.println("&&&&&&&&&&&&&&&&&&&&&&&& CONCURRENCY ISSUE &&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&");

            }

            successfulBookings.put(bookCampsiteRequest, "checkinDate="+ checkinDate + " - checkoutDate=" + checkoutDate);
        }
    }

    @Test
    public void concurrentTests() throws Exception {

        /*
        Here the count variable is used to create unique user information like
         - first name
         - last name
         - email address
         */
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
                        System.out.println("Retrying request .....");
                        e.printStackTrace();
                        tryCount++;
                    }
                }
            });
        }

        executorService.awaitTermination(10, TimeUnit.SECONDS);

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
        String params = "firstName=inputName&lastName=inputLastName&email=inputEmail&checkinDate=inputCheckinDate" +
                "&checkoutDate=inputCheckoutDate";

        String name = "Amit" + count;
        String lastName = "Ganvir" + count;
        String email = count+ "test@test.com";

        return params.replace("inputName", name)
                .replace("inputLastName", lastName)
                .replace("inputEmail", email)
                .replace("inputCheckinDate", checkinDate)
                .replace("inputCheckoutDate", checkoutDate);
    }
}
