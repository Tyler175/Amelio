package com.amelio.backend.controllers;

import com.amelio.backend.models.*;
import com.amelio.backend.repository.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/users")
public class UserController {
	@Autowired
	RoleRepository roleRepo;
	@Autowired
	UserRepository userRepo;
	@Autowired
	TaskRepository taskRepo;
	@Autowired
	PlanRepository planRepo;
	@Autowired
	WorkRepository workRepo;
	@Autowired
	ProjectRepository projectRepo;


	@GetMapping(params = "password")
	public Boolean isCorrect(@RequestParam("password") String password, Authentication authentication){
		return new BCryptPasswordEncoder().matches(password, userRepo.findByUsername(authentication.getName()).orElse(new User()).getPassword());
	}

	@GetMapping(params = "email")
	public List<User> getAllByEmail(@RequestParam("email") String email){
		return userRepo.findAllByEmailStartingWithOrUsernameStartingWith(email, email);
	}

	@GetMapping("{id}")
	public User getOne(@PathVariable("id") User user) {
		return user;
	}

	@PutMapping("{id}/{password}")
	public User changePassword(@PathVariable("id") User userFromDb, @PathVariable("password") String password, @RequestBody User user){

		if (new BCryptPasswordEncoder().matches(password, userFromDb.getPassword())){
			userFromDb.setPassword(new BCryptPasswordEncoder().encode(user.getPassword()));
		}

		return userRepo.save(userFromDb);
	}

	@PutMapping("{id}")
	@PreAuthorize("hasRole('ADMIN')")
	public User changeRole(@PathVariable("id") User userFromDb, @RequestBody User user){
		/*boolean i = true; //i dont know how (or maybe dont want) to check this nice so...
		Set<Role> roles = user.getRoles();
		//it looks so awful
		for (Role role: roles) {
			if (role.getId() == 1) {
				i = false;
			}
		}
		if (i) roles.add(roleRepo.findByName(ERole.ROLE_USER).orElse(new Role("ROLE_USER")));*/

		Set<Role> roles = new HashSet<Role>();
		roles.add(roleRepo.findById(user.getRoles().iterator().next().getId()).orElse(new Role("ROLE_ADMIN")));
		if (roles.contains(roleRepo.findByName(ERole.ROLE_MANAGER).orElse(new Role("ROLE_ADMIN")))) {
			roles.add(roleRepo.findByName(ERole.ROLE_MANAGER).orElse(new Role("ROLE_MANAGER")));
		}
		if (roles.contains(roleRepo.findByName(ERole.ROLE_MANAGER).orElse(new Role("ROLE_MANAGER")))){
			roles.add(roleRepo.findByName(ERole.ROLE_USER).orElse(new Role("ROLE_USER")));
		}
		userFromDb.setRoles(roles);
		return userRepo.save(userFromDb);
	}

	@DeleteMapping("{id}")
	public void delete(@PathVariable("id") User user) {
		for (Project project: projectRepo.findAllByWorkers(user)) {
			Set<User> users = project.getWorkers();
			users.remove(user);
			project.setWorkers(users);
			users = project.getInvitations();
			users.remove(user);
			project.setInvitations(users);
			projectRepo.save(project);
		}
		System.out.println("Projects were deleted");
		for (Task task: taskRepo.findAllByWorkers(user)) {
			Set<User> users = task.getWorkers();
			users.remove(user);
			task.setWorkers(users);
			if(users.size() == 0 && !projectRepo.findByTasks(task).isPresent()) taskRepo.delete(task);
			else taskRepo.save(task);
		}
		System.out.println("Tasks were deleted");
		planRepo.deleteAllByUser(user);
		System.out.println("Plans were deleted");
		//workRepo.deleteAllByUser(user);//it will disappear from statistic, maybe you should add isDel=true instead of deleting user from db
		System.out.println("Works weren't deleted");
		userRepo.delete(user);
	}
}
