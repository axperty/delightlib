package com.axperty.delightlib.api.builder;

import com.axperty.delightlib.api.DelightAddon;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import vectorwing.farmersdelight.common.block.FeastBlock;
import vectorwing.farmersdelight.common.block.PieBlock;
import net.minecraft.world.item.BlockItem;

import java.util.function.Supplier;

public class PlaceableFoodBuilder {
    private final DelightAddon addon;
    private final String name;
    private int maxStackSize = 1;
    private boolean isPie = false;
    private Supplier<Item> sliceItem;
    private boolean isFeast = false;
    private Supplier<Item> servingItem;
    private boolean hasLeftovers = true;

    public PlaceableFoodBuilder(DelightAddon addon, String name) {
        this.addon = addon;
        this.name = name;
    }

    public PlaceableFoodBuilder asPie(Supplier<Item> sliceItem) {
        this.isPie = true; this.isFeast = false;
        this.sliceItem = sliceItem;
        return this;
    }

    public PlaceableFoodBuilder asPie(String sliceItemName) {
        return asPie(addon.getItem(sliceItemName));
    }

    public PlaceableFoodBuilder asFeast(Supplier<Item> servingItem, boolean hasLeftovers) {
        this.isFeast = true; this.isPie = false;
        this.servingItem = servingItem;
        this.hasLeftovers = hasLeftovers;
        return this;
    }

    public PlaceableFoodBuilder asFeast(String servingItemName, boolean hasLeftovers) {
        return asFeast(addon.getItem(servingItemName), hasLeftovers);
    }

    public PlaceableFoodBuilder stacksTo(int size) { this.maxStackSize = size; return this; }

    public Supplier<Item> build() {
        if (!isPie && !isFeast) {
            throw new IllegalStateException("PlaceableFoodBuilder '" + name + "' must use .asPie() or .asFeast()");
        }
        addon.trackPlaceableFood(name, isPie);
        final int stack = maxStackSize;

        Supplier<Block> block;
        if (isPie) {
            final Supplier<Item> slice = sliceItem;
            block = addon.registerBlock(name, () -> new PieBlock(Block.Properties.ofFullCopy(Blocks.CAKE), slice));
            addon.addCutoutBlock(block);
        } else {
            final Supplier<Item> serving = servingItem;
            final boolean leftovers = hasLeftovers;
            block = addon.registerBlock(name, () -> new FeastBlock(Block.Properties.ofFullCopy(Blocks.CAKE), serving, leftovers));
            addon.addCutoutBlock(block);
        }
        return addon.registerItem(name, () -> new BlockItem(block.get(), new Item.Properties().stacksTo(stack)));
    }
}
