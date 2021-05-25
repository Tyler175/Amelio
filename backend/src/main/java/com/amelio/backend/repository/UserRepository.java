package com.amelio.backend.repository;

import java.util.List;
import java.util.Optional;

import com.amelio.backend.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
  Optional<User> findByUsername(String username);
  List<User> findAllByEmailStartingWith(String email);
  Boolean existsByUsername(String username);

  Boolean existsByEmail(String email);
}
