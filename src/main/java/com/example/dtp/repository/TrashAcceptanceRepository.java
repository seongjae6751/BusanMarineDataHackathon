package com.example.dtp.repository;

import java.util.Optional;

import org.springframework.data.repository.Repository;

import com.example.dtp.model.TrashAcceptance;

public interface TrashAcceptanceRepository extends Repository<TrashAcceptance, Integer> {
    void save(TrashAcceptance acceptance);

    Optional<TrashAcceptance> findById(Long trashAcceptanceId);
}
