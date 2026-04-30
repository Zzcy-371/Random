package com.random.app.enums;

public enum FeedbackRating {
    NOT_SATISFIED(1),
    NEUTRAL(2),
    SATISFIED(3);

    private final int value;

    FeedbackRating(int value) {
        this.value = value;
    }

    public int getValue() { return value; }

    public static FeedbackRating fromValue(int value) {
        for (FeedbackRating r : values()) {
            if (r.value == value) return r;
        }
        throw new IllegalArgumentException("Invalid feedback rating: " + value);
    }
}
