package com.example.microservice.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Table()
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
    private String userId; // add index_user_Id

    @Column
    private String videoId;

    @Column
    private LocalDateTime startTime; // last seen, add index.

    @Column
    private LocalDateTime endTime;
}
