package com.amelio.backend.controllers;

import com.amelio.backend.models.Task;
import com.amelio.backend.models.User;
import com.amelio.backend.models.Work;
import com.amelio.backend.repository.TaskRepository;
import com.amelio.backend.repository.UserRepository;
import com.amelio.backend.repository.WorkRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/works")
public class WorkController {
	@Autowired
	UserRepository userRepo;
	@Autowired
	TaskRepository taskRepo;
	@Autowired
	WorkRepository workRepo;



	@GetMapping//not used
	@PreAuthorize("hasRole('USER') or hasRole('MODERATOR') or hasRole('ADMIN')")
	public List<Task> todayTask(Authentication authentication) {
		return taskRepo.findAllByWorkersAndTaskCompleteIsFalse(userRepo.findByUsername(authentication.getName()).orElse(new User()));
	}

	@PostMapping
	public Work addWork(@RequestBody Work work) {
		return workRepo.save(work);
	}

}
