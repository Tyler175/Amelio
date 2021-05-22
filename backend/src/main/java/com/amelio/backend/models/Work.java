package com.amelio.backend.models;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(	name = "works")
public class Work {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Date work_start;

    private Date work_end;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinTable(
            name = "task_works",
            joinColumns = { @JoinColumn(name = "work_id") },
            inverseJoinColumns = { @JoinColumn(name = "task_id") }
    )
    private Task task;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinTable(
            name = "user_works",
            joinColumns = { @JoinColumn(name = "work_id") },
            inverseJoinColumns = { @JoinColumn(name = "user_id") }
    )
    private User user;

    public Work(){

    }
    public Work(Date work_start, Date work_end, Task task, User user) {
        this.work_start = work_start;
        this.work_end = work_end;
        this.task = task;
        this.user = user;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getWork_start() {
        return work_start;
    }

    public void setWork_start(Date work_start) {
        this.work_start = work_start;
    }

    public Date getWork_end() {
        return work_end;
    }

    public void setWork_end(Date work_end) {
        this.work_end = work_end;
    }

    public Task getTask() {
        return task;
    }

    public void setTask(Task task) {
        this.task = task;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
