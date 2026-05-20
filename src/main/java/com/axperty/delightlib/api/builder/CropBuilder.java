package com.axperty.delightlib.api.builder;

import com.axperty.delightlib.api.DelightAddon;
import com.axperty.delightlib.internal.DelightCropBlock;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.component.Consumables;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

import java.util.concurrent.atomic.AtomicReference;
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

    /**
     * Makes the crop's item form edible, with the given nutrition and saturation values.
     *
     * @param nutrition  the amount of hunger points restored by the crop item
     * @param saturation the saturation modifier of the crop item
     * @return this builder for chaining
     */
    public CropBuilder asFood(int nutrition, float saturation) {
        this.isFood = true;
        this.nutrition = nutrition;
        this.saturation = saturation;
        return this;
    }

    /**
     * Makes the crop's seed item the same as the crop item. This means that when you break the crop block, it will drop the crop item instead of a separate seed item. This is useful for crops that don't have a separate seed item and are planted using the crop item itself, like carrots or potatoes.
     *
     * @return this builder for chaining
     */
    public CropBuilder seedIsItem() {
        this.seedIsItem = true;
        return this;
    }

    public Supplier<Item> build() {
        String seedName = seedIsItem ? name : name + "_seeds";
        String blockName = name + "_crop";

        AtomicReference<Supplier<Item>> seedHolder = new AtomicReference<>();

        Supplier<Block> cropBlock = addon.registerBlock(blockName, () ->
                new DelightCropBlock(Block.Properties.ofFullCopy(Blocks.WHEAT), () -> seedHolder.get().get()));
        addon.addCutoutBlock(cropBlock);

        Supplier<Item> cropItem;
        Item.Properties properties = new Item.Properties();
        if (isFood) {
            properties.food(new FoodProperties.Builder()
                    .nutrition(nutrition)
                    .saturationModifier(saturation)
                    .build());
            properties.component(DataComponents.CONSUMABLE, Consumables.defaultFood().build());
        }

        if (seedIsItem) {
            cropItem = addon.registerItem(name, () -> new BlockItem(cropBlock.get(), properties));
            seedHolder.set(cropItem);
        } else {
            cropItem = addon.registerItem(name, () -> new Item(properties));
            seedHolder.set(addon.registerItem(seedName, () -> new BlockItem(cropBlock.get(), new Item.Properties())));
        }

        addon.trackCrop(name, seedName, blockName, seedIsItem);
        return cropItem;
    }
}
