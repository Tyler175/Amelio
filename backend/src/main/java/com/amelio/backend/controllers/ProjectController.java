package com.amelio.backend.controllers;

import com.amelio.backend.models.Project;
import com.amelio.backend.models.Task;
import com.amelio.backend.models.User;
import com.amelio.backend.repository.ProjectRepository;
import com.amelio.backend.repository.TaskRepository;
import com.amelio.backend.repository.UserRepository;
import com.amelio.backend.repository.WorkRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/projects")
public class ProjectController {
	@Autowired
	UserRepository userRepo;
	@Autowired
	TaskRepository taskRepo;
	@Autowired
	WorkRepository workRepo;
	@Autowired
	ProjectRepository projectRepository;

	@GetMapping("/invitations")
	@PreAuthorize("hasRole('USER') or hasRole('MANAGER') or hasRole('ADMIN')")
	public Set<Project> getInvitations(Authentication authentication) {
		Set<Project> res = userRepo.findByUsername(authentication.getName()).orElse(new User()).getInvitations();
		if (res.size() <= 0) res = null;
		return res;
	}


	@GetMapping
	@PreAuthorize("hasRole('USER') or hasRole('MANAGER') or hasRole('ADMIN')")
	public List<Project> projects(Authentication authentication) {
		User user = userRepo.findByUsername(authentication.getName()).orElse(new User());
		return projectRepository.findAllByWorkers(user);
	}

	@GetMapping("{id}")
	public Project getOne(@PathVariable("id") Project project) {
		return project;
	}

	@PostMapping
	public Project create(@RequestBody Project project, Authentication authentication) {
		project.addWorkers(userRepo.findByUsername(authentication.getName()).orElse(new User()));
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
		for (Task task:	project.getTasks()) {
			workRepo.deleteAllByTask(task);
			project.delTask(task);
			taskRepo.delete(task);
		}
		projectRepository.delete(project);
	}
}
