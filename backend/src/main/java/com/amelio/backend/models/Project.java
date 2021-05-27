package com.amelio.backend.models;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(	name = "projects")
public class Project {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(max = 30)
    private String name;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "worker_project",
            joinColumns = { @JoinColumn(name = "project_id") },
            inverseJoinColumns = { @JoinColumn(name = "user_id") }
    )
    private Set<User> workers = new HashSet<>();

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "invite_project",
            joinColumns = { @JoinColumn(name = "project_id") },
            inverseJoinColumns = { @JoinColumn(name = "user_id") }
    )
    private Set<User> invitations = new HashSet<>();

    @OneToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "task_project",
            joinColumns = { @JoinColumn(name = "project_id") },
            inverseJoinColumns = { @JoinColumn(name = "task_id") }
    )
    private Set<Task> tasks = new HashSet<>();

    public Project() {
    }

    public Project(@NotBlank @Size(max = 30) String name, Set<User> workers, Set<User> invitations, Set<Task> tasks) {
        this.name = name;
        this.workers = workers;
        this.invitations = invitations;
        this.tasks = tasks;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<User> getWorkers() {
        return workers;
    }

    public void setWorkers(Set<User> workers) {
        this.workers = workers;
    }

    public void addWorkers(User worker){
        this.workers.add(worker);
    }
    public void addWorkers(Set<User> workers){
        this.workers.addAll(workers);
    }

    public Set<User> getInvitations() {
        return invitations;
    }

    public void setInvitations(Set<User> invitations) {
        this.invitations = invitations;
    }

    public Set<Task> getTasks() {
        return tasks;
    }

    public void setTasks(Set<Task> tasks) {
        this.tasks = tasks;
    }

    public void delTask(Task task){
        this.tasks.remove(task);
    }
}
