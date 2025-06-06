package com.app.linkedinclone.repository;

import com.app.linkedinclone.model.dao.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Boolean existsByEmail(String email);

    Optional<User> findByEmail(String email);

    List<User> findAllByUserNameContainingIgnoreCase(String username);

    List<User> findAllByIdIn(List<Long> ids);

}
