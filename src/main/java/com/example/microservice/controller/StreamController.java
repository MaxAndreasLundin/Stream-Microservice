package com.example.microservice.controller;

import com.example.microservice.dto.StreamRequest;
import com.example.microservice.dto.UserIdRequest;
import com.example.microservice.entity.Stream;
import com.example.microservice.service.StreamService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
public class StreamController {
    private final StreamService streamService;

    @PostMapping("/startstream")
    public ResponseEntity<Stream> startStream(@RequestBody StreamRequest streamRequest) {
        try {
            Stream stream = streamService.startStream(streamRequest.getUserId(), streamRequest.getVideoId());
            return new ResponseEntity<>(stream, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/stopstream")
    public ResponseEntity<Stream> stopStream(@RequestBody StreamRequest streamRequest) {
        try {
            Stream stream = streamService.stopStream(streamRequest.getUserId(), streamRequest.getVideoId());
            return new ResponseEntity<>(stream, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (IllegalStateException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/runningstreams")
    public ResponseEntity<List<Stream>> getRunningStreams(@RequestBody UserIdRequest userIdRequest) {
        List<Stream> runningStreams = streamService.getRunningStreams(userIdRequest.getUserId());
        return new ResponseEntity<>(runningStreams, HttpStatus.OK);
    }
}
