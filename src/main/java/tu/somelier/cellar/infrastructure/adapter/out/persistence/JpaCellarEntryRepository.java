package tu.somelier.cellar.infrastructure.adapter.out.persistence;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import tu.somelier.cellar.domain.model.CellarEntryStatus;
import tu.somelier.cellar.infrastructure.adapter.out.persistence.entity.CellarEntryEntity;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface JpaCellarEntryRepository extends JpaRepository<CellarEntryEntity, UUID> {

    Optional<CellarEntryEntity> findByIdAndUserId(UUID id, UUID userId);

    Page<CellarEntryEntity> findByUserId(UUID userId, Pageable pageable);

    void deleteByIdAndUserId(UUID id, UUID userId);

    List<CellarEntryEntity> findByUserIdAndStatus(UUID userId, CellarEntryStatus status);

    boolean existsByIdAndUserId(UUID id, UUID userId);

    @Query("""
            SELECT e FROM CellarEntryEntity e
            WHERE e.userId = :userId
              AND e.status = 'AVAILABLE'
              AND (e.drinkFrom IS NULL OR e.drinkFrom <= :today)
              AND (e.drinkUntil IS NULL OR e.drinkUntil >= :today)
            """)
    List<CellarEntryEntity> findDrinkNow(@Param("userId") UUID userId, @Param("today") LocalDate today);

    @Query("""
            SELECT e FROM CellarEntryEntity e
            WHERE e.userId = :userId
              AND e.status = 'AVAILABLE'
              AND e.drinkUntil IS NOT NULL
              AND e.drinkUntil >= :today
              AND e.drinkUntil <= :until
            """)
    List<CellarEntryEntity> findExpiringSoon(
            @Param("userId") UUID userId,
            @Param("today") LocalDate today,
            @Param("until") LocalDate until);
}
