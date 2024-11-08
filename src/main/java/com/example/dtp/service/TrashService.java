package com.example.dtp.service;

import java.util.List;

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
        List<TrashesResponse.InnerTrashResponse> trashDTOs = trashes.stream()
            .map(trash -> new TrashesResponse.InnerTrashResponse(
                trash.getId(),
                trash.getCategory(),
                trash.getLatitude(),
                trash.getLongitude(),
                trash.getCreatedAt()))
            .toList();

        return TrashesResponse.from(trashDTOs);
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
    public void logGpsLocation(Long trashAcceptanceId, Double latitude, Double longitude) {
        TrashAcceptance acceptance = trashAcceptanceRepository.findById(trashAcceptanceId)
            .orElseThrow(() -> new IllegalArgumentException("Acceptance not found with id: " + trashAcceptanceId));

        Trash trash = acceptance.getTrash();

        // 위도 경도를 기반으로 두 위치가 가까운지 확인
        if (isWithinProximity(trash.getLatitude(), trash.getLongitude(), latitude, longitude)) {
            // 위치가 일치하면 처리 로직 (예: 상태 업데이트 등)
            // 여기에 필요한 로직 추가
        } else {
            throw new IllegalArgumentException("Location does not match the accepted trash location.");
        }
    }

    // 거리 계산 메서드 (위도, 경도 기준)
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
