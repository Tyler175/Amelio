package com.amelio.backend.repository;

import java.util.Optional;
import java.util.Set;

import com.amelio.backend.models.ERole;
import com.amelio.backend.models.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
  Optional<Role> findByName(ERole name);
  Optional<Role> findById(Integer id);
  boolean existsByName(ERole name);
}
