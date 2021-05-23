package com.amelio.backend.repository;

import com.amelio.backend.models.Task;
import com.amelio.backend.models.User;
import com.amelio.backend.models.Work;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WorkRepository extends JpaRepository<Work, Long> {
  List<Work> findAllByTask(Task task);
  Optional<Work> findByUserAndTaskAndWorkEndIsNull(User user, Task task);

}
