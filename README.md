<h2>Application Overview</h2>
This is a Campsite reservation service that allows users to book campsite for following business cases:

    1.  Booking campsite for max 3 days
    2.  Booking campsite latest 24 hours before the checkin date
    3.  Booking campsite earliest 1 month before the checkin date.
    
<h4> Implementation Overview </h4>
   
    Application is developed using Spring Boot Framework with H2 in-memory database.
    
    H2 Database Tables:
        1. Reservation
            - Reservations are stored in this table.
            - Fields : Name, Email, ReservationId, List<CampsiteAvailability> (References CampsiteAvailability)
        2. CampsiteAvailability
            - CampsiteAvailablility has dates which represent campsite booking dates
            - Fields : Date, Reservation (references Reservation table)
            - A Date with valid Resrevation entry means Campsite is booked.
            
            NOTE: CampsiteAvailability is pre-populated with dates for next 31 days. 
                  This is done when the application is started and is taken care in CampsiteBookingsApplication.java
    
    Implemtation is divided into following layers:
        Controller:
            Entry point for all the rest api requests
        Service:
            Business logic to validate input request. 
            Accessing data repositories for CRUD operations
            Accessing mapper layer.
        Mapper:
            Reforms DB reponse to User response.
        Database
            Performs CRUD operations.
            
            
     Concurrency:
        Achieved concurrency requirements by using:
            - Spring frameworks @Transactional
            - Optimistic Locking using @Version
            
     Junit Testing:
        - Test cases to validate business functionalities are available in CampsiteReservationsBusinessCasesTest.java
        - Test cases to validate concurrency is present in  CampsiteReservationsConcurrencyTest.java
        
     Swagger Testing:
        - Swagger can be accessed at http://localhost:8080/swagger-ui.html
        
        
     Application Start Command:
        ./mvnw -e spring-boot::run
        
            
        
        
