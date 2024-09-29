package com.ll.goohaeyou.payment.payment.domain.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class OrderIdUtil {

    public static String generateJobApplicationPaymentId(Long userId, Long jobApplicationId) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyMMddHHmmss");
        String currentTime = LocalDateTime.now().format(formatter);

        return String.format("ORD-%d-JAID-%d-%s", userId, jobApplicationId, currentTime);
    }

    public static Long parseUserIdFromOrderId(String orderId) {
        try {
            String[] parts = orderId.split("-");
            return Long.parseLong(parts[1]);
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid order ID format.");
        }
    }

    public static Long parseJobApplicationIdFromOrderId(String orderId) {
        try {
            String[] parts = orderId.split("-");
            if (!"JAID".equals(parts[2])) {
                throw new IllegalArgumentException("Invalid order ID format. Missing 'JAID'.");
            }
            return Long.parseLong(parts[3]);
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid order ID format.");
        }
    }
}
