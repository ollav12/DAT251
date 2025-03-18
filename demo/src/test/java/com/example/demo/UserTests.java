package com.example.demo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Map;

import com.example.demo.model.Trip;
import com.example.demo.model.User;
import com.example.demo.repository.TripRepository;
import com.example.demo.repository.UserRepository;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.http.HttpMethod;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.TestPropertySource;

import java.util.List;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(properties = {
                "spring.datasource.url=jdbc:h2:mem:testdb",
                "spring.datasource.driver-class-name=org.h2.Driver",
                "spring.datasource.username=sa",
                "spring.datasource.password=password",
                "spring.jpa.database-platform=org.hibernate.dialect.H2Dialect",
                "spring.jpa.hibernate.ddl-auto=create-drop",
})

class UserTests {

        @Autowired
        private TestRestTemplate restTemplate;

        @Autowired
        private BCryptPasswordEncoder passwordEncoder;

        @Autowired
        private UserRepository userRepository;

        @Autowired
        private TripRepository tripRepository;

        private User createTestUser() {
                User notRegisteredUser = new User();
                notRegisteredUser.setUsername("Test");
                notRegisteredUser.setPassword("password123");
                notRegisteredUser.setEmail("test@gmail.com");
                notRegisteredUser.setFirstName("MyName");
                notRegisteredUser.setLastName("MyLastName");
                notRegisteredUser.setPoints(100);
                return notRegisteredUser;
        }

        private User createTestUser2() {
                User notRegisteredUser = new User();
                notRegisteredUser.setUsername("Test2"); // new username
                notRegisteredUser.setPassword("password123");
                notRegisteredUser.setEmail("test@gmail.com");
                notRegisteredUser.setFirstName("MyName");
                notRegisteredUser.setLastName("MyLastName");
                notRegisteredUser.setPoints(200); // +100 points
                return notRegisteredUser;
        }

        @Test
        void contextLoads() {
        }

        @Test
        void testCreatingUser() throws InterruptedException {

                User notRegisteredUser = createTestUser();

                ResponseEntity<User> response = restTemplate.postForEntity(
                                "/users",
                                notRegisteredUser,
                                User.class);

                userRepository.flush();

                System.out.println("Status code: " + response.getStatusCode());
                System.out.println("Response body: " + response.getBody());

                User registeredUser = response.getBody();
                assertNotNull(registeredUser);
                assertEquals("Test", registeredUser.getUsername());
                assertEquals(HttpStatus.CREATED, response.getStatusCode());
        }

        @Test
        void testRetrievingUser() {

                User user = new User();
                user.setUsername("Test8969988");
                user.setPassword("password123");
                user.setEmail("test@gmail.com");
                user.setFirstName("MyName");
                user.setLastName("MyLastName");
                user.setPoints(100);

                userRepository.saveAndFlush(user);

                // Executes the GET getUser method
                ResponseEntity<User> getResponse = restTemplate.getForEntity(
                                "/users/" + user.getId(),
                                User.class);
                User retrievedUser = getResponse.getBody();
                assertNotNull(retrievedUser);
                assertEquals(HttpStatus.OK, getResponse.getStatusCode());
                assertEquals("Test8969988", retrievedUser.getUsername());
        }

        @Test
        void testDeletingUser() {
                User notRegisteredUser = new User();
                notRegisteredUser.setUsername("Test7653333");
                notRegisteredUser.setPassword("password1234");
                notRegisteredUser.setEmail("test@gmail.com");
                notRegisteredUser.setFirstName("MyName");
                notRegisteredUser.setLastName("MyLastName");
                notRegisteredUser.setPoints(100);

                userRepository.saveAndFlush(notRegisteredUser);

                System.out.println("userid: " + notRegisteredUser.getId());

                // Executes the DELETE deleteUser method
                restTemplate.delete("/users/" + notRegisteredUser.getId());
                ResponseEntity<User> getResponse = restTemplate.getForEntity(
                                "/users/" + notRegisteredUser.getId(),
                                User.class);
                assertEquals(HttpStatus.NOT_FOUND, getResponse.getStatusCode());
                // System.out.println("users:" + userRepository.findAll());
                // assertEquals(0, userRepository.count());

        }

        @Test
        void testUpdatingUser() {

                User notRegisteredUser = new User();
                notRegisteredUser.setUsername("Test999");
                notRegisteredUser.setPassword("password123");
                notRegisteredUser.setEmail("test@gmail.com");
                notRegisteredUser.setFirstName("MyName");
                notRegisteredUser.setLastName("MyLastName");
                notRegisteredUser.setPoints(100);

                User user2 = createTestUser2();

                userRepository.saveAndFlush(notRegisteredUser);

                /*
                 * ResponseEntity<User> response = restTemplate.postForEntity(
                 * "/users",
                 * notRegisteredUser,
                 * User.class);
                 * User registeredUser = response.getBody();
                 * assertNotNull(registeredUser);
                 * assertEquals(HttpStatus.CREATED, response.getStatusCode());
                 */

                // Executes the PUT updateUser method
                restTemplate.put("/users/" + notRegisteredUser.getId(), user2);

                userRepository.flush();

                ResponseEntity<User> getResponse = restTemplate.getForEntity(
                                "/users/" + notRegisteredUser.getId(),
                                User.class);
                assertEquals(HttpStatus.OK, getResponse.getStatusCode());
                User updatedUser = getResponse.getBody();

                assertNotNull(updatedUser);
                assertEquals("Test2", updatedUser.getUsername());
                assertTrue(
                                passwordEncoder.matches("password123", updatedUser.getPassword()));
                assertEquals("test@gmail.com", updatedUser.getEmail());
                assertEquals("MyName", updatedUser.getFirstName());
                assertEquals("MyLastName", updatedUser.getLastName());
                assertEquals(200, updatedUser.getPoints());
        }

        @Test
        void testUserRegistration() {

                User notRegisteredUser = new User();
                notRegisteredUser.setUsername("Test888");
                notRegisteredUser.setPassword("password123");
                notRegisteredUser.setEmail("test@gmail.com");
                notRegisteredUser.setFirstName("MyName");
                notRegisteredUser.setLastName("MyLastName");
                notRegisteredUser.setPoints(100);

                ResponseEntity<Map<String, String>> response = restTemplate.exchange(
                                "/auth/register",
                                HttpMethod.POST,
                                new HttpEntity<>(notRegisteredUser),
                                new ParameterizedTypeReference<Map<String, String>>() {
                                });

                assertEquals(HttpStatus.OK, response.getStatusCode());
                Map<String, String> body = response.getBody();
                assertNotNull(body);
                assertEquals("User registered successfully", body.get("message"));

        }

        @Test
        void testUserLogin() {

                User notRegisteredUser = new User();
                notRegisteredUser.setUsername("Test1111");
                notRegisteredUser.setPassword("password123");
                notRegisteredUser.setEmail("test@gmail.com");
                notRegisteredUser.setFirstName("MyName");
                notRegisteredUser.setLastName("MyLastName");
                notRegisteredUser.setPoints(100);

                restTemplate.exchange(
                                "/auth/register",
                                HttpMethod.POST,
                                new HttpEntity<>(notRegisteredUser),
                                new ParameterizedTypeReference<Map<String, String>>() {
                                });

                ResponseEntity<Map<String, String>> response = restTemplate.exchange(
                                "/auth/login",
                                HttpMethod.POST,
                                new HttpEntity<>(notRegisteredUser),
                                new ParameterizedTypeReference<Map<String, String>>() {
                                });

                assertEquals(HttpStatus.OK, response.getStatusCode());
                Map<String, String> body = response.getBody();
                assertNotNull(body);
                assertEquals("User logged in successfully", body.get("message"));

        }

        @Test
        void testNotRegisteredUserLogin() {

                User notRegisteredUser = new User();
                notRegisteredUser.setUsername("Test8887");
                notRegisteredUser.setPassword("password123");
                notRegisteredUser.setEmail("test@gmail.com");
                notRegisteredUser.setFirstName("MyName");
                notRegisteredUser.setLastName("MyLastName");
                notRegisteredUser.setPoints(100);

                ResponseEntity<Map<String, String>> response = restTemplate.exchange(
                                "/auth/login",
                                HttpMethod.POST,
                                new HttpEntity<>(notRegisteredUser),
                                new ParameterizedTypeReference<Map<String, String>>() {
                                });

                assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
                Map<String, String> body = response.getBody();
                assertNotNull(body);
                assertEquals("User not found", body.get("error"));

        }

        @Test
        void testLoginWithWrongPassword() {

                User notRegisteredUser = new User();
                notRegisteredUser.setUsername("Test3458");
                notRegisteredUser.setPassword("password123");
                notRegisteredUser.setEmail("test@gmail.com");
                notRegisteredUser.setFirstName("MyName");
                notRegisteredUser.setLastName("MyLastName");
                notRegisteredUser.setPoints(100);

                restTemplate.exchange(
                                "/auth/register",
                                HttpMethod.POST,
                                new HttpEntity<>(notRegisteredUser),
                                new ParameterizedTypeReference<Map<String, String>>() {
                                });

                notRegisteredUser.setPassword("newPassword123");

                ResponseEntity<Map<String, String>> response = restTemplate.exchange(
                                "/auth/login",
                                HttpMethod.POST,
                                new HttpEntity<>(notRegisteredUser),
                                new ParameterizedTypeReference<Map<String, String>>() {
                                });

                assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
                Map<String, String> body = response.getBody();
                assertNotNull(body);
                assertEquals("Invalid password", body.get("error"));

        }

        @Test
        void testGetUserEmission() {

                User user = new User();
                user.setUsername("Test332155");
                user.setPassword("password123");
                user.setEmail("user@gmail.com");
                user.setFirstName("MyName");
                user.setLastName("MyLastName");
                user.setPoints(100);

                Trip trip = new Trip();
                trip.setUser(user);
                trip.setOrigin("Inndalsveien, 5063 Bergen");
                trip.setDestination("Høyteknologisenteret - Universitetet i Bergen, Thormøhlens Gate 55, 5006 Bergen");
                trip.setTotalDistanceKm(300.0);
                trip.setTotalDurationSeconds(10800.0);
                trip.setTotalEmissionsCO2eKg(10.0);
                trip.setSavedEmissionsCO2eKg(0.0);

                userRepository.saveAndFlush(user);
                tripRepository.saveAndFlush(trip);

                ResponseEntity<Double> response = restTemplate.getForEntity(
                                "/users/" + user.getId() + "/emission",
                                Double.class);

                System.out.println("User ID: " + user.getId());
                System.out.println("Response status: " + response.getStatusCode());
                System.out.println("Response body: " + response.getBody());

                assertEquals(HttpStatus.OK, response.getStatusCode());

                Double userEmission = response.getBody();

                assertEquals(10.0, userEmission);

        }


        @Test
        void testDeleteUserEmission() {

                User user = new User();
                user.setUsername("Test332156");
                user.setPassword("password123");
                user.setEmail("user@gmail.com");
                user.setFirstName("MyName");
                user.setLastName("MyLastName");
                user.setPoints(100);

                Trip trip = new Trip();
                trip.setUser(user);
                trip.setOrigin("Inndalsveien, 5063 Bergen");
                trip.setDestination("Høyteknologisenteret - Universitetet i Bergen, Thormøhlens Gate 55, 5006 Bergen");
                trip.setTotalDistanceKm(300.0);
                trip.setTotalDurationSeconds(10800.0);
                trip.setTotalEmissionsCO2eKg(10.0);
                trip.setSavedEmissionsCO2eKg(0.0);



                userRepository.saveAndFlush(user);
                tripRepository.saveAndFlush(trip);

                restTemplate.delete("/users/" + user.getId() + "/emission");
                ResponseEntity<List<Trip>> getResponse = restTemplate.exchange(
                        "/users/" + user.getId() + "/trips",
                        HttpMethod.GET,
                        null,
                        new ParameterizedTypeReference<List<Trip>>() {});

                List<Trip> trips = getResponse.getBody();
                assertEquals(HttpStatus.OK, getResponse.getStatusCode());
                assertEquals(0, trips.size());
        }

        @Test
        void testUpdateUserTrip() {
                User user = new User();
                user.setUsername("Test432156");
                user.setPassword("password123");
                user.setEmail("user@gmail.com");
                user.setFirstName("MyName");
                user.setLastName("MyLastName");
                user.setPoints(100);

                Trip trip = new Trip();
                trip.setUser(user);
                trip.setOrigin("Inndalsveien, 5063 Bergen");
                trip.setDestination("Høyteknologisenteret - Universitetet i Bergen, Thormøhlens Gate 55, 5006 Bergen");
                trip.setTotalDistanceKm(300.0);
                trip.setTotalDurationSeconds(10800.0);
                trip.setTotalEmissionsCO2eKg(10.0);
                trip.setSavedEmissionsCO2eKg(0.0);

                Trip updatedTrip = new Trip();
                updatedTrip.setUser(user);
                updatedTrip.setOrigin("Inndalsveien, 5063 Bergen");
                updatedTrip.setDestination("Høyteknologisenteret - Universitetet i Bergen, Thormøhlens Gate 55, 5006 Bergen");
                updatedTrip.setTotalDistanceKm(300.0);
                updatedTrip.setTotalDurationSeconds(10800.0);
                updatedTrip.setTotalEmissionsCO2eKg(20.0); // +10 kg
                updatedTrip.setSavedEmissionsCO2eKg(0.0);

                userRepository.saveAndFlush(user);
                tripRepository.saveAndFlush(trip);

                restTemplate.put("/trips/" + trip.getId(), updatedTrip);

                ResponseEntity<Trip> getResponse = restTemplate.getForEntity(
                        "/trips/" + trip.getId(),
                        Trip.class);

                assertEquals(HttpStatus.OK, getResponse.getStatusCode());
                Trip updatedTripResponse = getResponse.getBody();
                assertNotNull(updatedTripResponse);
                assertEquals(20.0, updatedTripResponse.getTotalEmissionsCO2eKg());



        }

}
