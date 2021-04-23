package com.amelio.backend.controllers;

import com.amelio.backend.models.Task;
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
@RequestMapping("/api/tasks")
public class TaskController {

    @Autowired
    UserRepository userRepo;
    @Autowired
    TaskRepository taskRepo;
    @GetMapping
    @PreAuthorize("hasRole('USER') or hasRole('MODERATOR') or hasRole('ADMIN')")
    public List<ChildTask> allTasks(Authentication authentication) {
        List<Task> parents = taskRepo.findAllByUsername(authentication.getName());
        List<ChildTask> allTask = new ArrayList<ChildTask>();
        for (Task parent: parents) {
            allTask.add(new ChildTask(parent,taskRepo.findAllByParent(parent.getId())));
        }

        return allTask;
    }

    @GetMapping("{id}")
    public Task getOne(@PathVariable("id") Task task) {
        return task;
    }

    @PostMapping
    public Task create(@RequestBody Task task) {
        return taskRepo.save(task);
    }

    @PutMapping("{id}")
    public Task update(
            @PathVariable("id") Task taskFromDb,
            @RequestBody Task task
    ) {
        BeanUtils.copyProperties(task, taskFromDb, "id");

        return taskRepo.save(taskFromDb);
    }

    @DeleteMapping("{id}")
    public void delete(@PathVariable("id") Task task) {
        taskRepo.delete(task);
    }

}
