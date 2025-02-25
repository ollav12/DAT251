package com.example.demo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.TestPropertySource;

import com.example.demo.Model.User;
import com.example.demo.Service.UserService;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(properties = {
		"spring.datasource.url=jdbc:h2:mem:testdb",
		"spring.datasource.driver-class-name=org.h2.Driver",
		"spring.datasource.username=sa",
		"spring.datasource.password=password",
		"spring.jpa.database-platform=org.hibernate.dialect.H2Dialect",
		"spring.jpa.hibernate.ddl-auto=create-drop"
})
class Dat251ApplicationTests {

	@Autowired
	private UserService userService;

	@Autowired
	private TestRestTemplate restTemplate;

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

	@Test
	void contextLoads() {
	}

	@Test
	void testRegisterUser() {
		User user = createTestUser();

		User registeredUser = userService.registerUser(user);
		assertNotNull(registeredUser);
		assertEquals("Test", registeredUser.getUsername());
	}

	@Test
	void testGetUser() {
		User user = createTestUser();

		ResponseEntity<User> response = restTemplate.postForEntity("/users", user, User.class);
		User registeredUser = response.getBody();
		assertNotNull(registeredUser);
		assertEquals("Test", registeredUser.getUsername());
		assertEquals(HttpStatus.CREATED, response.getStatusCode());

		ResponseEntity<User> getResponse = restTemplate.getForEntity("/users/" + registeredUser.getId(), User.class);
		User retrievedUser = getResponse.getBody();
		assertNotNull(retrievedUser);
		assertEquals(HttpStatus.OK, getResponse.getStatusCode());
		assertEquals("Test", retrievedUser.getUsername());
	}

}
