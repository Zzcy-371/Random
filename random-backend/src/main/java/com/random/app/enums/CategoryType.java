package com.random.app.enums;

public enum CategoryType {
    EATING("eating", "吃"),
    DRINKING("drinking", "喝"),
    PLAYING("playing", "玩"),
    STAYING("staying", "住"),
    OTHER("other", "其他");

    private final String code;
    private final String displayName;

    CategoryType(String code, String displayName) {
        this.code = code;
        this.displayName = displayName;
    }

    public String getCode() { return code; }
    public String getDisplayName() { return displayName; }
}
