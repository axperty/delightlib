package com.axperty.delightlib.api;

import net.minecraft.world.item.Item;
import java.util.function.Supplier;

public record PlaceableFoodInfo(
        String name,
        FoodType type,
        Supplier<Item> sliceItem,
        Supplier<Item> servingItem,
        Supplier<Item> feastOutputItem
) {
    public enum FoodType {
        PIE, FEAST
    }
}