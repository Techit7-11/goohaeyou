package com.ll.gooHaeYu.domain.member.member.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;

@Builder
@Getter
public class AuthAndMakeTokensResponse {
    @NonNull
    MemberDto memberDto;
    @NonNull
    String accessToken;

    String refreshToken;
}
