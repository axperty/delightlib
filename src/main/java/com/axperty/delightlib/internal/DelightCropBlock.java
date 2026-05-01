package com.axperty.delightlib.internal;

import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.CropBlock;

import java.util.function.Supplier;

public class DelightCropBlock extends CropBlock {
    private final Supplier<Item> seedItem;

    public DelightCropBlock(Properties props, Supplier<Item> seedItem) {
        super(props);
        this.seedItem = seedItem;
    }

    @Override
    protected ItemLike getBaseSeedId() {
        return seedItem.get();
    }
}
