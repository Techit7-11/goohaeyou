package com.ll.gooHaeYu.domain.member.member.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@AllArgsConstructor
public class AddUserRequest {
    private String username;
    private String password;
}
