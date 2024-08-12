package com.ll.goohaeyou.notification.dto;

import com.ll.goohaeyou.notification.domain.Notification;
import com.ll.goohaeyou.notification.domain.type.CauseTypeCode;
import com.ll.goohaeyou.notification.domain.type.ResultTypeCode;
import lombok.Builder;
import lombok.Getter;

import java.time.format.DateTimeFormatter;
import java.util.List;

@Builder
@Getter
public class NotificationDto {
    private Long id;
    private String createAt;
    private String toMember;
    private String fromMember;
    private String relPostTitle;
    private CauseTypeCode causeTypeCode;
    private ResultTypeCode resultTypeCode;
    private boolean seen;
    private String url;

    public static NotificationDto from(Notification notification) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        return NotificationDto.builder()
                .id(notification.getId())
                .createAt(notification.getCreateAt().format(formatter))
                .toMember(notification.getToMember().getUsername())
                .fromMember(notification.getFromMember().getUsername())
                .relPostTitle(notification.getRelPostTitle())
                .causeTypeCode(notification.getCauseTypeCode())
                .resultTypeCode(notification.getResultTypeCode())
                .seen(notification.isSeen())
                .url(notification.getUrl())
                .build();
    }

    public static List<NotificationDto> convertToDtoList(List<Notification> notifications) {
        return notifications.stream()
                .map(NotificationDto::from)
                .toList();
    }
}
