package com.amelio.backend.controllers;

import com.amelio.backend.models.*;
import com.amelio.backend.repository.TaskRepository;
import com.amelio.backend.repository.UserRepository;
import com.amelio.backend.repository.WorkRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@PreAuthorize("hasRole('USER') or hasRole('MANAGER') or hasRole('ADMIN')")
@RequestMapping("/api/works")
public class WorkController {
	@Autowired
	UserRepository userRepo;
	@Autowired
	TaskRepository taskRepo;
	@Autowired
	WorkRepository workRepo;

	@GetMapping //not used
	public List<Task> todayTask(Authentication authentication) {
		return taskRepo.findAllByWorkersAndTaskCompleteIsFalse(userRepo.findByUsername(authentication.getName()).orElse(new User()));
	}
	@GetMapping(params = "user_id")
	public List<Work> getByUser(@RequestParam("user_id") User user) {
		return workRepo.findAllByUserAndWorkEndIsNotNull(user);
	}
	@GetMapping(params = "project_id")
	public List<Work> getByUser(@RequestParam("project_id") Project project) {
		List<Work> test = workRepo.findAllByTaskIn(project.getTasks());
		return workRepo.findAllByTaskIn(project.getTasks());
	}
	@GetMapping("/{user_id}/{task_id}")
	public Work getCurrentWork(@PathVariable("user_id") User user, @PathVariable("task_id") Task task){

		return workRepo.findByUserAndTaskAndWorkEndIsNull(user, task).orElse(null);
	}

	@PostMapping
	public Work addWork(Authentication authentication, @RequestBody Work work) {
		work.setUser(userRepo.findByUsername(authentication.getName()).orElse(new User()));
		if (work.getWorkEnd() != null){
			List<Work> works = workRepo.findAllByUserAndWorkEndIsNotNull(work.getUser());
			for (Work checkWork : works) {
				if(checkWork.getWorkEnd().compareTo(work.getWorkStart()) > 0 && checkWork.getWorkStart().compareTo(work.getWorkEnd()) < 0){
					return null;
				}
			}
		}
		return workRepo.save(work);
	}
	@PutMapping("{id}")
	public Work update(@PathVariable("id") Work workFromDb, @RequestBody Work work, Authentication authentication){
		work.setUser(userRepo.findByUsername(authentication.getName()).orElse(new User()));
		if (work.getWorkEnd() != null){
			List<Work> works = workRepo.findAllByUserAndWorkEndIsNotNull(work.getUser());
			for (Work checkWork : works) {
				if(!checkWork.getId().equals(workFromDb.getId()) && checkWork.getWorkEnd().compareTo(work.getWorkStart()) > 0 && checkWork.getWorkStart().compareTo(work.getWorkEnd()) < 0){
					return null;
				}
			}
		}
		BeanUtils.copyProperties(work, workFromDb, "id");
		return workRepo.save(workFromDb);
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

	@DeleteMapping("{id}")
	public void deleteWork(@PathVariable("id") Work work) {

		workRepo.delete(work);
	}
}
