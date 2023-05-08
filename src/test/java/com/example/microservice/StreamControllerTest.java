package com.example.microservice;

import com.example.microservice.controller.StreamController;
import com.example.microservice.entity.Stream;
import com.example.microservice.service.StreamService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebMvcTest(controllers = StreamController.class)
public class StreamControllerTest {
    private static final String USER_ID = "Max";
    private static final String VALID_VIDEO_ID = "13808230";
    private static final String INVALID_VIDEO_ID = "INVALID_VIDEO_ID";

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private StreamService streamService;

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
    public void startStream_validRequest_startsNewStream() throws Exception {
        when(streamService.startStream(any(), any())).thenReturn(stream);

        mockMvc.perform(post("/startstream")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"userId\": \"" + USER_ID + "\", \"videoId\": \"" + VALID_VIDEO_ID + "\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(stream.getId()))
                .andExpect(jsonPath("$.userId").value(stream.getUserId()))
                .andExpect(jsonPath("$.videoId").value(stream.getVideoId()));
    }

    @Test
    public void startStream_invalidRequest_returnsBadRequest() throws Exception {
        when(streamService.startStream(any(), any())).thenThrow(new IllegalArgumentException());

        mockMvc.perform(post("/startstream")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"userId\": \"" + USER_ID + "\", \"videoId\": \"" + INVALID_VIDEO_ID + "\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void startStream_maxRunningStreams_returnsForbidden() throws Exception {
        when(streamService.startStream(any(), any())).thenThrow(new IllegalStateException());

        mockMvc.perform(post("/startstream")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"userId\": \"" + USER_ID + "\", \"videoId\": \"" + VALID_VIDEO_ID + "\"}"))
                .andExpect(status().isForbidden());
    }

    @Test
    public void stopStream_validRequest_stopsStream() throws Exception {
        when(streamService.stopStream(any(), any())).thenReturn(stream);

        mockMvc.perform(post("/stopstream")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"userId\": \"" + USER_ID + "\", \"videoId\": \"" + VALID_VIDEO_ID + "\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(stream.getId()))
                .andExpect(jsonPath("$.userId").value(stream.getUserId()))
                .andExpect(jsonPath("$.videoId").value(stream.getVideoId()));
    }

    @Test
    public void stopStream_invalidRequest_returnsBadRequest() throws Exception {
        when(streamService.stopStream(any(), any())).thenThrow(new IllegalArgumentException());

        mockMvc.perform(post("/stopstream")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"userId\": \"" + USER_ID + "\", \"videoId\": \"" + INVALID_VIDEO_ID + "\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void stopStream_streamNotFound_returnsNotFound() throws Exception {
        when(streamService.stopStream(any(), any())).thenThrow(new IllegalStateException());

        mockMvc.perform(post("/stopstream")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"userId\": \"" + USER_ID + "\", \"videoId\": \"" + VALID_VIDEO_ID + "\"}"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void getRunningStreams_validRequest_returnsRunningStreams() throws Exception {
        when(streamService.getRunningStreams(any())).thenReturn(List.of(stream));

        mockMvc.perform(post("/runningstreams")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"userId\": \"" + USER_ID + "\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(stream.getId()))
                .andExpect(jsonPath("$[0].userId").value(stream.getUserId()))
                .andExpect(jsonPath("$[0].videoId").value(stream.getVideoId()));
    }
}

