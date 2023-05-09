package com.example.microservice.service;

import com.example.microservice.dto.StreamResponse;
import com.example.microservice.entity.Stream;
import com.example.microservice.repository.StreamRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class StreamService {
    private final StreamRepository streamRepository;

    public Stream startStream(String userId, String videoId) {
        if (hasMaxRunningStreams(userId)) {
            throw new IllegalStateException("User has reached the maximum allowed running streams");
        }

        if (validateStream(videoId)) {
            Stream stream = new Stream();
            stream.setUserId(userId);
            stream.setVideoId(videoId);
            stream.setStartTime(LocalDateTime.now());

            return streamRepository.save(stream);
        } else {
            throw new IllegalArgumentException("Invalid video ID");
        }
    }

    public Stream stopStream(String userId, String videoId) {
        Optional<Stream> streamOptional = streamRepository
                .findFirstByUserIdAndVideoIdAndEndTimeIsNull(userId, videoId);

        if (streamOptional.isPresent()) {
            Stream stream = streamOptional.get();
            stream.setEndTime(LocalDateTime.now());
            return streamRepository.save(stream);
        } else {
            throw new IllegalStateException("Stream not found");
        }
    }

    public List<Stream> getRunningStreams(String userId) {
        return streamRepository.findAllByUserIdAndEndTimeIsNull(userId);
    }

    private boolean validateStream(String videoId) {
        ResponseEntity<String> response = getStreamById(videoId);
        if (response.getStatusCode() == HttpStatus.OK) {
            try {
                ObjectMapper mapper = new ObjectMapper();
                StreamResponse streamResponse = mapper.readValue(response.getBody(), StreamResponse.class);
                if (!streamResponse.getData().isEmpty()) {
                    return videoId.equals(streamResponse.getData().get(0).getId());
                }
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    private ResponseEntity<String> getStreamById(String videoId) {
        String apiUrl = "https://tv4-search.a2d.tv/assets";
        RestTemplate restTemplate = new RestTemplate();
        String requestUrl = apiUrl + "?id=" + videoId;

        return restTemplate.getForEntity(requestUrl, String.class);
    }

    private boolean hasMaxRunningStreams(String userId) {
        List<Stream> runningStreams = streamRepository.findAllByUserIdAndEndTimeIsNull(userId);
        return runningStreams.size() >= 2;
    }
}
