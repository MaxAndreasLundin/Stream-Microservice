package com.example.microservice.controller;

import com.example.microservice.entity.Stream;
import com.example.microservice.service.StreamService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/v1/stream")
public class StreamController {
    private final StreamService streamService;

    @PostMapping
    public ResponseEntity<Stream> startStream(@RequestParam String userId, @RequestParam String videoId) {
        try {
            Stream stream = streamService.startStream(userId, videoId);
            return new ResponseEntity<>(stream, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (IllegalStateException e) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
    }

    @DeleteMapping
    public ResponseEntity<String> stopStream(@RequestParam String userId, @RequestParam String videoId) {
        try {
            streamService.stopStream(userId, videoId);
            return new ResponseEntity<>("Stream successfully stopped", HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>("Invalid request", HttpStatus.BAD_REQUEST);
        } catch (IllegalStateException e) {
            return new ResponseEntity<>("Stream not found", HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping
    public ResponseEntity<List<Stream>> getRunningStreams(@RequestParam String userId) {
        List<Stream> runningStreams = streamService.getRunningStreams(userId);
        return new ResponseEntity<>(runningStreams, HttpStatus.OK);
    }
}
