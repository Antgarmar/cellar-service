package tu.somelier.cellar.infrastructure.adapter.out.cache;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import tu.somelier.cellar.domain.model.CellarStats;
import tu.somelier.cellar.domain.port.out.CellarStatsCachePort;

import java.time.Duration;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class RedisCellarStatsCacheAdapter implements CellarStatsCachePort {

    private static final String KEY_PREFIX = "cellar:stats:";
    private static final Duration TTL = Duration.ofHours(1);

    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;

    @Override
    public Optional<CellarStats> getStats(UUID userId) {
        try {
            String json = redisTemplate.opsForValue().get(key(userId));
            if (json == null) return Optional.empty();
            return Optional.of(objectMapper.readValue(json, CellarStats.class));
        } catch (Exception e) {
            log.warn("Redis read failed for stats userId={}: {}", userId, e.getMessage());
            return Optional.empty();
        }
    }

    @Override
    public void saveStats(UUID userId, CellarStats stats) {
        try {
            String json = objectMapper.writeValueAsString(stats);
            redisTemplate.opsForValue().set(key(userId), json, TTL);
        } catch (Exception e) {
            log.warn("Redis write failed for stats userId={}: {}", userId, e.getMessage());
        }
    }

    @Override
    public void evictStats(UUID userId) {
        try {
            redisTemplate.delete(key(userId));
        } catch (Exception e) {
            log.warn("Redis evict failed for stats userId={}: {}", userId, e.getMessage());
        }
    }

    private String key(UUID userId) {
        return KEY_PREFIX + userId;
    }
}
