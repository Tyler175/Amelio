package com.amelio.backend.controllers;

import com.amelio.backend.models.*;
import com.amelio.backend.repository.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    @Autowired
    UserRepository userRepo;
    @Autowired
    TaskRepository taskRepo;
    @Autowired
    ProjectRepository projectRepository;
    @Autowired
    PlanRepository planRepository;
    @Autowired
    WorkRepository workRepo;

    @PostMapping("/plan")
    public Plan createPlan(@RequestBody Plan plan) {
        planRepository.deleteAllByTaskIsNull();
        return planRepository.save(plan);
    }

    @PutMapping("/plan")
    public Plan planUpdate(
            @RequestBody Plan plan
    ) {
        Plan planFromDb = planRepository.findByUserAndTask(plan.getUser(),plan.getTask()).orElse(new Plan());
        BeanUtils.copyProperties(plan, planFromDb, "id");
        return planRepository.save(planFromDb);
    }
    @RequestMapping("/plan")
    @DeleteMapping(params = "plan")
    public void planDelete(@RequestParam("plan") Plan plan) {
        planRepository.deleteByUserAndTask(plan.getUser(),plan.getTask());
    }

    @GetMapping("/permissions/{id}")
    public boolean[] getPermission(Authentication authentication,
            @PathVariable("id") Task task) {
        boolean isOwner;
        boolean isManager = false;
        User user = userRepo.findByUsername(authentication.getName()).orElse(new User());
        if (task.getProject() != null) {
            isOwner = task.getProject().getOwner().equals(user);
            isManager = task.getProject().getManagers().contains(user);
        } else isOwner = task.getWorkers().contains(user);
        return new boolean[]{isOwner,isManager};
    }
    @GetMapping
    @PreAuthorize("hasRole('USER') or hasRole('MODERATOR') or hasRole('ADMIN')")
    public List<Task> getTasks(Authentication authentication) {
        Optional<User> user = userRepo.findByUsername(authentication.getName());
        if(user.isPresent()){
          return taskRepo.findAllByWorkers(user.orElse(new User()));
        } else return null;
    }

    @GetMapping("{id}")
    public Task getOne(@PathVariable("id") Task task) {
        return task;
    }

    @PostMapping
    public Task create(@RequestBody Task task) {
        if (task.getParent() != null && !task.getTaskComplete()){
            Task update = taskRepo.findById(task.getParent().getId()).orElse(new Task());
            update.setTaskComplete(false);
            taskRepo.save(update);
        }
        return taskRepo.save(task);
    }

    @PutMapping("{id}")
    public Task update(
            Authentication authentication,
            @PathVariable("id") Task taskFromDb,
            @RequestBody Task task
    ) {

        BeanUtils.copyProperties(task, taskFromDb, "id");
        Set<Task> childrenToDel = new HashSet<>();
        for (Task child : taskRepo.findAllByParent(taskFromDb)) {
            if (taskFromDb.getTaskComplete()) child.setTaskComplete(true);
            if (child.getTask_start().compareTo(taskFromDb.getTask_start()) < 0) child.setTask_start(taskFromDb.getTask_start());
            if (child.getTask_end().compareTo(taskFromDb.getTask_end()) > 0) child.setTask_end(taskFromDb.getTask_end());
            if (child.getTask_start().compareTo(taskFromDb.getTask_end()) > 0
                    || child.getTask_end().compareTo(taskFromDb.getTask_start()) < 0
                    || child.getTask_end().compareTo(child.getTask_start()) < 0){
                childrenToDel.add(child);
            } else taskRepo.save(child);
        }
        taskFromDb.delChild(childrenToDel);

        Optional<Work> work = workRepo.findByUserAndTaskAndWorkEndIsNull(userRepo.findByUsername(authentication.getName()).orElse(new User()), taskFromDb);
        if(work.isPresent()){
            work.orElse(new Work()).setWorkEnd(new Date());
        }

        Task saved = taskRepo.save(taskFromDb);

        taskRepo.deleteAll(childrenToDel);

        return saved;
    }

    @DeleteMapping("{id}")
    public void delete(@PathVariable("id") Task task) {
        Optional<Project> project = projectRepository.findByTasks(task);
        if(project.isPresent()){
            project.orElse(new Project()).delTask(task);
        }
        //При переносе удаления в put помни про то, что у задачи может остаться незаконченный work
        taskRepo.delete(task);
    }

}
