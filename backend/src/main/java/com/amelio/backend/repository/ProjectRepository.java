package com.amelio.backend.repository;

import com.amelio.backend.models.Project;
import com.amelio.backend.models.Task;
import com.amelio.backend.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {
    List<Project> findAllByOwner(User user);
    List<Project> findAllByManagersOrWorkers(User user, User user2);

    Optional<Project> findByTasks(Task task);
}
