package com.sparta.msa.example_part_3.cache;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.StringUtils;

@SpringBootTest
public class CachePatternTest {

    private static final Logger log = LoggerFactory.getLogger(CachePatternTest.class);

    private Map<Long, String> cacheTemplate = new HashMap<>();
    private Map<Long, String> userRepository = new HashMap<>();

    @BeforeEach
    void setUp() {
        userRepository.put(1L, "김철수");
        userRepository.put(2L, "최민수");
    }

    @Test
    public void cacheAsidePattern() {
        Long searchUserId = 1L;

        String cachedData = cacheTemplate.get(searchUserId);

        if (cachedData != null) {
            log.info("캐시에서 온 데이터 : {}", cachedData);
            return;
        }

        log.info("사용자 ID {}의 데이터를 데이터베이스에서 조회합니다.", searchUserId);

        String dbData = userRepository.get(searchUserId);

        if (StringUtils.hasText(dbData)) {
            cacheTemplate.put(searchUserId, dbData);
            log.info("사용자 ID {}의 데이터를 캐시에 저장했습니다.", searchUserId);
        } else {
            log.info("사용자 ID {}의 데이터가 데이터베이스에 존재하지 않습니다.", searchUserId);
        }
    }

    @Test
    public void writeThroughPattern() {
        Long updateUserId = 1L;
        String updateData = "김수현";

        log.info("Write-through: 사용자 ID {}의 데이터 저장 요청.", updateUserId);

        try {
            cacheTemplate.put(updateUserId, updateData);
            log.info("캐시에 사용자 ID {}의 데이터 저장 완료.", updateUserId);

            userRepository.put(updateUserId, updateData);
            log.info("데이터베이스에 사용자 ID {}의 데이터 저장 완료.", updateUserId);

            log.info("Write-through: 사용자 ID {}의 데이터 캐시와 DB에 성공적으로 저장되었습니다.", updateUserId);
        } catch (Exception e) {
            log.info("Write-through 실패! 사용자 ID {} 의 데이터 저장 중 오류 발생: {}", updateUserId, e.getMessage());
            throw new RuntimeException("데이터 저장 실패", e);
        }
    }

    @Test
    public void writeBackPattern() {
        Long updateUserId = 1L;
        String updateData = "김수현";

        log.info("Write-back: 사용자 ID {}의 데이터 저장 요청 (캐시 우선).", updateUserId);

        cacheTemplate.put(updateUserId, updateData);
        log.info("캐시에 사용자 ID {}의 데이터 저장 완료. (응답 즉시 반환)", updateUserId);

        // 실제 프로덕션에서는 메시지 큐(Kafka, RabbitMQ)나 스케줄러(Quartz)와 결합하여 견고하게 구현됨
        CompletableFuture.runAsync(() -> {
            try {
                userRepository.put(updateUserId, updateData);
                log.info("비동기 DB 업데이트 완료: 사용자 ID {}의 데이터가 DB에 최종 저장되었습니다.", updateUserId);
            } catch (Exception e) {
                log.info("비동기 DB 업데이트 실패! 사용자 ID {} : {}", updateUserId, e.getMessage());
            }
        });
    }

    @Test
    public void writeAroundPattern() {
        Long updateUserId = 1L;
        String updateData = "김수현";

        log.info("Write-around: 사용자 ID {}의 데이터 저장 요청 (DB에만 저장).", updateUserId);

        userRepository.put(updateUserId, updateData);
        log.info("데이터베이스에 사용자 ID {}의 데이터 저장 완료.", updateUserId);

        //선택 사항: 만약 캐시에 해당 userId의 오래된 데이터가 있을 수 있다면, 명시적으로 삭제하여 다음 읽기 시 DB에서 가져오도록 유도.
        cacheTemplate.remove(updateUserId);
        log.info("(선택적) 사용자 ID {}의 캐시 데이터 삭제 완료.", updateUserId);
        //이후, cacheAsidePattern()과 동일한 로직으로 조회 시, 캐시 갱신
    }
}