package com.ll.gooHaeYu.domain.chat.message.dto;

import com.ll.gooHaeYu.domain.chat.message.entity.Message;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
public class MessageDto {

    private Long id;
    @NotNull
    private String sender;
    @NotNull
    private String text;
    private LocalDateTime createdAt;

    public static MessageDto fromEntity(Message message) {

        return MessageDto.builder()
                .id(message.getId())
                .sender(message.getSender())
                .text(message.getContent())
                .createdAt(message.getCreatedAt())
                .build();
    }

    public static List<MessageDto> toDtoList(List<Message> messages) {
        return messages.stream()
                .map(MessageDto::fromEntity)
                .toList();
    }
}
