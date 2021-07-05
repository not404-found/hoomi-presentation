package com.hoomicorp.controller;


import com.hoomicorp.model.request.StreamRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/stream")
public class StreamController {

    private static final Logger logger = LoggerFactory.getLogger(StreamController.class);

    @GetMapping(value = "/listen")
    public ResponseEntity<Void> listenRTCData(@RequestBody StreamRequest request) {
        final String streamId = request.getId();
        logger.info("[Streaming Controller] Starting listen WebRTC data: {}", streamId);

        logger.info("[Streaming Controller] Listening WebRTC data: {}", streamId);
        return ResponseEntity.ok().build();
    }


    @GetMapping(value = "/start")
    public ResponseEntity<String> startStream(@RequestBody StreamRequest request) {
        logger.info("[Streaming Controller] Starting stream: {}", request.getId());

        logger.info("[Streaming Controller] Stream successfully started: {}", "link");
        return ResponseEntity.ok("link");
    }

    @GetMapping(value = "/stop")
    public ResponseEntity<Void> stopStream(@RequestBody StreamRequest request) {
        final String streamId = request.getId();
        logger.info("[Streaming Controller] Stopping stream: {}", streamId);

        logger.info("[Streaming Controller] Stream successfully stopped: {}", streamId);
        return ResponseEntity.ok().build();
    }
}
