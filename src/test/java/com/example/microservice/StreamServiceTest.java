package com.example.microservice;

import com.example.microservice.entity.Stream;
import com.example.microservice.repository.StreamRepository;
import com.example.microservice.service.StreamService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = {StreamService.class})
public class StreamServiceTest {
    private static final String USER_ID = "Max";
    private static final String VALID_VIDEO_ID = "13808230";
    private static final String INVALID_VIDEO_ID = "INVALID_VIDEO_ID";

    @Autowired
    private StreamService streamService;

    @MockBean
    private StreamRepository streamRepository;

    @MockBean
    private RestTemplate restTemplate;

    private Stream stream;

    @BeforeEach
    public void setUp() {
        stream = new Stream();
        stream.setId(1L);
        stream.setUserId(USER_ID);
        stream.setVideoId(VALID_VIDEO_ID);
        stream.setStartTime(LocalDateTime.now());
        stream.setEndTime(null);
    }

    @Test
    public void startStream_validVideoId_startsNewStream() {
        when(streamRepository.findAllByUserIdAndEndTimeIsNull(any())).thenReturn(List.of());
        when(streamRepository.save(any(Stream.class))).thenReturn(stream);
        when(restTemplate.getForEntity(any(String.class), eq(String.class))).thenReturn(new ResponseEntity<>("{\"id\": \"" + VALID_VIDEO_ID + "\"}", HttpStatus.OK));

        Stream result = streamService.startStream(USER_ID, VALID_VIDEO_ID);
        assertNotNull(result);
        assertEquals(stream.getUserId(), result.getUserId());
        assertEquals(stream.getVideoId(), result.getVideoId());
    }

    @Test
    public void startStream_invalidVideoId_throwsIllegalArgumentException() {
        when(restTemplate.getForEntity(any(String.class), eq(String.class))).thenReturn(new ResponseEntity<>(HttpStatus.NOT_FOUND));

        assertThrows(IllegalArgumentException.class, () -> streamService.startStream(USER_ID, INVALID_VIDEO_ID));
    }

    @Test
    public void startStream_userHasMaxRunningStreams_throwsIllegalStateException() {
        when(streamRepository.findAllByUserIdAndEndTimeIsNull(any())).thenReturn(List.of(stream, new Stream()));
        assertThrows(IllegalStateException.class, () -> streamService.startStream(USER_ID, VALID_VIDEO_ID));
    }

    @Test
    public void stopStream_validVideoId_stopsStream() {
        when(streamRepository.findFirstByUserIdAndVideoIdAndEndTimeIsNull(any(), any())).thenReturn(Optional.of(stream));
        when(streamRepository.save(any(Stream.class))).thenReturn(stream);

        Stream result = streamService.stopStream(USER_ID, VALID_VIDEO_ID);
        assertNotNull(result);
        assertNotNull(result.getEndTime());
    }

    @Test
    public void stopStream_invalidVideoId_throwsIllegalStateException() {
        when(restTemplate.getForEntity(any(String.class), eq(String.class))).thenReturn(new ResponseEntity<>(HttpStatus.NOT_FOUND));

        assertThrows(IllegalStateException.class, () -> streamService.stopStream(USER_ID, INVALID_VIDEO_ID));
    }

    @Test
    public void stopStream_streamNotFound_throwsIllegalIllegalStateException() {
        when(streamRepository.findFirstByUserIdAndVideoIdAndEndTimeIsNull(any(), any())).thenReturn(Optional.empty());

        assertThrows(IllegalStateException.class, () -> streamService.stopStream(USER_ID, VALID_VIDEO_ID));
    }

    @Test
    public void getRunningStreams_returnsRunningStreams() {
        when(streamRepository.findAllByUserIdAndEndTimeIsNull(any())).thenReturn(List.of(stream));

        List<Stream> runningStreams = streamService.getRunningStreams(USER_ID);
        assertNotNull(runningStreams);
        assertEquals(1, runningStreams.size());
        assertEquals(stream, runningStreams.get(0));
    }
}
