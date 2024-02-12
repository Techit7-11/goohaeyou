package com.ll.gooHaeYu.domain.notification.dto;

import com.ll.gooHaeYu.domain.application.application.entity.Application;
import com.ll.gooHaeYu.domain.member.member.entity.Member;
import com.ll.gooHaeYu.domain.notification.entity.Notification;
import com.ll.gooHaeYu.domain.notification.entity.type.CauseTypeCode;
import com.ll.gooHaeYu.domain.notification.entity.type.ResultTypeCode;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDateTime;
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

    public static NotificationDto fromEntity(Notification notification) {
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
                .build();
    }

    public static List<NotificationDto> toDtoList(List<Notification> notifications) {
        return notifications.stream()
                .map(NotificationDto::fromEntity)
                .toList();
    }

}
