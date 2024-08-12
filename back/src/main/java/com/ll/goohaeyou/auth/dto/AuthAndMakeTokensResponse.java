package com.ll.goohaeyou.auth.dto;

import com.ll.goohaeyou.member.member.dto.MemberDto;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;

@Builder
@Getter
public class AuthAndMakeTokensResponse {
    @NonNull
    MemberDto memberDto;
    @NotNull
    String accessToken;
    @NotNull
    String refreshToken;
}
