package com.example.dtp.dto;

import java.time.LocalDateTime;
import java.util.List;

public record TrashesResponse(
    List<InnerTrashResponse> trashes,
    double totalPrice // totalPrice 추가
) {

    public record InnerTrashResponse(
        Integer id,
        String category,
        Double latitude,
        Double longitude,
        LocalDateTime createdAt,
        Double weight, // weight 필드 추가
        double price   // 개별 price 필드 추가
    ) {
    }

    public static TrashesResponse from(List<InnerTrashResponse> trashes, double totalPrice) {
        return new TrashesResponse(trashes, totalPrice);
    }
}
