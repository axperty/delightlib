package com.axperty.delightlib.api.builder;

import com.axperty.delightlib.api.DelightAddon;
import com.axperty.delightlib.internal.DelightCropBlock;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemNameBlockItem;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

import java.util.function.Supplier;

public class CropBuilder {
    private final DelightAddon addon;
    private final String name;
    private boolean isFood = false;
    private int nutrition = 0;
    private float saturation = 0;
    private boolean seedIsItem = false;

    public CropBuilder(DelightAddon addon, String name) {
        this.addon = addon;
        this.name = name;
    }

    public CropBuilder asFood(int nutrition, float saturation) {
        this.isFood = true;
        this.nutrition = nutrition;
        this.saturation = saturation;
        return this;
    }

    public CropBuilder seedIsItem() {
        this.seedIsItem = true;
        return this;
    }

    public Supplier<Item> build() {
        String seedName = seedIsItem ? name : name + "_seeds";
        String blockName = name + "_crop";

        final Supplier<Item>[] seedHolder = new Supplier[1];

        Supplier<Block> cropBlock = addon.registerBlock(blockName, () ->
                new DelightCropBlock(Block.Properties.ofFullCopy(Blocks.WHEAT), seedHolder[0]));

        Supplier<Item> cropItem;
        if (isFood) {
            FoodProperties food = new FoodProperties.Builder()
                    .nutrition(nutrition).saturationModifier(saturation).build();
            if (seedIsItem) {
                cropItem = addon.registerItem(name, () ->
                        new ItemNameBlockItem(cropBlock.get(), new Item.Properties().food(food)));
                seedHolder[0] = cropItem;
            } else {
                cropItem = addon.registerItem(name, () -> new Item(new Item.Properties().food(food)));
                seedHolder[0] = addon.registerItem(seedName, () ->
                        new ItemNameBlockItem(cropBlock.get(), new Item.Properties()));
            }
        } else {
            if (seedIsItem) {
                cropItem = addon.registerItem(name, () ->
                        new ItemNameBlockItem(cropBlock.get(), new Item.Properties()));
                seedHolder[0] = cropItem;
            } else {
                cropItem = addon.registerItem(name, () -> new Item(new Item.Properties()));
                seedHolder[0] = addon.registerItem(seedName, () ->
                        new ItemNameBlockItem(cropBlock.get(), new Item.Properties()));
            }
        }

        addon.trackCrop(name, seedName, blockName, seedIsItem);
        return cropItem;
    }
}
