package com.example.microservice.service;

import com.example.microservice.entity.Stream;
import com.example.microservice.repository.StreamRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class StreamService {
    private final StreamRepository streamRepository;
    private final RestTemplate restTemplate;
    private static final int MAX_RUNNING_STREAMS = 2;

    public Stream startStream(String userId, String videoId) {
        if (hasMaxRunningStreams(userId)) {
            throw new IllegalStateException("User has reached the maximum allowed running streams");
        }

        if (!videoExistsLenient(videoId)) {
            throw new IllegalArgumentException("Invalid video ID");
        }

        Stream stream = new Stream();
        stream.setUserId(userId);
        stream.setVideoId(videoId);
        stream.setLastSeen(LocalDateTime.now());

        return streamRepository.save(stream);
    }

    public void stopStream(String userId, String videoId) {
        Optional<Stream> streamOptional = streamRepository
                .findFirstByUserIdAndVideoId(userId, videoId);

        if (streamOptional.isPresent()) {
            streamRepository.delete(streamOptional.get());
        } else {
            throw new IllegalStateException("Stream not found");
        }
    }

    public List<Stream> getRunningStreams(String userId) {
        return streamRepository.findAllByUserId(userId);
    }

    // Internal server error would return true to allow user to watch.
    private boolean videoExistsLenient(String videoId) {
        String url = "https://tv4-search.a2d.tv/assets/" + videoId;
        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
        return response.getStatusCode() != HttpStatus.NOT_FOUND;
    }

    private boolean hasMaxRunningStreams(String userId) {
        List<Stream> runningStreams = streamRepository.findAllByUserId(userId);
        return runningStreams.size() >= MAX_RUNNING_STREAMS;
    }

    @Scheduled(fixedDelay = 60000)
    public void purgeOldStreams() {
        streamRepository.deleteByOlderThanOneHour();
    }
}
