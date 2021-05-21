package com.amelio.backend.controllers;

import com.amelio.backend.models.Project;
import com.amelio.backend.models.Task;
import com.amelio.backend.models.User;
import com.amelio.backend.repository.ProjectRepository;
import com.amelio.backend.repository.TaskRepository;
import com.amelio.backend.repository.UserRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/projects")
public class ProjectController {
	@Autowired
	UserRepository userRepo;
	@Autowired
	TaskRepository taskRepo;
	@Autowired
	ProjectRepository projectRepository;

	@GetMapping("/userProjects")
	@PreAuthorize("hasRole('USER') or hasRole('MODERATOR') or hasRole('ADMIN')")
	public List<Project> userProjects(Authentication authentication) {
		return projectRepository.findAllByOwner(userRepo.findByUsername(authentication.getName()).orElse(new User()));
	}
	@GetMapping
	@PreAuthorize("hasRole('USER') or hasRole('MODERATOR') or hasRole('ADMIN')")
	public List<Project> projects(Authentication authentication) {
		User user = userRepo.findByUsername(authentication.getName()).orElse(new User());
		return projectRepository.findAllByManagersOrWorkers(user, user);
	}

	@GetMapping("{id}")
	public Project getOne(@PathVariable("id") Project project) {
		return project;
	}

	@PostMapping
	public Project create(@RequestBody Project project, Authentication authentication) {
		project.setOwner(userRepo.findByUsername(authentication.getName()).orElse(new User()));
		return projectRepository.save(project);
	}

	@PutMapping("{id}")
	public Project update(
			@PathVariable("id") Project projectFromDb,
			@RequestBody Project project
	) {
		projectFromDb.setName(project.getName());
		return projectRepository.save(projectFromDb);
	}

	@PutMapping("/invitation/{id}")
	public Project invUpdate(
			@PathVariable("id") Project projectFromDb,
			@RequestBody Project project
	) {
		boolean ok = true;
		for (User worker : projectFromDb.getWorkers())
		{
			for (User user : project.getInvitations()){
				if(worker.equals(user)) {
					ok = false;
					break;
				}
			}
		}
		for (User manager : projectFromDb.getManagers())
		{
			for (User user : project.getInvitations()){
				if(manager.equals(user)) {
					ok = false;
					break;
				}
			}
		}
		if (ok){
			projectFromDb.setInvitations(project.getInvitations());
		}
		return projectRepository.save(projectFromDb);
	}
	@PutMapping("/workers/{id}")
	public Project usersUpdate(
			@PathVariable("id") Project projectFromDb,
			@RequestBody Project project
	) {
		projectFromDb.setWorkers(project.getWorkers());
		projectFromDb.setManagers(project.getManagers());
		return projectRepository.save(projectFromDb);
	}

	@PutMapping("/tasks/{id}")
	public Project tasksUpdate(
			@PathVariable("id") Project projectFromDb,
			@RequestBody Project project
	) {
		projectFromDb.setTasks(project.getTasks());
		return projectRepository.save(projectFromDb);
	}

	@DeleteMapping("{id}")
	public void delete(@PathVariable("id") Project project) {
		projectRepository.delete(project);
	}
}
