## EventManagementSystem

### Tech Stack and Database Choice: 

For this event management system, I chose to use the following tech stack:

1. **Spring Boot:** Spring Boot provides a robust framework for building Java applications, offering features like auto-configuration and simplified dependency management. It allows for rapid development and easy integration with other Spring projects.

2. **Spring Data JPA:** Spring Data JPA simplifies the implementation of JPA-based data access layers. It reduces boilerplate code and offers repository support for querying databases.

3. **RESTful API:** RESTful architecture was chosen for its simplicity, scalability, and ease of integration with various client applications.

4. **mysql Database:** It provides fast performance and can be easily integrated with Spring Boot applications.

5. **Spring Framework:** Spring Framework provides comprehensive infrastructure support for developing Java applications. It offers features like dependency injection, aspect-oriented programming, and transaction management.



 ### Setup and Instructions to Run Code:

. Clone the repository to your local machine.
. Ensure you have JDK 8 or higher installed.
. Install Maven for dependency management.
. Configure the application.properties file with necessary database and API keys.
. Run the application: `mvn spring-boot:run`.
. Access the API endpoints using tools like Postman.

1. **Clone the Repository:**

   ```bash
   git clone <repository-url>
   cd EventManagementSystem
   ```

2. **Database Configuration:**

   - Create a MySQL database with the name `EventManagementSystem`.
   - Update the `application.properties` file with your database configuration:
   
      ```properties
      spring.application.name=EventManagementSystem
      spring.datasource.url=jdbc:mysql://localhost:3306/EventManagementSystem
      spring.datasource.username=root
      spring.datasource.password=password
      spring.jpa.hibernate.ddl-auto=update

      spring.thymeleaf.enabled=true
      spring.main.banner-mode=off

      distance.api.key=https://gg-backend-assignment.azurewebsites.net/api/Distance?code=IAKvV2EvJa6Z6dEIUqqd7yGAu7IZ8gaH-a0QO6btjRc1AzFu8Y3IcQ==&latitude1=
      weather.api.key=https://gg-backend-assignment.azurewebsites.net/api/Weather?code=KfQnTWHJbg1giyB_Q9Ih3Xu3L9QOBDTuU5zwqVikZepCAzFut3rqsg==&city=
      ```

3. **Dataset:**

   - CSV dataset containing details of various events, including event names, city names, dates, times, latitudes, and longitudes for each event.
   - [Download Dataset](https://1drv.ms/x/c/87fcbc361b2ba052/ESG54eQmsuhOm-Ww-nayks4BrPWKdbAAI1Gm7bxLZe5Ohw?e=BfO9vT)

4. **External APIs:**

   - **Weather API:** Retrieve weather conditions for an event based on its location and date.
     ```
     https://gg-backend-assignment.azurewebsites.net/api/Weather?code=Kf
     QnTWHJbg1giyB_Q9Ih3Xu3L9QOBDTuU5zwqVikZepCAzFut3rqsg==&city=Port%20R
     ebeccaberg&date=2024-03-01
     ```

   - **Distance Calculation API:** Calculate the distance between the user's location and the event location.
     ```
     distance.api.key="https://gg-backend-assignment.azurewebsites.net/api/Distance?code=IAKvV2EvJa6Z6dEIUqqd7yGAu7IZ8gaH-a0QO6btjRc1AzFu8Y3IcQ==
     &latitude1=40.7128&longitude1=-74.0060&latitude2=25.5169968004073&longitude2=-173
     .22570039222800"
     ```

---

### 1. `/events/add`

**Description:**  
This endpoint adds a new event to the system.

**Request:**
- Method: POST
- Parameters:
  - `requestbody` (required):
    ```json
    {
      "eventName": "goGreen",
      "cityName": "Hoffmanfurt",
      "date": "2024-03-05",
      "time": "8:30:00",
      "latitude": -77.19655522627876,
      "longitude": -163.49416987502906
    }
    ```

- Url: `http://localhost:8080/events/add`

**Response:**
- Status Code: 200 OK
- Content Type: application/json
- Body:
  ```json
  {
    "eventName": "goGreen",
    "cityName": "Hoffmanfurt",
    "date": "2024-03-05",
    "time": "8:30:00",
    "latitude": -77.19655522627876,
    "longitude": -163.49416987502906
  }
  ```

---

### 2. `/events/find`

**Description:**  
This endpoint retrieves a list of events based on the user's latitude, longitude, and specified date. It returns events occurring within the next 14 days from the specified date.

**Request:**
- Method: GET
- Parameters:
  - `latitude` (required): Latitude of the user's location.
  - `longitude` (required): Longitude of the user's location.
  - `date` (required): Date in the format 'YYYY-MM-DD' specifying the reference date.
  - `page` (optional, default: 1): Page number for pagination.
  - `size` (optional, default: 10): Number of items per page.

- Url: `http://localhost:8080/events/find`

**Response:**
- Status Code: 200 OK
- Content Type: application/json
- Body:
  ```json
  {
    "all_events": [
      {
        "eventName": "String",
        "cityName": "String",
        "date": "String (YYYY-MM-DD)",
        "weather": "String",
        "distance": "Double"
      },
      // More event objects...
    ],
    "pageable": {
      "pageNumber": "Integer",
      "pageSize": "Integer",
      "sort": {
        "empty": "Boolean",
        "sorted": "Boolean",
        "unsorted": "Boolean"
      },
      "offset": "Integer",
      "paged": "Boolean",
      "unpaged": "Boolean"
    }
  }
  ```


### code flow


1. **Starting Point:**
   - The application starts with the `EventManagementSystemApplication` class, which is the entry point of the Spring Boot application.
   - It uses the `@SpringBootApplication` annotation to enable auto-configuration and component scanning.
  ![Alt text](![Screenshot 2024-03-22 200415](https://github.com/vamsiannangi/EventManagementSystem/assets/117896369/e8107464-e529-4f0a-be02-2c14683736d5))

2. **Controller Layer:**
   - The `EventController` class is responsible for handling incoming HTTP requests related to events.
   - It defines endpoints for creating events (`/events/add`) and finding events (`/events/find`).
 
3. **Service Layer:**
   - The `EventService` class contains the business logic for handling events.
   - It interacts with the repository layer to perform CRUD operations on events.
   - The `findEvents` method calculates distances, retrieves weather details, and maps events to DTOs.
   - It utilizes helper methods like `fetchEvents`, `retrieveWeatherDetails`, and `calculateDistances` for specific tasks.

4. **Repository Layer:**
   - The `EventRepository` interface extends `JpaRepository` to leverage Spring Data JPA functionality.
   - It provides methods for querying the database, such as finding events by date.

5. **Entity Layer:**
   - The `Event` class represents the event entity and is mapped to the database table using JPA annotations.
   - It contains fields like event name, city name, date, time, latitude, longitude, weather, and distance.

6. **DTO (Data Transfer Object):**
   - The `EventDto` class is a lightweight representation of an event, used for transferring data between layers.
   - It contains fields similar to the `Event` class but only includes essential information for client consumption.

7. **Supporting Components:**
-  The application utilizes external APIs to retrieve weather information and calculate distances. 
- `RestTemplate` is employed for making HTTP requests to external services. 
- The `getNext14Days` method in `EventService` generates a list of dates for the next 14 days. 
- It's considered good practice to store external APIs and secret links in the application.properties file. 
- To integrate the Distance Calculation API, you'll need to inject the API variables into the service layer.
     **Weather API:** Retrieve weather conditions for an event based on its location and date.
     ```
     https://gg-backend-assignment.azurewebsites.net/api/Weather?code=Kf
     QnTWHJbg1giyB_Q9Ih3Xu3L9QOBDTuU5zwqVikZepCAzFut3rqsg==&city=Port%20R
     ebeccaberg&date=2024-03-01
     ```


    **Distance Calculation API:** Calculate the distance between the user's location and the event location.

  ```
  distance.api.key="https://gg-backend-assignment.azurewebsites.net/api/Distance?code=IAKvV2EvJa6Z6dEIUqqd7yGAu7IZ8gaH-a0QO6btjRc1AzFu8Y3IcQ==
  &latitude1=40.7128&longitude1=-74.0060&latitude2=25.5169968004073&longitude2=-173.22570039222800"
  ```

  In the service layer, inject the variables of APIs.

  ![Screenshot](https://github.com/vamsiannangi/EventManagementSystem/assets/117896369/c88d65a2-81d2-4a9d-b7f6-54f2d53748dc)
  
 
  String weatherUrl = weatherApiKey + event.getCityName() + "&date=" + event.getDate();
  
  String distanceUrl=distanceApiKey+  latitude + "&longitude1=" + longitude + "&latitude2=" + event.getLatitude() + "&longitude2=" + event.getLongitude();
        

8. **Configuration:**
   - The application.properties file contains configuration properties like database connection details and API keys.
   - These properties are injected into beans using Spring's `@Value` annotation.

### Design Decisions and Challenges:

1. **Controller-Service-Repository Pattern:** I structured the application using the controller-service-repository pattern, separating concerns and promoting code maintainability and scalability.

2. **Error Handling:** Exception handling was implemented to gracefully handle errors and provide meaningful responses to clients. This ensures a better user experience and helps in troubleshooting issues.

3. **External API Integration:** Integration with external APIs for fetching weather information and calculating distances posed a challenge. Proper error handling and fallback mechanisms were implemented to handle API failures gracefully.


### Code Flow Summary:
- The `EventController` receives HTTP requests and delegates tasks to the `EventService`.
- The `EventService` performs business logic, interacts with the repository, and orchestrates external API calls.
- The `EventRepository` provides database access methods for querying events.
- Entities represent data stored in the database, while DTOs facilitate data transfer between layers.
- Supporting components handle external interactions and utility functions.

