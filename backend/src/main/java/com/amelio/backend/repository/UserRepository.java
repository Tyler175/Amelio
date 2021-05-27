package com.amelio.backend.repository;

import com.amelio.backend.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.persistence.NamedNativeQueries;
import javax.persistence.NamedNativeQuery;
import javax.persistence.NamedQuery;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
  @Query(value = "SELECT COUNT(u) FROM User u")
  int isTableEmpty();

  Optional<User> findByUsername(String username);
  List<User> findAllByEmailStartingWith(String email);
  List<User> findAllByEmailStartingWithOrUsernameStartingWith (String email, String username);
  Boolean existsByUsername(String username);

  Boolean existsByEmail(String email);
}
