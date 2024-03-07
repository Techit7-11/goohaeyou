package com.ll.gooHaeYu.domain.chat.message.controller;

import com.ll.gooHaeYu.domain.chat.message.dto.MessageDto;
import com.ll.gooHaeYu.domain.chat.message.dto.MessageForm;
import com.ll.gooHaeYu.domain.chat.message.entity.Message;
import com.ll.gooHaeYu.domain.chat.message.service.MessageService;
import com.ll.gooHaeYu.global.rsData.RsData;
import com.ll.gooHaeYu.global.security.MemberDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Chatting", description = "채팅 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/chat/{roomId}/message")
public class MessageController {
    private final MessageService messageService;
    private final SimpMessagingTemplate messagingTemplate;

    @PostMapping
    @Operation(summary = "채팅 생성")
    public RsData<Void> writeChat(@AuthenticationPrincipal MemberDetails memberDetails,
                                        @PathVariable(name = "roomId")Long roomId,
                                        @RequestBody MessageForm.Register form) {
        Message message = messageService.write(memberDetails.getUsername(), roomId, form);

        messagingTemplate.convertAndSend("/queue/api/chat/"+roomId+ "/newMessage", MessageDto.fromEntity(message));

        return RsData.of("204", "NO_CONTENT");
    }

    @GetMapping
    @Operation(summary = "채팅 메세지 로드")
    public RsData<List<MessageDto>> writeChat(@AuthenticationPrincipal MemberDetails memberDetails,
                                              @PathVariable(name = "roomId")Long roomId) {

        return RsData.of(messageService.findByPostId(memberDetails.getUsername(),roomId));
    }
}
