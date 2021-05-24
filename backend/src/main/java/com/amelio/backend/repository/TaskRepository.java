package com.amelio.backend.repository;

import com.amelio.backend.models.Task;
import com.amelio.backend.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Optional;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
  //Optional<Task> findById(Long id);
  List<Task> findAllByParent(Task parent);
  List<Task> findAllByWorkers(User user);
  List<Task> findAllByWorkersAndTaskNameStartingWith(User user, String task_name);
  List<Task> findAllByWorkersAndTaskCompleteIsFalse(User user);
  List<Task> findAllByParentAndTaskCompleteFalse(Long parent);
}
