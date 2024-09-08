package com.ll.goohaeyou.member.emailAuth.domain;

public interface AuthCodeStorage {
    String getData(String key);
    void setData(String key, String value);
    void setDataExpire(String key, String value, long duration);
    void deleteData(String key);
}
