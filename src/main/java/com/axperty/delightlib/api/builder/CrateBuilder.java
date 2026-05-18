package com.axperty.delightlib.api.builder;

import com.axperty.delightlib.api.DelightAddon;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;

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