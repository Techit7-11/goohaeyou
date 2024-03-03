package com.ll.gooHaeYu.domain.member.member.entity.type;

public enum Level {

    LV1(10),
    LV2(30),
    LV3(60),
    LV4(100),
    LV5(Integer.MAX_VALUE);

    private final int maxTransactionCount;

    Level(int maxTransactionCount) {
        this.maxTransactionCount = maxTransactionCount;
    }

    public static Level getLevelByTransactionCount(int count) {
        for (Level level : Level.values()) {
            if (count <= level.maxTransactionCount) {
                return level;
            }
        }
        return LV5;
    }
}
