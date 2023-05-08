package com.example.microservice.service;

import com.example.microservice.entity.Stream;
import com.example.microservice.repository.StreamRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class StreamService {

    @Autowired
    private StreamRepository streamRepository;

    public Stream startStream(String userId, String videoId) {
        if (validateVideo(videoId)) {
            Stream stream = new Stream();
            stream.setUserId(userId);
            stream.setVideoId(videoId);
            stream.setStartTime(LocalDateTime.now());

            return streamRepository.save(stream);
        } else {
            // Handle invalid video ID
            throw new IllegalArgumentException("Invalid video ID");
        }
    }

    public Stream stopStream(String userId, String videoId) {
        if (validateVideo(videoId)) {
            Optional<Stream> streamOptional = streamRepository
                    .findFirstByUserIdAndVideoIdAndEndTimeIsNull(userId, videoId);

            if (streamOptional.isPresent()) {
                Stream stream = streamOptional.get();
                stream.setEndTime(LocalDateTime.now());
                return streamRepository.save(stream);
            } else {
                // Handle stream not found
                throw new IllegalStateException("Stream not found");
            }
        } else {
            // Handle invalid video ID
            throw new IllegalArgumentException("Invalid video ID");
        }
    }

    private boolean validateVideo(String videoId) {
        ResponseEntity<String> response = getVideoById(videoId, "your-client-id");
        if (response.getStatusCode() == HttpStatus.OK) {
            // Parse the JSON response to check if it contains a valid video object
            // You can use any JSON parsing library like Jackson or Gson
            // For this example, we will use a simple string check
            return response.getBody().contains("id") && response.getBody().contains(videoId);
        }
        return false;
    }


    private ResponseEntity<String> getVideoById(String videoId, String clientId) {
        String apiUrl = "https://tv4-search.a2d.tv/assets";
        RestTemplate restTemplate = new RestTemplate();
        String requestUrl = apiUrl + "?client=" + clientId + "&id=" + videoId;

        ResponseEntity<String> response = restTemplate.getForEntity(requestUrl, String.class);
        return response;
    }
}
