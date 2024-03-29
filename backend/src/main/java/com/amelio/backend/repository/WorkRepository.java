package com.amelio.backend.repository;

import com.amelio.backend.models.Task;
import com.amelio.backend.models.User;
import com.amelio.backend.models.Work;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface WorkRepository extends JpaRepository<Work, Long> {
  List<Work> findAllByTask(Task task);
  List<Work> findAllByUserAndWorkEndIsNotNull(User user);
  List<Work> findAllByTaskIn(Set<Task> tasks);
  Optional<Work> findByUserAndTaskAndWorkEndIsNull(User user, Task task);

  @Transactional
  void deleteAllByTask (Task task);
  @Transactional
  void deleteAllByUser (User user);

}
