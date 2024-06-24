package com.ll.goohaeyou.global.readiness.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

@Service
@RequiredArgsConstructor
public class ReadinessService {
    private final DataSource dataSource;

    public boolean checkApplicationReadiness() {
        return checkDatabaseConnection();
    }

    private boolean checkDatabaseConnection() {
        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT 1")) {
            if (rs.next()) {
                // 쿼리 실행이 성공하면, 데이터베이스 연결이 정상임을 의미
                return true;
            }
        } catch (Exception e) {
            // 데이터베이스 연결 실패 또는 쿼리 실행 중 에러 발생
            System.out.println("Database connection check failed: " + e.getMessage());
            return false;
        }
        return false;
    }
}
