<h1>Campsite Reservations </h1>

Swagger UI Link: http://localhost:8080/swagger-ui.html

Junit Test Cases <br>

1) CampsiteReservationsConcurrencyTest.java
    - Concurrency test case. Multiple users are hitting the controller methods for add reservation.
    - 40 users try to book campsite for 3 days in the coming month. Only 10 users (3 days per user) 
    should be able to book the campsite and the remaining users should get a proper
    error message.
    
2) CampsiteReservationsApplicationSuite2Tests.java 
    - Test cases to validate add, edit and delete reservations. 

App Start Command:  ./mvnw -e spring-boot::run