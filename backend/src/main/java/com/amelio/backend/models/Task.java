package com.amelio.backend.models;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.Date;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(	name = "user_tasks")

public class Task {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@NotBlank
	@Size(max = 100)
	private String task_name;

	@Size(max = 700)
	private String task_description;

	private Date task_start;

	private Date task_end;

	private boolean taskComplete;

	private boolean taskDelete;

	private boolean current;

	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(
			name = "u_tasks",
			joinColumns = { @JoinColumn(name = "task_id") },
			inverseJoinColumns = { @JoinColumn(name = "user_id") }
	)
	private Set<User> workers = new HashSet<>();

	@ManyToOne
	private Task parent;

	@JsonIgnore
	@OneToMany(mappedBy="parent", cascade=CascadeType.ALL)
	private Set<Task> children = new HashSet<>();

	@JsonIgnore
	@ManyToOne()
	@JoinTable(
			name = "task_project",
			joinColumns = { @JoinColumn(name = "task_id") },
			inverseJoinColumns = { @JoinColumn(name = "project_id") }
	)
	private Project project;

	@OneToMany(cascade=CascadeType.ALL)
	@JoinTable(
			name = "task_plan",
			joinColumns = { @JoinColumn(name = "task_id") },
			inverseJoinColumns = { @JoinColumn(name = "plan_id") }
	)
	private Set<Plan> plans = new HashSet<>();

	public Task() {
	}

	public Task(@NotBlank @Size(max = 100) String task_name, @Size(max = 700) String task_description, Task parent, Set<Task> children, Date task_start, Date task_end, boolean taskComplete, boolean taskDelete, boolean current, Set<User> workers, Project project, Set<Plan> plans) {
		this.task_name = task_name;
		this.task_description = task_description;
		this.parent = parent;
		this.children = children;
		this.task_start = task_start;
		this.task_end = task_end;
		this.taskComplete = taskComplete;
		this.taskDelete = taskDelete;
		this.current = current;
		this.workers = workers;
		this.project = project;
		this.plans = plans;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Task task = (Task) o;
		return Objects.equals(id, task.id);
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

	public boolean getTaskComplete() {
		return taskComplete;
	}

	public boolean isTaskComplete() {
		return taskComplete;
	}

	public void setTaskComplete(boolean taskComplete) {
		this.taskComplete = taskComplete;
	}

	public boolean getTaskDelete() {
		return taskDelete;
	}

	public boolean isTaskDelete() {
		return taskDelete;
	}

	public void setTaskDelete(boolean taskDelete) {
		this.taskDelete = taskDelete;
	}

	public void setCurrent(boolean current) {
		this.current = current;
	}

	public boolean getCurrent() {
		return current;
	}

	public boolean isCurrent() {
		return current;
	}

	public Set<User> getWorkers() {
		return workers;
	}

	public void setWorkers(Set<User> workers) {
		this.workers = workers;
	}

	public Task getParent() {
		return parent;
	}

	public void setParent(Task parent) {
		this.parent = parent;
	}

	public Set<Task> getChildren() {
		return children;
	}

	public void setChildren(Set<Task> children) {
		this.children = children;
	}

	public Project getProject() {
		return project;
	}

	public void setProject(Project project) {
		this.project = project;
	}

	public Set<Plan> getPlans() {
		return plans;
	}

	public void setPlans(Set<Plan> plans) { this.plans = plans;}

	public void addPlan(Plan plan){	this.plans.add(plan);}

	public void removePlan(Plan plan){this.plans.remove(plan);}

	public void addWorker(User user){
		workers.add(user);
	}

	public void delChild(Task task) {this.children.remove(task);}
	public void delChild(Set<Task> task) {this.children.removeAll(task);}
}
