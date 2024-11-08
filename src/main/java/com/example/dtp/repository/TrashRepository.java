package com.example.dtp.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.repository.Repository;

import com.example.dtp.model.Trash;

public interface TrashRepository extends Repository<Trash, Integer> {
    List<Trash> findAll();

    Optional<Trash> findById(Long trashId);
}
