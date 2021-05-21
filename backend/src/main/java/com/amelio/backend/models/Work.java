package com.amelio.backend.models;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(	name = "works")
public class Work {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Date date;

    private Date start;

    private Date end;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinTable(
            name = "task_works",
            joinColumns = { @JoinColumn(name = "work_id") },
            inverseJoinColumns = { @JoinColumn(name = "task_id") }
    )
    private Task task;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinTable(
            name = "user_works",
            joinColumns = { @JoinColumn(name = "work_id") },
            inverseJoinColumns = { @JoinColumn(name = "user_id") }
    )
    private User user;

    public Work(){

    }
    public Work(Date date, Date start, Date end, Task task, User user) {
        this.date = date;
        this.start = start;
        this.end = end;
        this.task = task;
        this.user = user;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Date getStart() {
        return start;
    }

    public void setStart(Date start) {
        this.start = start;
    }

    public Date getEnd() {
        return end;
    }

    public void setEnd(Date end) {
        this.end = end;
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
