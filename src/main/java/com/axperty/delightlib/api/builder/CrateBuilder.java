package com.axperty.delightlib.api.builder;

import com.axperty.delightlib.api.DelightAddon;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class CrateBuilder extends RecipeRequiredBuilder<CrateBuilder> {
    private final DelightAddon addon;
    private final String name;

    public CrateBuilder(DelightAddon addon, String name) {
        this.addon = addon;
        this.name = name;
    }

    @Override
    protected CrateBuilder self() {
        return this;
    }

    @Override
    protected Supplier<Block> doBuild() {
        if (recipeConfig == null) {
            throw new IllegalStateException("Crate '" + name + "' requires a shaped recipe. Call .recipe() before .build().");
        }
        addon.trackCrate(name);

        Supplier<Block> block = addon.registerBlock(name, () ->
                new Block(BlockBehaviour.Properties.of().sound(SoundType.WOOD).strength(2.0f, 3.0f)));

        addon.registerItem(name, () -> new BlockItem(block.get(), new Item.Properties()));

        ShapedRecipeBuilder rb = addon.shapedRecipe(name);
        recipeConfig.accept(rb);
        rb.result(addon.getModId() + ":" + name).build();

        return block;
    }
}