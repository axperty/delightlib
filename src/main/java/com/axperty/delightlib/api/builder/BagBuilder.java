package com.axperty.delightlib.api.builder;

import com.axperty.delightlib.api.DelightAddon;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;

import java.util.function.Supplier;

public class BagBuilder extends RecipeRequiredBuilder<BagBuilder> {
    private final DelightAddon addon;
    private final String name;

    public BagBuilder(DelightAddon addon, String name) {
        this.addon = addon;
        this.name = name;
    }

    @Override
    protected BagBuilder self() {
        return this;
    }

    @Override
    protected Supplier<Block> doBuild() {
        if (recipeConfig == null) {
            throw new IllegalStateException("Bag '" + name + "' requires a shaped recipe. Call .recipe() before .build().");
        }
        addon.trackBag(name);

        Supplier<Block> block = addon.registerBlock(name, () ->
                new Block(addon.defaultBlockProperties(name, BlockBehaviour.Properties.of().sound(SoundType.WOOL).strength(0.8f))));

        addon.registerItem(name, () -> new BlockItem(block.get(), addon.defaultItemProperties(name)));

        ShapedRecipeBuilder rb = addon.shapedRecipe(name);
        recipeConfig.accept(rb);
        rb.result(addon.getModId() + ":" + name).build();

        return block;
    }
}