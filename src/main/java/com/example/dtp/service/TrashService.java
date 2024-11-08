package com.example.dtp.service;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.example.dtp.dto.TrashesResponse;
import com.example.dtp.model.Trash;
import com.example.dtp.model.TrashAcceptance;
import com.example.dtp.model.User;
import com.example.dtp.repository.TrashAcceptanceRepository;
import com.example.dtp.repository.TrashRepository;
import com.example.dtp.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TrashService {

    private final TrashRepository trashRepository;
    private final TrashAcceptanceRepository trashAcceptanceRepository;
    private final UserRepository userRepository;

    public TrashesResponse getAllTrash() {
        List<Trash> trashes = trashRepository.findAll();

        // 카테고리별로 단가를 설정합니다 (예: kg당 가격)
        final Map<String, Double> pricePerKg = Map.of(
            "bottle", 0.5,
            "plastic", 0.2,
            "metal", 1.0,
            "paper", 0.1
        );

        List<TrashesResponse.InnerTrashResponse> trashDTOs = trashes.stream()
            .map(trash -> {
                double price = calculatePrice(trash, pricePerKg); // 개별 가격 계산

                return new TrashesResponse.InnerTrashResponse(
                    trash.getId(),
                    trash.getCategory(),
                    trash.getLatitude(),
                    trash.getLongitude(),
                    trash.getCreatedAt(),
                    trash.getWeight(),
                    price
                );
            })
            .toList();

        double totalPrice = trashDTOs.stream()
            .mapToDouble(TrashesResponse.InnerTrashResponse::price)
            .sum();

        return TrashesResponse.from(trashDTOs, totalPrice);
    }

    private double calculatePrice(Trash trash, Map<String, Double> pricePerKg) {
        // 카테고리별로 설정된 단가를 가져와 무게와 곱하여 가격을 계산
        return pricePerKg.getOrDefault(trash.getCategory(), 0.0) * trash.getWeight();
    }

    public void acceptTrash(Long trashId, String userId) {
        // trashId를 기반으로 쓰레기 정보 확인
        Trash trash = trashRepository.findById(trashId)
            .orElseThrow(() -> new IllegalArgumentException("Trash not found with id: " + trashId));

        // User를 임시로 가져오는 로직 (UUID로 유저 확인 필요 시 추가 구현)
        User user = userRepository.findByUuid(userId)
            .orElseGet(() -> userRepository.save(User.builder()
                .uuid(userId)
                .build()));

        // TrashAcceptance 엔티티 생성 및 저장
        TrashAcceptance acceptance = new TrashAcceptance(user, trash);
        trashAcceptanceRepository.save(acceptance);
    }

    // GPS 위치 확인 메서드
    public boolean logGpsLocation(Long trashAcceptanceId, Double latitude, Double longitude) {
        TrashAcceptance acceptance = trashAcceptanceRepository.findById(trashAcceptanceId)
            .orElseThrow(() -> new IllegalArgumentException("Acceptance not found with id: " + trashAcceptanceId));

        Trash trash = acceptance.getTrash();

        // 위치가 50미터 이내인지 확인 후 결과 반환
        return isWithinProximity(trash.getLatitude(), trash.getLongitude(), latitude, longitude);
    }

    private boolean isWithinProximity(double lat1, double lon1, double lat2, double lon2) {
        final double EARTH_RADIUS = 6371; // 지구 반지름 (킬로미터)
        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);

        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
            + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
            * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distance = EARTH_RADIUS * c; // 거리 계산 결과 (킬로미터)

        return distance <= 0.05; // 50미터 이내일 경우 true 반환
    }
}
