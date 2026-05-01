package com.axperty.delightlib.api.builder;

import com.axperty.delightlib.api.DelightAddon;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class BagBuilder {
    private final DelightAddon addon;
    private final String name;
    private Consumer<ShapedRecipeBuilder> recipeConfig = null;

    public BagBuilder(DelightAddon addon, String name) {
        this.addon = addon;
        this.name = name;
    }

    public BagBuilder recipe(Consumer<ShapedRecipeBuilder> recipeConfig) {
        this.recipeConfig = recipeConfig;
        return this;
    }

    public Supplier<Block> build() {
        if (recipeConfig == null) {
            throw new IllegalStateException("Bag '" + name + "' requires a shaped recipe. Call .recipe() before .build().");
        }
        addon.trackBag(name);

        Supplier<Block> block = addon.registerBlock(name, () ->
                new Block(BlockBehaviour.Properties.of().sound(SoundType.WOOL).strength(0.8f)));

        addon.registerItem(name, () -> new BlockItem(block.get(), new Item.Properties()));

        ShapedRecipeBuilder rb = addon.shapedRecipe(name);
        recipeConfig.accept(rb);
        rb.result(addon.getModId() + ":" + name).build();

        return block;
    }
}
