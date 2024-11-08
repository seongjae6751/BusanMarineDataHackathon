package com.example.dtp.dto;

import java.time.LocalDateTime;
import java.util.List;

public record TrashesResponse(
    List<InnerTrashResponse> trashes
) {

    public record InnerTrashResponse(
        Integer id,
        String category,
        Double latitude,
        Double longitude,
        LocalDateTime createdAt) {

    }

    public static TrashesResponse from(List<InnerTrashResponse> trashes) {
        return new TrashesResponse(trashes);
    }
}
