package com.example.low_resource_api;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
public class IngestionController {

    private final ProcessingService processingService;

    public IngestionController(ProcessingService processingService) {
        this.processingService = processingService;
    }

    @PostMapping("/process")
    public Mono<ResponseEntity<Void>> processNumber(@RequestBody RequestPayload payload) {
        processingService.processNumber(payload.getNumber());
        return Mono.just(ResponseEntity.status(HttpStatus.ACCEPTED).build());
    }

    static class RequestPayload {
        private Integer number;
        public Integer getNumber() { return number; }
        public void setNumber(Integer number) { this.number = number; }
    }
}