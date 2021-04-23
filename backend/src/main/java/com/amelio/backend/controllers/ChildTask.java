package com.amelio.backend.controllers;

import com.amelio.backend.models.Task;
import com.amelio.backend.repository.TaskRepository;

import java.util.List;

public class ChildTask {
    private Task parent;
    private List<Task> children;

    public Task getParent() {
        return parent;
    }

    public void setParent(Task parent) {
        this.parent = parent;
    }

    public List<Task> getChildren() {
        return children;
    }

    public void setChildren(List<Task> children) {
        this.children = children;
    }

    public ChildTask(Task parent, List<Task> children) {
        this.parent = parent;
        this.children = children;
    }
}
