package com.ll.goohaeyou.chat.room.presentation;

import com.ll.goohaeyou.chat.room.dto.RoomDto;
import com.ll.goohaeyou.chat.room.dto.RoomListDto;
import com.ll.goohaeyou.chat.room.application.RoomService;
import com.ll.goohaeyou.global.apiResponse.ApiResponse;
import com.ll.goohaeyou.auth.domain.MemberDetails;
import com.ll.goohaeyou.global.standard.base.Empty;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
@Tag(name = "Chatting", description = "채팅 API")
public class RoomController {
    private final RoomService roomService;

    @GetMapping("/{roomId}")
    @Operation(summary = "채팅방 입장")
    public ApiResponse<RoomDto> showRoom(@AuthenticationPrincipal MemberDetails memberDetails,
                                         @PathVariable Long roomId) {
        return ApiResponse.ok(roomService.findById(roomId, memberDetails.getUsername()));
    }

    @GetMapping
    @Operation(summary = "채팅방 목록")
    public ApiResponse<List<RoomListDto>> showRoomList(@AuthenticationPrincipal MemberDetails memberDetails) {
        return ApiResponse.ok(roomService.getRoomList(memberDetails.getUsername()));
    }

    @PutMapping("/{roomId}")
    @Operation(summary = "채팅방 퇴장")
    public ApiResponse<Empty> exitsRoom(@AuthenticationPrincipal MemberDetails memberDetails,
                                        @PathVariable Long roomId) {
        roomService.exitsRoom(memberDetails.getUsername(), roomId);
        return ApiResponse.noContent();
    }
}
