package org.example.rentalsytsem.repository;

import org.example.rentalsytsem.entity.user;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface userrepository extends JpaRepository<user, Long> {

    Optional<user> findByUsername(String username);

    Optional<user> findById(Long id);
}
