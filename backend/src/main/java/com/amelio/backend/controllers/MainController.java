package com.amelio.backend.controllers;

import com.amelio.backend.models.Task;
import com.amelio.backend.models.User;
import com.amelio.backend.repository.TaskRepository;
import com.amelio.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api")
public class MainController {
	@Autowired
	UserRepository userRepo;
	@Autowired
	TaskRepository taskRepo;
	@GetMapping("/all")    //not used
	public String allAccess() {
		return "Общедоступная информация.";
	}

	@GetMapping("/today")
	@PreAuthorize("hasRole('USER') or hasRole('MODERATOR') or hasRole('ADMIN')")
	public List<Task> todayTask(Authentication authentication) {
		List<Task> tasks = taskRepo.findAllUndoneChildren(authentication.getName());
		return tasks;
	}

	@GetMapping("/user")
	@PreAuthorize("hasRole('USER') or hasRole('MODERATOR') or hasRole('ADMIN')")
	public String userStat(Authentication authentication) {
		return authentication.getName();
	}

	@GetMapping("/projects")
	@PreAuthorize("hasRole('USER') or hasRole('MODERATOR') or hasRole('ADMIN')")
	public String userProject() {
		return "Проекты.";
	}

	@GetMapping("/mod")
	@PreAuthorize("hasRole('MODERATOR')")
	public String moderatorAccess() {
		return "Панель модератора.";
	}

	@GetMapping("/admin")
	@PreAuthorize("hasRole('ADMIN')")
	public String adminAccess() {
		return "Панель администратора.";
	}
}
