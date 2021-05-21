package com.amelio.backend.controllers;

import com.amelio.backend.models.Project;
import com.amelio.backend.models.User;
import com.amelio.backend.repository.ProjectRepository;
import com.amelio.backend.repository.TaskRepository;
import com.amelio.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/users")
public class UserController {
	@Autowired
	UserRepository userRepo;



	@GetMapping(params = "email")
	public List<User> getAllByEmail(@RequestParam("email") String email){
		return userRepo.findAllByEmailStartingWith(email);
	}

	@GetMapping("{id}")
	public User getOne(@PathVariable("id") User user) {
		return user;
	}

	@DeleteMapping("{id}")
	public void delete(@PathVariable("id") User user) {
		userRepo.delete(user);
	}
}
