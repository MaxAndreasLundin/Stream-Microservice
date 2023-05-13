package com.example.microservice.repository;

import com.example.microservice.entity.Stream;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface StreamRepository extends JpaRepository<Stream, Long> {
    Optional<Stream> findFirstByUserIdAndVideoId(String userId, String videoId);
    List<Stream> findAllByUserId(String userId);

    @Transactional
    @Modifying
    @Query(value = "delete from Stream s where s.last_Seen <= (CURRENT_TIMESTAMP - INTERVAL '1 hour')", nativeQuery = true)
    void deleteByOlderThanOneHour();
}
