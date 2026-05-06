package com.axperty.delightlib.api;

public record PlaceableFoodInfo(String name, FoodType type) {
    public enum FoodType {
        PIE, FEAST
    }
}
