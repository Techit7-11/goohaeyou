package com.ll.goohaeyou.member.member.domain.type;

import lombok.Getter;

@Getter
public enum Role {
    ADMIN, // 관리자
    USER,  // 회원
    GUEST  // 비회원
}
