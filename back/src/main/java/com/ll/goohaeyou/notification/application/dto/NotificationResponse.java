package com.ll.goohaeyou.notification.application.dto;

import com.ll.goohaeyou.notification.domain.entity.Notification;
import com.ll.goohaeyou.notification.domain.type.CauseTypeCode;
import com.ll.goohaeyou.notification.domain.type.ResultTypeCode;

import java.time.format.DateTimeFormatter;
import java.util.List;

public record NotificationResponse(
        Long id,
        String createAt,
        String toMember,
        String fromMember,
        String relPostTitle,
        CauseTypeCode causeTypeCode,
        ResultTypeCode resultTypeCode,
        boolean seen,
        String url
) {
    public static NotificationResponse from(Notification notification) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        return new NotificationResponse(
                notification.getId(),
                notification.getCreateAt().format(formatter),
                notification.getToMember().getUsername(),
                notification.getFromMember().getUsername(),
                notification.getRelPostTitle(),
                notification.getCauseTypeCode(),
                notification.getResultTypeCode(),
                notification.isSeen(),
                notification.getUrl()
        );
    }

    public static List<NotificationResponse> convertToDtoList(List<Notification> notifications) {
        return notifications.stream()
                .map(NotificationResponse::from)
                .toList();
    }
}
