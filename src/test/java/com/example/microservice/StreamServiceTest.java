package com.example.microservice;

import com.example.microservice.entity.Stream;
import com.example.microservice.repository.StreamRepository;
import com.example.microservice.service.StreamService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class StreamServiceTest {

    @Mock
    private StreamRepository streamRepository;

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private StreamService streamService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    private static final String VALID_VIDEO_ID = "validVideoId";
    private static final String INVALID_VIDEO_ID = "invalidVideoId";
    private static final String USER_ID = "userId";
    private static final String VIDEO_ID1 = "videoId1";
    private static final String VIDEO_ID2 = "videoId2";

    @Test
    void shouldStopStreamGivenValidUserAndVideoId() {
        Stream stream = new Stream();
        stream.setUserId(USER_ID);
        stream.setVideoId(VIDEO_ID1);
        stream.setLastSeen(LocalDateTime.now());

        when(streamRepository.findFirstByUserIdAndVideoId(USER_ID, VIDEO_ID1)).thenReturn(Optional.of(stream));
        doNothing().when(streamRepository).delete(any(Stream.class));

        streamService.stopStream(USER_ID, VIDEO_ID1);

        verify(streamRepository, times(1)).delete(any(Stream.class));
    }

    @Test
    void shouldThrowExceptionWhenStoppingNonExistingStream() {
        when(streamRepository.findFirstByUserIdAndVideoId(USER_ID, VIDEO_ID1)).thenReturn(Optional.empty());

        assertThrows(IllegalStateException.class, () -> streamService.stopStream(USER_ID, VIDEO_ID1));
    }

    @Test
    void shouldStartStreamGivenValidUserAndVideoId() {
        when(streamRepository.findAllByUserId(USER_ID)).thenReturn(Collections.emptyList());
        when(restTemplate.getForEntity(anyString(), eq(String.class)))
                .thenReturn(new ResponseEntity<>(HttpStatus.OK));
        when(streamRepository.save(any(Stream.class))).thenAnswer(i -> i.getArguments()[0]);

        Stream result = streamService.startStream(USER_ID, VALID_VIDEO_ID);

        assertNotNull(result);
        assertEquals(USER_ID, result.getUserId());
        assertEquals(VALID_VIDEO_ID, result.getVideoId());
        verify(streamRepository, times(1)).save(any(Stream.class));
    }

    @Test
    void shouldThrowExceptionWhenStartingStreamAndMaxRunningStreamsReached() {
        Stream stream1 = new Stream();
        stream1.setUserId(USER_ID);
        stream1.setVideoId(VIDEO_ID1);
        stream1.setLastSeen(LocalDateTime.now());

        Stream stream2 = new Stream();
        stream2.setUserId(USER_ID);
        stream2.setVideoId(VIDEO_ID2);
        stream2.setLastSeen(LocalDateTime.now());

        when(streamRepository.findAllByUserId(USER_ID)).thenReturn(List.of(stream1, stream2));

        assertThrows(IllegalStateException.class, () -> streamService.startStream(USER_ID, VALID_VIDEO_ID));
    }

    @Test
    void shouldThrowExceptionWhenStartingStreamWithInvalidVideoId() {
        when(streamRepository.findAllByUserId(USER_ID)).thenReturn(Collections.emptyList());
        when(restTemplate.getForEntity(anyString(), eq(String.class)))
                .thenReturn(new ResponseEntity<>(HttpStatus.NOT_FOUND));

        assertThrows(IllegalArgumentException.class, () -> streamService.startStream(USER_ID, INVALID_VIDEO_ID));
    }
}
