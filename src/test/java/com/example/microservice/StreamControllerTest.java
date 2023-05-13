package com.example.microservice;
import com.example.microservice.controller.StreamController;
import com.example.microservice.entity.Stream;
import com.example.microservice.service.StreamService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class StreamControllerTest {

    @Mock
    private StreamService streamService;

    @InjectMocks
    private StreamController streamController;

    private MockMvc mockMvc;

    private static final String USER_ID = "userId";
    private static final String VIDEO_ID = "videoId";

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(streamController).build();
    }

    @Test
    void shouldStartStreamGivenValidUserAndVideoId() throws Exception {
        Stream stream = new Stream();
        stream.setUserId(USER_ID);
        stream.setVideoId(VIDEO_ID);
        stream.setLastSeen(LocalDateTime.now());

        when(streamService.startStream(USER_ID, VIDEO_ID)).thenReturn(stream);

        mockMvc.perform(post("/v1/stream")
                        .param("userId", USER_ID)
                        .param("videoId", VIDEO_ID))
                .andExpect(status().isOk());
    }

    @Test
    void shouldStopStreamGivenValidUserAndVideoId() throws Exception {
        mockMvc.perform(delete("/v1/stream")
                        .param("userId", USER_ID)
                        .param("videoId", VIDEO_ID))
                .andExpect(status().isOk());
    }

    @Test
    void shouldGetRunningStreamsGivenValidUserId() throws Exception {
        Stream stream = new Stream();
        stream.setUserId(USER_ID);
        stream.setVideoId(VIDEO_ID);
        stream.setLastSeen(LocalDateTime.now());

        when(streamService.getRunningStreams(USER_ID)).thenReturn(List.of(stream));

        mockMvc.perform(get("/v1/stream")
                        .param("userId", USER_ID))
                .andExpect(status().isOk());
    }
}
