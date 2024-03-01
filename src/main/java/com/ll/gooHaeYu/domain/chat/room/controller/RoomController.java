package com.ll.gooHaeYu.domain.chat.room.controller;

import com.ll.gooHaeYu.domain.chat.room.dto.CreateForm;
import com.ll.gooHaeYu.domain.chat.room.dto.RoomDto;
import com.ll.gooHaeYu.domain.chat.room.dto.RoomListDto;
import com.ll.gooHaeYu.domain.chat.room.service.RoomService;
import com.ll.gooHaeYu.global.rsData.RsData;
import com.ll.gooHaeYu.global.security.MemberDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@Tag(name = "Chatting", description = "채팅 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/chat")
public class RoomController {
    private final RoomService roomService;

    @PostMapping
    @Operation(summary = "채팅방 생서")
    public RsData<URI> createRoom(@AuthenticationPrincipal MemberDetails memberDetails,
                                  @Valid @RequestBody CreateForm createForm) {
        Long roomId = roomService.createRoom(memberDetails.getId(),createForm.getMemberId());
        return RsData.of("201","CREATE",URI.create("/api/chat/" + roomId));
    }

    @GetMapping("/{roomId}")
    @Operation(summary = "채팅방 입장")
    public RsData<RoomDto> showRoom(@AuthenticationPrincipal MemberDetails memberDetails,
                                    @PathVariable Long roomId) {
        return RsData.of(roomService.findById(roomId));
    }

    @GetMapping
    @Operation(summary = "채팅방 목록")
    public RsData<List<RoomListDto>> showRoomList(@AuthenticationPrincipal MemberDetails memberDetails) {
        return RsData.of(roomService.getRoomList(memberDetails.getUsername()));
    }
}
