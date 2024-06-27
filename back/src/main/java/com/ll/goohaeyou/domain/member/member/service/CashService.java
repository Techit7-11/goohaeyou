package com.ll.goohaeyou.domain.member.member.service;

import com.ll.goohaeyou.domain.member.member.entity.Member;
import com.ll.goohaeyou.domain.member.member.entity.repository.MemberRepository;
import com.ll.goohaeyou.global.exception.payment.PaymentException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
            throw new PaymentException.InsufficientBalance();
        }
    }
}