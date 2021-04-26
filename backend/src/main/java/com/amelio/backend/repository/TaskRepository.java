package com.amelio.backend.repository;

import com.amelio.backend.models.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
  Optional<Task> findById(String id);
  List<Task> findAllByUsername(String username);
  List<Task> findAllByParent(Long parent);
  List<Task> findAllByUsernameAndParentIsNullAndTaskCompleteFalse(String username);
  List<Task> findAllByParentAndTaskCompleteFalse(Long parent);
  List<Task> findAllUndoneChildren(String username);
}
