package com.ll.goohaeyou.domain.member.member.entity.type;

import lombok.Getter;

@Getter
public enum Role {
    ADMIN, // 관리자
    USER,  // 회원
    GUEST  // 비회원
}
