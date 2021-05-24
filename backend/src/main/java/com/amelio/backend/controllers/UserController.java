package com.amelio.backend.controllers;

import com.amelio.backend.models.Project;
import com.amelio.backend.models.User;
import com.amelio.backend.repository.ProjectRepository;
import com.amelio.backend.repository.TaskRepository;
import com.amelio.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/users")
public class UserController {
	@Autowired
	UserRepository userRepo;

	@GetMapping(params = "password")
	public Boolean isCorrect(@RequestParam("password") String password, Authentication authentication){
		return new BCryptPasswordEncoder().matches(password, userRepo.findByUsername(authentication.getName()).orElse(new User()).getPassword());
	}

	@GetMapping(params = "email")
	public List<User> getAllByEmail(@RequestParam("email") String email){
		return userRepo.findAllByEmailStartingWith(email);
	}

	@GetMapping("{id}")
	public User getOne(@PathVariable("id") User user) {
		return user;
	}

	@PutMapping//doesnt work
	public User changePassword(@PathVariable("id") User userFromDb, @RequestBody User user, Authentication authentication){
		userFromDb.setPassword(new BCryptPasswordEncoder().encode(user.getPassword()));
		return userRepo.save(userFromDb);
	}

	@DeleteMapping("{id}")
	public void delete(@PathVariable("id") User user) {
		userRepo.delete(user);
	}
}
