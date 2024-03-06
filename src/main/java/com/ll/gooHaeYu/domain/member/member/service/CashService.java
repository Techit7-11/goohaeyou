package com.ll.gooHaeYu.domain.member.member.service;

import com.ll.gooHaeYu.domain.member.member.entity.Member;
import com.ll.gooHaeYu.domain.member.member.repository.MemberRepository;
import com.ll.gooHaeYu.global.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.ll.gooHaeYu.global.exception.ErrorCode.INSUFFICIENT_BALANCE;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CashService {
    private final MemberService memberService;
    private final MemberRepository memberRepository;

    @Transactional
    public long increaseCash(String username, long amount) {
        Member member = memberService.getMember(username);
        member.updateRestCash(member.getRestCash() + amount);
        memberRepository.save(member);

        return member.getRestCash();
    }

    @Transactional
    public long decreaseCash(String username, long amount) {
        Member member = memberService.getMember(username);

        checkEnoughBalance(member.getRestCash(), amount);

        member.updateRestCash(member.getRestCash() - amount);
        memberRepository.save(member);

        return member.getRestCash();
    }

    private void checkEnoughBalance(long currentCash, long amount) {
        if (currentCash < amount) {
            throw new CustomException(INSUFFICIENT_BALANCE);
        }
    }
}
