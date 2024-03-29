package com.amelio.backend.models;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(	name = "works")
public class Work {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Date workStart;

    private Date workEnd;

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
    public Work(Date workStart, Date workEnd, Task task, User user) {
        this.workStart = workStart;
        this.workEnd = workEnd;
        this.task = task;
        this.user = user;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getWorkStart() {
        return workStart;
    }

    public void setWorkStart(Date work_start) {
        this.workStart = work_start;
    }

    public Date getWorkEnd() {
        return workEnd;
    }

    public void setWorkEnd(Date work_end) {
        this.workEnd = work_end;
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
