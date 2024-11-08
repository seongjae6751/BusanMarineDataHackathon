package com.example.dtp.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.dtp.dto.TrashesResponse;
import com.example.dtp.service.TrashService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class TrashController {

    private final TrashService trashService;

    @GetMapping("/trash/all")
    public ResponseEntity<TrashesResponse> getAllTrash(

    ) {
        TrashesResponse response = trashService.getAllTrash();
        return ResponseEntity.ok(response);
    }

    @PostMapping("/trash/accept/trash/{trashId}")
    public ResponseEntity<Void> acceptTrash(
        @PathVariable Long trashId,
        @RequestParam String userId
    ) {
        trashService.acceptTrash(trashId, userId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/check/gps/{trashId}")
    public ResponseEntity<Boolean> logGps(
        @PathVariable Long trashId,
        @RequestParam Double latitude,
        @RequestParam Double longitude) {
        boolean isNearTrash = trashService.logGpsLocation(trashId, latitude, longitude);
        return ResponseEntity.ok(isNearTrash);
    }
}
