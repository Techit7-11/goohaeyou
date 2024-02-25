package com.ll.gooHaeYu.global.readiness.controller;

import com.ll.gooHaeYu.global.readiness.service.ReadinessService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ReadinessController {
    private final ReadinessService readinessService;

    @GetMapping("/ready")
    public ResponseEntity<String> isReady() {
        // 애플리케이션의 준비 상태를 확인
        boolean ready = readinessService.checkApplicationReadiness();

        if (ready) {
            return ResponseEntity.ok("Application is ready");
        } else {
            // 준비되지 않았다면, HTTP 상태 코드와 메시지 반환
            return ResponseEntity.status(503).body("Application is not ready");
        }
    }
}
