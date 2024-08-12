package com.ll.goohaeyou.chat.message.presentation;

import com.ll.goohaeyou.chat.message.dto.MessageDto;
import com.ll.goohaeyou.chat.message.dto.MessageForm;
import com.ll.goohaeyou.chat.message.domain.Message;
import com.ll.goohaeyou.chat.message.application.MessageService;
import com.ll.goohaeyou.global.apiResponse.ApiResponse;
import com.ll.goohaeyou.auth.domain.MemberDetails;
import com.ll.goohaeyou.global.standard.base.Empty;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/chat/{roomId}/message")
@RequiredArgsConstructor
@Tag(name = "Chatting", description = "채팅 API")
public class MessageController {
    private final MessageService messageService;
    private final SimpMessagingTemplate messagingTemplate;

    @PostMapping
    @Operation(summary = "채팅 생성")
    public ApiResponse<Empty> writeChat(@AuthenticationPrincipal MemberDetails memberDetails,
                                        @PathVariable(name = "roomId")Long roomId,
                                        @RequestBody MessageForm.Register form) {
        Message message = messageService.write(memberDetails.getUsername(), roomId, form);

        messagingTemplate.convertAndSend("/queue/api/chat/"+roomId+ "/newMessage", MessageDto.from(message));

        return ApiResponse.noContent();
    }

    @GetMapping
    @Operation(summary = "채팅 메세지 로드")
    public ApiResponse<List<MessageDto>> writeChat(@AuthenticationPrincipal MemberDetails memberDetails,
                                                   @PathVariable(name = "roomId")Long roomId) {

        return ApiResponse.ok(messageService.findByPostId(memberDetails.getUsername(),roomId));
    }
}
