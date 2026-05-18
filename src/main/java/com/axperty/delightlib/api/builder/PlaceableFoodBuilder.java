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

    /**
     * Defines this placeable food as a pie, with the given slice item. The slice item is what players will get when they take a slice from the placed block.
     *
     * @param sliceItem the item that will be given when taking a slice from the placed block
     * @return this builder for chaining
     */
    public PlaceableFoodBuilder pie(Supplier<Item> sliceItem) {
        foodType = PlaceableFoodInfo.FoodType.PIE;
        this.sliceItem = sliceItem;
        return this;
    }

    /**
     * Defines this placeable food as a pie, with the given slice item. The slice item is what players will get when they take a slice from the placed block.
     *
     * @param sliceItemName the name of the item that will be given when taking a slice from the placed block
     * @return this builder for chaining
     */
    public PlaceableFoodBuilder pie(String sliceItemName) {
        return pie(addon.getItem(sliceItemName));
    }

    /**
     * Defines this placeable food as a pie, with the given slice item. The slice item is what players will get when they take a slice from the placed block.
     *
     * @param foodBuilder a FoodBuilder that will be built to get the slice item. This is a convenience method to avoid having to call build() on the FoodBuilder separately.
     * @return this builder for chaining
     */
    public PlaceableFoodBuilder pie(FoodBuilder foodBuilder) {
        return pie(foodBuilder.build());
    }

    /**
     * Defines this placeable food as a feast, with the given serving item. The serving item is what players will get when they take a serving from the placed block.
     *
     * @param servingItem the item that will be given when taking a serving from the placed block
     * @return this builder for chaining
     */
    public PlaceableFoodBuilder feast(Supplier<Item> servingItem) {
        foodType = PlaceableFoodInfo.FoodType.FEAST;
        this.servingItem = servingItem;
        return this;
    }

    /**
     * Defines this placeable food as a feast, with the given serving item. The serving item is what players will get when they take a serving from the placed block.
     *
     * @param servingItemName the name of the item that will be given when taking a serving from the placed block
     * @return this builder for chaining
     */
    public PlaceableFoodBuilder feast(String servingItemName) {
        return feast(addon.getItem(servingItemName));
    }

    /**
     * Defines this placeable food as a feast, with the given serving item. The serving item is what players will get when they take a serving from the placed block.
     *
     * @param foodBuilder a FoodBuilder that will be built to get the serving item. This is a convenience method to avoid having to call build() on the FoodBuilder separately.
     * @return this builder for chaining
     */
    public PlaceableFoodBuilder feast(FoodBuilder foodBuilder) {
        return feast(foodBuilder.build());
    }

    /**
     * Defines the output item for this feast. This is the item that will be given when the placed block is fully consumed. This only applies to feasts, and will be ignored for pies.
     *
     * @param item the item that will be given when the placed block is fully consumed
     * @return this builder for chaining
     */
    public PlaceableFoodBuilder feastOutput(Supplier<Item> item) {
        this.feastOutputItem = item;
        return this;
    }

    /**
     * Defines the output item for this feast. This is the item that will be given when the placed block is fully consumed. This only applies to feasts, and will be ignored for pies.
     *
     * @param itemName the name of the item that will be given when the placed block is fully consumed
     * @return this builder for chaining
     */
    public PlaceableFoodBuilder feastOutput(String itemName) {
        return feastOutput(addon.getItem(itemName));
    }

    /**
     * Defines this placeable food for stacking. By default, placeable foods do not stack, but if you want to allow them to stack (for example, if they are not fully consumed when placed), you can set the max stack size with this method.
     *
     * @param size the max stack size for this placeable food. Must be at least 1.
     * @return this builder for chaining
     */
    public PlaceableFoodBuilder stacksTo(int size) {
        this.maxStackSize = size;
        return this;
    }

    /**
     * Builds the placeable food item and registers the corresponding block. The block will be registered with the same name as this placeable food, and the item will be registered with the same name as well. The block will have the same properties as a cake block, but with the appropriate behavior for either a pie or a feast depending on how this builder was configured. The item will be a BlockItem that places the corresponding block when used.
     *
     * @return a Supplier that provides the built Item instance for this placeable food. This allows for lazy initialization of the item, which can be important for certain registration timing requirements in Minecraft modding.
     */
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