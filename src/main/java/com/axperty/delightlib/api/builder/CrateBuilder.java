package com.axperty.delightlib.api.builder;

import com.axperty.delightlib.api.DelightAddon;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class CrateBuilder {
    private final DelightAddon addon;
    private final String name;
    private Consumer<ShapedRecipeBuilder> recipeConfig = null;

    public CrateBuilder(DelightAddon addon, String name) {
        this.addon = addon;
        this.name = name;
    }

    public CrateBuilder recipe(Consumer<ShapedRecipeBuilder> recipeConfig) {
        this.recipeConfig = recipeConfig;
        return this;
    }

    public Supplier<Block> build() {
        if (recipeConfig == null) {
            throw new IllegalStateException("Crate '" + name + "' requires a shaped recipe. Call .recipe() before .build().");
        }
        addon.trackCrate(name);

        Supplier<Block> block = addon.registerBlock(name, () ->
                new Block(addon.defaultBlockProperties(name, BlockBehaviour.Properties.of().sound(SoundType.WOOD).strength(2.0f, 3.0f))));

        addon.registerItem(name, () -> new BlockItem(block.get(), addon.defaultItemProperties(name)));

        ShapedRecipeBuilder rb = addon.shapedRecipe(name);
        recipeConfig.accept(rb);
        rb.result(addon.getModId() + ":" + name).build();

        return block;
    }
}
