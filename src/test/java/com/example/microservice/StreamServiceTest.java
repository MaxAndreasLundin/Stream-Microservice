package com.example.microservice;

import com.example.microservice.entity.Stream;
import com.example.microservice.repository.StreamRepository;
import com.example.microservice.service.StreamService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StreamServiceTest {

    @Mock
    private StreamRepository streamRepository;

    @InjectMocks
    private StreamService streamService;

    private String testUserId;
    private String testVideoId;

    @BeforeEach
    void setUp() {
        testUserId = "user123";
        testVideoId = "13808230";
    }

    @Test
    void startStreamSuccess() {
        when(streamRepository.findFirstByUserIdAndVideoIdAndEndTimeIsNull(testUserId, testVideoId)).thenReturn(Optional.empty());
        Stream streamToSave = Stream.builder().userId(testUserId).videoId(testVideoId).startTime(LocalDateTime.now()).build();
        when(streamRepository.save(any(Stream.class))).thenReturn(streamToSave);

        Stream stream = streamService.startStream(testUserId, testVideoId);

        assertNotNull(stream);
        assertEquals(testUserId, stream.getUserId());
        assertEquals(testVideoId, stream.getVideoId());
        assertNotNull(stream.getStartTime());
        assertNull(stream.getEndTime());

        verify(streamRepository, times(1)).findFirstByUserIdAndVideoIdAndEndTimeIsNull(testUserId, testVideoId);
        verify(streamRepository, times(1)).save(any(Stream.class));
    }

    @Test
    void startStreamExceedsLimit() {
        Stream stream1 = Stream.builder().userId(testUserId).videoId("video1").startTime(LocalDateTime.now()).build();
        Stream stream2 = Stream.builder().userId(testUserId).videoId("video2").startTime(LocalDateTime.now()).build();
        List<Stream> activeStreams = Arrays.asList(stream1, stream2);

        when(streamRepository.findAllByUserIdAndEndTimeIsNull(testUserId)).thenReturn(activeStreams);

        assertThrows(IllegalArgumentException.class, () -> streamService.startStream(testUserId, testVideoId));

        verify(streamRepository, times(1)).findAllByUserIdAndEndTimeIsNull(testUserId);
        verify(streamRepository, never()).findFirstByUserIdAndVideoIdAndEndTimeIsNull(testUserId, testVideoId);
        verify(streamRepository, never()).save(any(Stream.class));
    }
}
