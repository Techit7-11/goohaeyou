package com.ll.goohaeyou.global.readiness.presentation;

import com.ll.goohaeyou.global.readiness.application.ReadinessService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Readiness", description = "배포 관련 API")
@RestController
@RequiredArgsConstructor
public class ReadinessController {
    private final ReadinessService readinessService;

    @GetMapping("/ready")
    @Operation(summary = "배포한 애플리케이션의 준비 상태를 확인")
    public ResponseEntity<String> isReady() {
        boolean ready = readinessService.checkApplicationReadiness();

        if (ready) {
            return ResponseEntity.ok("JobApplication is ready");
        } else {
            // 준비 되지 않았다면, 503 상태 코드와 메시지 반환
            return ResponseEntity.status(503).body("JobApplication is not ready");
        }
    }
}
