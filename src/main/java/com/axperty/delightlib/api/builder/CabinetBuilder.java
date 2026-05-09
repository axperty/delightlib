package com.axperty.delightlib.api.builder;

import com.axperty.delightlib.api.DelightAddon;
import com.axperty.delightlib.internal.DelightCabinetBlock;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import vectorwing.farmersdelight.common.item.FuelBlockItem;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class CabinetBuilder extends RecipeRequiredBuilder<CabinetBuilder> {
    private final DelightAddon addon;
    private final String name;
    private SoundType soundType = null;
    private int burnTime = 300;

    public CabinetBuilder(DelightAddon addon, String name) {
        this.addon = addon;
        this.name = name;
    }

    public CabinetBuilder soundType(SoundType soundType) { this.soundType = soundType; return this; }
    public CabinetBuilder burnTime(int burnTime) { this.burnTime = burnTime; return this; }
    @Override
    protected CabinetBuilder self() { return this; }

    @Override
    protected Supplier<Block> doBuild() {
        if (recipeConfig == null) {
            throw new IllegalStateException("Cabinet '" + name + "' requires a shaped recipe. Call .recipe() before .build().");
        }
        addon.trackCabinet(name);
        final SoundType sound = soundType;
        final int fuel = burnTime;

        Supplier<Block> block = addon.registerBlock(name, () -> {
            Block.Properties props = Block.Properties.copy(Blocks.BARREL);
            if (sound != null) props = props.sound(sound);
            return new DelightCabinetBlock(props);
        });

        addon.registerItem(name, () -> fuel > 0
                ? new FuelBlockItem(block.get(), new Item.Properties(), fuel)
                : new BlockItem(block.get(), new Item.Properties()));

        addon.addCabinetBlock(block);

        ShapedRecipeBuilder rb = addon.shapedRecipe(name);
        recipeConfig.accept(rb);
        rb.result(addon.getModId() + ":" + name).build();

        return block;
    }
}
