package com.ll.gooHaeYu.domain.chat.room.controller;

import com.ll.gooHaeYu.domain.chat.room.dto.RoomDto;
import com.ll.gooHaeYu.domain.chat.room.dto.RoomListDto;
import com.ll.gooHaeYu.domain.chat.room.service.RoomService;
import com.ll.gooHaeYu.global.apiResponse.ApiResponse;
import com.ll.gooHaeYu.global.security.MemberDetails;
import com.ll.gooHaeYu.standard.base.Empty;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Chatting", description = "채팅 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/chat")
public class RoomController {
    private final RoomService roomService;

//    @PostMapping
//    @Operation(summary = "채팅방 생서")
//    public RsData<URI> createRoom(@AuthenticationPrincipal MemberDetails memberDetails,
//                                  @Valid @RequestBody CreateForm createForm) {
//        Long roomId = roomService.createRoom(memberDetails.getId(),createForm.getMemberId());
//        return RsData.of("201","CREATE",URI.create("/api/chat/" + roomId));
//    }

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
