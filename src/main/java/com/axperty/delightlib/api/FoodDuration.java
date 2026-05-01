package com.axperty.delightlib.api;

public enum FoodDuration {
    BRIEF(600),
    SHORT(1200),
    MEDIUM(3600),
    LONG(6000);

    private final int ticks;

    FoodDuration(int ticks) {
        this.ticks = ticks;
    }

    public int getTicks() {
        return ticks;
    }
}
