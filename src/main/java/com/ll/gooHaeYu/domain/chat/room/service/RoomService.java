package com.ll.gooHaeYu.domain.chat.room.service;

import com.ll.gooHaeYu.domain.chat.room.entity.Room;
import com.ll.gooHaeYu.domain.chat.room.repository.RoomRepository;
import com.ll.gooHaeYu.domain.member.member.entity.Member;
import com.ll.gooHaeYu.domain.member.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RoomService {
    private final RoomRepository roomRepository;
    private final MemberService memberService;

    public Long createRoom(Long memberId1, Long memberId2) {
        Member member1 = memberService.findById(memberId1);
        Member member2 = memberService.findById(memberId2);

        Room newRoom = Room.builder()
                .member1(member1)
                .member2(member2)
                .build();

        roomRepository.save(newRoom);

        return newRoom.getId();
    }
}
