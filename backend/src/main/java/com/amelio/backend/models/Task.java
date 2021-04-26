package com.amelio.backend.models;

import org.springframework.beans.factory.annotation.Value;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.Date;
import java.util.List;

@Entity
@Table(	name = "user_tasks")
@NamedQuery(name = "Task.findAllUndoneChildren",
		query = "select t1 from Task t1 where not exists (select t2 from Task t2 where t2.parent = t1.id) and t1.username = ?1 and t1.taskComplete = false")


public class Task {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@NotBlank
	@Size(max = 100)
	private String task_name;

	@Size(max = 700)
	private String task_description;

	private String username;

	private Long parent;

	private Date task_start;

	private Date task_end;

	private Date work_start;

	private int task_work;

	private boolean taskComplete;

	public Task() {
	}

	public Task(@NotBlank @Size(max = 100) String task_name, @Size(max = 700) String task_description, String username, Long parent, Date task_start, Date task_end, int task_work) {
		this.task_name = task_name;
		this.task_description = task_description;
		this.username = username;
		this.parent = parent;
		this.task_start = task_start;
		this.task_end = task_end;
		this.task_work = task_work;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getTask_name() {
		return task_name;
	}

	public void setTask_name(String task_name) {
		this.task_name = task_name;
	}

	public String getTask_description() {
		return task_description;
	}

	public void setTask_description(String task_description) {
		this.task_description = task_description;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public Date getTask_start() {
		return task_start;
	}

	public void setTask_start(Date task_start) {
		this.task_start = task_start;
	}

	public Date getTask_end() {
		return task_end;
	}

	public void setTask_end(Date task_end) {
		this.task_end = task_end;
	}

	public int getTask_work() {
		return task_work;
	}

	public void setTask_work(int task_work) {
		this.task_work = task_work;
	}

	public Long getParent() {
		return parent;
	}

	public void setParent(Long parent) {
		this.parent = parent;
	}

	public Date getWork_start() {
		return work_start;
	}

	public void setWork_start(Date work_start) {
		this.work_start = work_start;
	}

	public boolean getTaskComplete() {
		return taskComplete;
	}

	public void setTaskComplete(boolean taskComplete) {
		this.taskComplete = taskComplete;
	}
}
