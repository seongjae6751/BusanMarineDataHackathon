package com.example.dtp.repository;

import java.util.Optional;

import org.springframework.data.repository.Repository;

import com.example.dtp.model.User;

public interface UserRepository extends Repository <User, Integer>{
    Optional<User> findByUuid(String s);

    User save(User user);
}
