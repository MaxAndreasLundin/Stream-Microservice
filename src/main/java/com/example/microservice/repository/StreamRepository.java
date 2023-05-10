package com.example.microservice.repository;

import com.example.microservice.entity.Stream;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StreamRepository extends JpaRepository<Stream, Long> {
    Optional<Stream> findFirstByUserIdAndVideoId(String userId, String videoId);
    List<Stream> findAllByUserId(String userId);
}
