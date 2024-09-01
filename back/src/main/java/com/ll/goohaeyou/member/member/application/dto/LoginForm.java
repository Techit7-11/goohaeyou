package com.ll.goohaeyou.member.member.application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LoginForm {
    @NotBlank(message = "아이디는 필수 항목입니다.")
    @Size(min = 4, max = 20, message = "아이디는 최소 4자 ~ 최대 20자 이내로 입력해주세요.")
    private String username;

    @NotBlank(message = "비밀번호는 필수 항목입니다.")
    @Size(min = 8, max = 20, message = "비밀번호는 최소 8자 ~ 최대 20자 이내로 입력해주세요.")
    private String password;
}
