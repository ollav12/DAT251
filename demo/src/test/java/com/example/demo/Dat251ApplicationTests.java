package com.example.demo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Map;

import com.example.demo.model.User;

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

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(properties = {
                "spring.datasource.url=jdbc:h2:mem:testdb",
                "spring.datasource.driver-class-name=org.h2.Driver",
                "spring.datasource.username=sa",
                "spring.datasource.password=password",
                "spring.jpa.database-platform=org.hibernate.dialect.H2Dialect",
                "spring.jpa.hibernate.ddl-auto=create-drop",
})
class Dat251ApplicationTests {

        @Autowired
        private TestRestTemplate restTemplate;

        @Autowired
        private BCryptPasswordEncoder passwordEncoder;

        private User createTestUser() {
                User user = new User();
                user.setUsername("Test");
                user.setPassword("password123");
                user.setEmail("test@gmail.com");
                user.setFirstName("MyName");
                user.setLastName("MyLastName");
                user.setPoints(100);
                return user;
        }

        private User createTestUser2() {
                User user = new User();
                user.setUsername("Test2"); // new username
                user.setPassword("password123");
                user.setEmail("test@gmail.com");
                user.setFirstName("MyName");
                user.setLastName("MyLastName");
                user.setPoints(200); // +100 points
                return user;
        }

        @Test
        void contextLoads() {
        }

        @Test
        void testCreatingAndRetrievingUser() {
                User user = createTestUser();

                // Executes the POST createUser method
                ResponseEntity<User> response = restTemplate.postForEntity(
                                "/users",
                                user,
                                User.class);
                User registeredUser = response.getBody();
                assertNotNull(registeredUser);
                assertEquals("Test", registeredUser.getUsername());
                assertEquals(HttpStatus.CREATED, response.getStatusCode());

                // Executes the GET getUser method
                ResponseEntity<User> getResponse = restTemplate.getForEntity(
                                "/users/" + registeredUser.getId(),
                                User.class);
                User retrievedUser = getResponse.getBody();
                assertNotNull(retrievedUser);
                assertEquals(HttpStatus.OK, getResponse.getStatusCode());
                assertEquals("Test", retrievedUser.getUsername());
        }

        @Test
        void testDeletingUser() {
                User user = createTestUser();
                ResponseEntity<User> response = restTemplate.postForEntity(
                                "/users",
                                user,
                                User.class);
                User registeredUser = response.getBody();
                assertNotNull(registeredUser);
                assertEquals(HttpStatus.CREATED, response.getStatusCode());

                // Executes the DELETE deleteUser method
                restTemplate.delete("/users/" + registeredUser.getId());
                ResponseEntity<User> getResponse = restTemplate.getForEntity(
                                "/users/" + registeredUser.getId(),
                                User.class);
                assertEquals(HttpStatus.NOT_FOUND, getResponse.getStatusCode());
        }

        @Test
        void testUpdatingUser() {
                User user = createTestUser();
                User user2 = createTestUser2();

                ResponseEntity<User> response = restTemplate.postForEntity(
                                "/users",
                                user,
                                User.class);
                User registeredUser = response.getBody();
                assertNotNull(registeredUser);
                assertEquals(HttpStatus.CREATED, response.getStatusCode());

                // Executes the PUT updateUser method
                restTemplate.put("/users/" + registeredUser.getId(), user2);

                ResponseEntity<User> getResponse = restTemplate.getForEntity(
                                "/users/" + registeredUser.getId(),
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
                User user = createTestUser();

                ResponseEntity<Map<String, String>> response = restTemplate.exchange(
                                "/auth/register",
                                HttpMethod.POST,
                                new HttpEntity<>(user),
                                new ParameterizedTypeReference<Map<String, String>>() {
                                });

                assertEquals(HttpStatus.OK, response.getStatusCode());
                Map<String, String> body = response.getBody();
                assertNotNull(body);
                assertEquals("User registered successfully", body.get("message"));

        }

        @Test
        void testUserLogin() {
                User user = createTestUser();

                restTemplate.exchange(
                                "/auth/register",
                                HttpMethod.POST,
                                new HttpEntity<>(user),
                                new ParameterizedTypeReference<Map<String, String>>() {
                                });

                ResponseEntity<Map<String, String>> response = restTemplate.exchange(
                                "/auth/login",
                                HttpMethod.POST,
                                new HttpEntity<>(user),
                                new ParameterizedTypeReference<Map<String, String>>() {
                                });

                assertEquals(HttpStatus.OK, response.getStatusCode());
                Map<String, String> body = response.getBody();
                assertNotNull(body);
                assertEquals("User logged in successfully", body.get("message"));

        }

        @Test
        void testNotRegisteredUserLogin() {
                User notRegisteredUser = createTestUser();

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
                User user = createTestUser();

                restTemplate.exchange(
                                "/auth/register",
                                HttpMethod.POST,
                                new HttpEntity<>(user),
                                new ParameterizedTypeReference<Map<String, String>>() {
                                });

                user.setPassword("newPassword123");

                ResponseEntity<Map<String, String>> response = restTemplate.exchange(
                                "/auth/login",
                                HttpMethod.POST,
                                new HttpEntity<>(user),
                                new ParameterizedTypeReference<Map<String, String>>() {
                                });

                assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
                Map<String, String> body = response.getBody();
                assertNotNull(body);
                assertEquals("Invalid password", body.get("error"));

        }

}
