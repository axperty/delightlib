package com.axperty.delightlib.api.builder;

import com.axperty.delightlib.api.DelightAddon;
import com.axperty.delightlib.api.PlaceableFoodInfo;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import vectorwing.farmersdelight.common.block.FeastBlock;
import vectorwing.farmersdelight.common.block.PieBlock;

import java.util.function.Supplier;

public class PlaceableFoodBuilder {
    private final DelightAddon addon;
    private final String name;
    private int maxStackSize = 1;
    private Supplier<Item> sliceItem;
    private Supplier<Item> servingItem;
    private Supplier<Item> feastOutputItem;
    private PlaceableFoodInfo.FoodType foodType = null;

    public PlaceableFoodBuilder(DelightAddon addon, String name) {
        this.addon = addon;
        this.name = name;
    }

    public PlaceableFoodBuilder pie(Supplier<Item> sliceItem) {
        foodType = PlaceableFoodInfo.FoodType.PIE;
        this.sliceItem = sliceItem;
        return this;
    }

    public PlaceableFoodBuilder pie(String sliceItemName) {
        return pie(addon.getItem(sliceItemName));
    }

    public PlaceableFoodBuilder pie(FoodBuilder foodBuilder) {
        return pie(foodBuilder.build());
    }

    public PlaceableFoodBuilder feast(Supplier<Item> servingItem) {
        foodType = PlaceableFoodInfo.FoodType.FEAST;
        this.servingItem = servingItem;
        return this;
    }

    public PlaceableFoodBuilder feast(String servingItemName) {
        return feast(addon.getItem(servingItemName));
    }

    public PlaceableFoodBuilder feast(FoodBuilder foodBuilder) {
        return feast(foodBuilder.build());
    }

    public PlaceableFoodBuilder feastOutput(Supplier<Item> item) {
        this.feastOutputItem = item;
        return this;
    }

    public PlaceableFoodBuilder feastOutput(String itemName) {
        return feastOutput(addon.getItem(itemName));
    }

    public PlaceableFoodBuilder stacksTo(int size) {
        this.maxStackSize = size;
        return this;
    }

    public Supplier<Item> build() {
        if (foodType == null) {
            throw new IllegalStateException("PlaceableFoodBuilder '" + name + "' must use .pie() or .feast()");
        }
        addon.trackPlaceableFood(name, foodType, sliceItem, servingItem, feastOutputItem);
        final int stack = maxStackSize;

        Supplier<Block> block = null;
        switch (foodType) {
            case PIE -> {
                final Supplier<Item> slice = sliceItem;
                block = addon.registerBlock(name, () -> new PieBlock(addon.defaultBlockProperties(name, Block.Properties.ofFullCopy(Blocks.CAKE)), slice));
                addon.addCutoutBlock(block);
            }
            case FEAST -> {
                final Supplier<Item> serving = servingItem;
                final Supplier<Item> output = feastOutputItem;
                block = addon.registerBlock(name, () -> new FeastBlock(addon.defaultBlockProperties(name, Block.Properties.ofFullCopy(Blocks.CAKE)), serving, output != null));
                addon.addCutoutBlock(block);
            }
        }
        Supplier<Block> finalBlock = block;

        return addon.registerItem(name, () -> new BlockItem(finalBlock.get(), addon.defaultItemProperties(name).stacksTo(stack)));
    }
}