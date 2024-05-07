package com.ll.gooHaeYu.standard.base;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum KwType {
    ALL("all"),
    TITLE("title"),
    BODY("body"),
    AUTHOR("author"),
    LOCATION("location"),
    EMPLOYED("employed");

    private final String value;
}
