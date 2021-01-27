package com.denemeler.redishash;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

@RestController
public class UserController {


	@Autowired
	UserRepository userRepository;

	private static final String template = "Hello, %s!";
	private final AtomicLong counter = new AtomicLong();

	@GetMapping("/users")
	public Map<String, User> getAll() {
		return userRepository.getAll();

	}

	@GetMapping("/users/{userId}")
	public User getUsers(@PathVariable String userId) {
		return userRepository.get(userId);

	}

	@PutMapping("/userCreate")
	User createUser(@RequestBody User user) {
		userRepository.create(user);
		return user;
	}

	@DeleteMapping("/users/{userId}")
	void deleteUser(@PathVariable String userId) {
		userRepository.delete(userId);
	}
}
