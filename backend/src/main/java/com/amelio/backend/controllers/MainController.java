package com.amelio.backend.controllers;

import com.amelio.backend.models.Project;
import com.amelio.backend.models.Task;
import com.amelio.backend.models.User;
import com.amelio.backend.models.Work;
import com.amelio.backend.repository.TaskRepository;
import com.amelio.backend.repository.UserRepository;
import com.amelio.backend.repository.WorkRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.*;
import java.util.stream.Collectors;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api")
public class MainController {
	@Autowired
	UserRepository userRepo;
	@Autowired
	TaskRepository taskRepo;
	@Autowired
	WorkRepository workRepo;


	@GetMapping("/all")    //not used
	public String allAccess() {
		return "Общедоступная информация.";
	}

	@GetMapping("/today")
	@PreAuthorize("hasRole('USER') or hasRole('MODERATOR') or hasRole('ADMIN')")
	public List<Task> todayTask(Authentication authentication) {
		return taskRepo.findAllByWorkersAndTaskCompleteIsFalse(userRepo.findByUsername(authentication.getName()).orElse(new User()));
	}

	@GetMapping("/user")
	@PreAuthorize("hasRole('USER') or hasRole('MODERATOR') or hasRole('ADMIN')")
	public User getUser(Authentication authentication) {
		return userRepo.findByUsername(authentication.getName()).orElse(new User());
	}

	@GetMapping("/statistics")
	@PreAuthorize("hasRole('USER') or hasRole('MODERATOR') or hasRole('ADMIN')")
	public User stat(Authentication authentication) {
		return userRepo.findByUsername(authentication.getName()).orElse(new User());
	}

	@PutMapping("/profile/{id}")
	public User update(
			@PathVariable("id") User userFromDb,
			@RequestBody User user
	) {
		userFromDb.setDescription(user.getDescription());
		return userRepo.save(userFromDb);
	}


	@GetMapping("/mod")
	@PreAuthorize("hasRole('MODERATOR')")
	public String moderatorAccess() {
		return "Панель модератора.";
	}

	@GetMapping("/admin")
	@PreAuthorize("hasRole('ADMIN')")
	public String adminAccess() {
		Set<Work> childrenToDel = new HashSet<>();
		System.out.println("Still Working 2");
		workRepo.deleteAll(childrenToDel);
		System.out.println("Still Working 2");
		return "Панель администратора.";
	}

}
