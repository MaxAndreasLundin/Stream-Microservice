package com.example.microservice.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Table(name = "stream",
        indexes = {
                @Index(name = "index_user_id", columnList = "userId"),
                @Index(name = "index_last_seen", columnList = "lastSeen")
        })
@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Stream {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column
    private String userId;

    @Column
    private String videoId;

    @Column
    private LocalDateTime lastSeen;
}

