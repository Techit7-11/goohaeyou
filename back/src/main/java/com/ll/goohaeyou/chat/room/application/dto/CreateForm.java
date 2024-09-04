package com.ll.goohaeyou.chat.room.application.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateForm {
    @NotBlank
    private Long memberId;
}
