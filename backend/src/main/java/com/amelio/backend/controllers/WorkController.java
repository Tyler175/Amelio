package com.amelio.backend.controllers;

import com.amelio.backend.models.Plan;
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
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

	@GetMapping("/{user_id}/{task_id}")
	public Work getCurrentWork(@PathVariable("user_id") User user, @PathVariable("task_id") Task task){

		return workRepo.findByUserAndTaskAndWorkEndIsNull(user, task).orElse(null);
	}

	@PostMapping
	@PreAuthorize("hasRole('USER') or hasRole('MODERATOR') or hasRole('ADMIN')")
	public Work addWork(Authentication authentication, @RequestBody Work work) {
		work.setUser(userRepo.findByUsername(authentication.getName()).orElse(new User()));

		return workRepo.save(work);
	}

	@PutMapping
	public Work setEndTime(
			Authentication authentication,
			@RequestBody Work work
	) {
		work.setUser(userRepo.findByUsername(authentication.getName()).orElse(new User()));
		Work workFromDb = workRepo.findByUserAndTaskAndWorkEndIsNull(work.getUser(), work.getTask()).orElse(new Work());
		BeanUtils.copyProperties(work, workFromDb, "id");
		return workRepo.save(workFromDb);
	}

}
