package com.sparta.msa.example_part_3.redis;

import java.util.List;
import java.util.Map;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;

@SpringBootTest
public class RedisDataStructureTest {

    private static final Logger log = LoggerFactory.getLogger(RedisDataStructureTest.class);

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Test
    public void redisBasicTest() {
        redisTemplate.opsForValue().set("myRedisKey", "Hello World!");
        String myRedisValue = redisTemplate.opsForValue().get("myRedisKey");
        log.info("value: {}", myRedisValue);
    }

    @Test
    public void testStringBasicOperations() {
        final String key = "user:123:session";
        final String value = "active";

        redisTemplate.opsForValue().set(key, value);
        log.info("저장: Key={}, Value={}", key, value);

        String sessionStatus = redisTemplate.opsForValue().get(key);
        log.info("조회된 세션 상태: {}", sessionStatus); // active

        assert sessionStatus.equals(value);
    }

    @Test
    public void testStringCounterOperations() {
        final String pageViewsKey = "page:views";
        final String likesKey = "item:123:likes";

        redisTemplate.delete(pageViewsKey);
        redisTemplate.delete(likesKey);

        // 1. 1 증가 (jedis.incr)
        // 키가 없으면 0에서 시작하여 1을 반환합니다.
        Long pageViewsAfterIncr = redisTemplate.opsForValue().increment(pageViewsKey);
        log.info("{}: 1 증가 후: {}", pageViewsKey, pageViewsAfterIncr); // 1

        // 2. 1 감소 (jedis.decr)
        Long pageViewsAfterDecr = redisTemplate.opsForValue().decrement(pageViewsKey);
        log.info("{}: 1 감소 후: {}", pageViewsKey, pageViewsAfterDecr); // 0

        // 3. 특정 값(5) 증가 (jedis.incrBy)
        Long likesAfterIncrBy = redisTemplate.opsForValue().increment(likesKey, 5);
        log.info("{}: 5 증가 후: {}", likesKey, likesAfterIncrBy); // 5

        // 4. 데이터 조회 및 Long 변환
        String pageViewsStr = redisTemplate.opsForValue().get(pageViewsKey);
        long pageViews = Long.parseLong(pageViewsStr);
        log.info("최종 조회된 페이지 뷰 수 (Long 변환): {}", pageViews); // 0

        assert pageViews == 0;
        assert redisTemplate.opsForValue().get(likesKey).equals("5");
    }


    @Test
    public void testListOperations() {
        final String TASKS_KEY = "tasks";
        final String CHAT_KEY = "chat_room:general";

        redisTemplate.delete(TASKS_KEY);
        redisTemplate.delete(CHAT_KEY);

        // 1. 작업 대기열 생성 (LIFO 스택 방식 구현 - Head(왼쪽)에 삽입)
        Long listSize = redisTemplate.opsForList().leftPushAll(TASKS_KEY, "Task1", "Task2", "Task3");

        // Redis 리스트 상태: [Task3, Task2, Task1]
        log.info("{}: 3개 삽입 후 크기: {}", TASKS_KEY, listSize);

        // 2. 작업 처리 (FIFO 큐 방식 구현 - Tail(오른쪽)에서 제거)
        String task1 = redisTemplate.opsForList().rightPop(TASKS_KEY);
        log.info("RightPop (Task1 처리): {}", task1); // Task1

        String task2 = redisTemplate.opsForList().rightPop(TASKS_KEY);
        log.info("RightPop (Task2 처리): {}", task2); // Task2
        // 최종 리스트 상태: [Task3]

        // 최근 메시지 조회
        redisTemplate.opsForList().rightPushAll(CHAT_KEY, "Message A", "Message B", "Message C");
        // 리스트 상태: [Message A, Message B, Message C]

        // range: 0부터 -1까지 (전체) 조회
        List<String> messages = redisTemplate.opsForList().range(CHAT_KEY, 0, -1);
        log.info("{}: 전체 메시지 조회 결과: {}", CHAT_KEY, messages);
        // 리스트 상태: [Message A, Message B, Message C]
    }

    @Test
    public void testSetOperations() {
        final String PARTICIPANTS_KEY = "event:participants";
        final String EVENT1_KEY = "event1";
        final String EVENT2_KEY = "event2";

        // 초기화: 테스트 전에 키 삭제
        redisTemplate.delete(PARTICIPANTS_KEY);
        redisTemplate.delete(EVENT1_KEY);
        redisTemplate.delete(EVENT2_KEY);

        // 고유 사용자 저장 (jedis.sadd)
        // add: Set에 값을 추가합니다. 중복된 "User1"은 무시됩니다.
        Long addedCount = redisTemplate.opsForSet()
                .add(PARTICIPANTS_KEY, "User1", "User2", "User3", "User1");
        log.info("Participants에 추가된 고유 사용자 수: {}", addedCount); // 3

        // Set<String> participants = jedis.smembers("event:participants");
        // members: Set의 모든 멤버(요소)를 조회합니다.
        Set<String> participants = redisTemplate.opsForSet().members(PARTICIPANTS_KEY);
        log.info("{}: 고유 사용자 목록: {}", PARTICIPANTS_KEY,
                participants); // {"User1", "User2", "User3"} (순서 보장 안됨)

        // 집합 데이터
        redisTemplate.opsForSet().add(EVENT1_KEY, "User1", "User2", "User4");
        redisTemplate.opsForSet().add(EVENT2_KEY, "User2", "User3", "User4");

        // 교집합 구하기
        // intersect: EVENT1과 EVENT2의 공통 멤버를 Set으로 반환합니다.
        Set<String> commonUsers = redisTemplate.opsForSet().intersect(EVENT1_KEY, EVENT2_KEY);
        log.info("교집합 (Common Users): {}", commonUsers); // {"User2", "User4"}

        // 합집합 구하기 (jedis.sunion)
        // union: EVENT1과 EVENT2의 모든 멤버를 합쳐 Set으로 반환합니다.
        Set<String> allUsers = redisTemplate.opsForSet().union(EVENT1_KEY, EVENT2_KEY);
        log.info("합집합 (All Users): {}", allUsers); // {"User1", "User2", "User3", "User4"}

        // 차집합 구하기 (EVENT1에는 있지만 EVENT2에는 없는 사용자)
        // difference: 첫 번째 키(EVENT1)에는 있지만 나머지 키(EVENT2)에는 없는 멤버를 반환합니다.
        Set<String> onlyInEvent1 = redisTemplate.opsForSet().difference(EVENT1_KEY, EVENT2_KEY);
        log.info("차집합 (Only in Event1): {}", onlyInEvent1); // {"User1"}
    }


    @Test
    public void testHashOperations() {
        final String USER_PROFILE_KEY = "user:123";

        // 초기화: 테스트 전에 키 삭제
        redisTemplate.delete(USER_PROFILE_KEY);

        // 사용자 프로필 저장
        // hSet: Hash에 특정 필드(Field)와 값(Value)을 저장합니다.
        redisTemplate.opsForHash().put(USER_PROFILE_KEY, "name", "John Doe");
        redisTemplate.opsForHash().put(USER_PROFILE_KEY, "email", "john.doe@example.com");
        redisTemplate.opsForHash().put(USER_PROFILE_KEY, "age", "30");
        redisTemplate.opsForHash().put(USER_PROFILE_KEY, "city", "Seoul");

        log.info("{}: 4개 필드 저장 완료.", USER_PROFILE_KEY);

        // 필드 단위 조회
        // get: Hash Key에서 특정 필드에 해당하는 값을 조회합니다.
        String name = (String) redisTemplate.opsForHash().get(USER_PROFILE_KEY, "name");
        String email = (String) redisTemplate.opsForHash().get(USER_PROFILE_KEY, "email");

        log.info("조회된 이름 (name): {}", name);     // John Doe
        log.info("조회된 이메일 (email): {}", email); // john.doe@example.com

        // 모든 필드-값 쌍 조회
        // entries: Hash Key의 모든 필드-값 쌍을 Map<Object, Object> 형태로 반환합니다.
        Map<Object, Object> userProfile = redisTemplate.opsForHash().entries(USER_PROFILE_KEY);

        log.info("{}: 모든 필드-값 쌍 조회: {}", USER_PROFILE_KEY, userProfile);
        // 출력: {name=John Doe, email=john.doe@example.com, age=30, city=Seoul}

    }


    @Test
    public void testSortedSetOperations() {
        final String LEADERBOARD_KEY = "leaderboard";

        // 초기화: 테스트 전에 키 삭제
        redisTemplate.delete(LEADERBOARD_KEY);

        // 게임 순위 저장
        // add: 값(member)과 점수(score)를 함께 저장합니다.
        redisTemplate.opsForZSet().add(LEADERBOARD_KEY, "Player1", 1500.0);
        redisTemplate.opsForZSet().add(LEADERBOARD_KEY, "Player2", 2000.0);
        redisTemplate.opsForZSet().add(LEADERBOARD_KEY, "Player3", 1800.0);

        // 상위 순위 조회
        // reverseRange: 점수가 높은 순(내림차순)으로 지정된 범위(0번째부터 1번째까지, 즉 상위 2명)의 멤버를 조회합니다.
        Set<String> topPlayers = redisTemplate.opsForZSet().reverseRange(LEADERBOARD_KEY, 0, 1);
        log.info("상위 2명 조회 (내림차순): {}", topPlayers);
        // 결과: [Player2, Player3] (Redis의 순서대로 출력되며, Set이 아닌 List와 유사한 순서로 반환됨)

        // 점수 업데이트
        // incrementScore: 특정 멤버의 점수를 지정된 값만큼 증가시킵니다.
        Double newScore = redisTemplate.opsForZSet().incrementScore(LEADERBOARD_KEY, "Player1", 100.0);
        log.info("Player1의 점수 업데이트: 1500.0 -> {}", newScore); // 1600.0

        // 특정 점수 범위 조회
        // rangeByScore: 지정된 점수 범위 내의 멤버를 조회합니다.
        Set<String> playersInRange = redisTemplate.opsForZSet()
                .rangeByScore(LEADERBOARD_KEY, 1600.0, 1900.0);
        log.info("점수 1600.0 ~ 1900.0 범위 플레이어: {}", playersInRange);
        // 결과: [Player1, Player3]

        // 5. 멤버의 현재 순위 조회 (0부터 시작)
        // rank: 점수가 낮은 순(오름차순)으로 해당 멤버의 순위를 반환합니다.
        Long rankOfPlayer3 = redisTemplate.opsForZSet().rank(LEADERBOARD_KEY, "Player3");
        log.info("Player3의 오름차순 순위 (0부터 시작): {}", rankOfPlayer3);
        // 1 (0:Player1, 1:Player3, 2:Player2)

    }


}