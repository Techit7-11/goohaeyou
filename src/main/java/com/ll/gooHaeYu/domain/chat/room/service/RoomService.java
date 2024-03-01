package com.ll.gooHaeYu.domain.chat.room.service;

import com.ll.gooHaeYu.domain.chat.room.dto.RoomDto;
import com.ll.gooHaeYu.domain.chat.room.dto.RoomListDto;
import com.ll.gooHaeYu.domain.chat.room.entity.Room;
import com.ll.gooHaeYu.domain.chat.room.repository.RoomRepository;
import com.ll.gooHaeYu.domain.jobPost.jobPost.dto.JobPostDetailDto;
import com.ll.gooHaeYu.domain.jobPost.jobPost.entity.JobPost;
import com.ll.gooHaeYu.domain.jobPost.jobPost.entity.JobPostDetail;
import com.ll.gooHaeYu.domain.member.member.entity.Member;
import com.ll.gooHaeYu.domain.member.member.service.MemberService;
import com.ll.gooHaeYu.global.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.ll.gooHaeYu.global.exception.ErrorCode.POST_NOT_EXIST;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RoomService {
    private final RoomRepository roomRepository;
    private final MemberService memberService;

    @Transactional
    public Long createRoom(Long memberId1, Long memberId2) {
        Member member1 = memberService.findById(memberId1);
        Member member2 = memberService.findById(memberId2);

        Room newRoom = Room.builder()
                .username1(member1.getUsername())
                .username2(member2.getUsername())
                .build();

        roomRepository.save(newRoom);

        return newRoom.getId();
    }

    public RoomDto findById(Long roomId) {
        return RoomDto.fromEntity(findByIdAndValidate(roomId));
    }

    public Room findByIdAndValidate(Long roomId) {
        return roomRepository.findById(roomId)
                .orElseThrow(() -> new CustomException(POST_NOT_EXIST));
    }

    public List<RoomListDto> getRoomList(String username) {
        List<Room> rooms = roomRepository.findByUsername1OrUsername2(username);
        return RoomListDto.toDtoList(rooms);
    }
}
