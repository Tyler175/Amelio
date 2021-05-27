package com.amelio.backend.repository;

import com.amelio.backend.models.Plan;
import com.amelio.backend.models.Task;
import com.amelio.backend.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Repository
public interface PlanRepository extends JpaRepository<Plan, Long> {
    boolean existsByUserAndTask(User user, Task task);

    List<Plan> findAllByTaskIsNull();

    @Transactional
    void deleteAllByTaskIsNull();
    void deleteByUserAndTask (User user, Task task);
    @Transactional
    void deleteAllByUser (User user);
    Optional<Plan> findByUserAndTask(User user, Task task);
}
