package com.axperty.delightlib.api.builder;

import com.axperty.delightlib.api.DelightAddon;
import com.axperty.delightlib.internal.DelightCabinetBlock;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import vectorwing.farmersdelight.common.item.FuelBlockItem;

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

    @Override
    protected CabinetBuilder self() {
        return this;
    }

    /**
     * Sets the sound type of this cabinet. If not set, it will use the default sound type of a barrel. Setting this to null will also use the default sound type.
     *
     * @param soundType the sound type to use for this cabinet, or null to use the default sound type
     * @return this builder for chaining
     */
    public CabinetBuilder soundType(SoundType soundType) {
        this.soundType = soundType;
        return this;
    }

    /**
     * Sets the burn time of this cabinet when used as fuel. If not set, it will default to 300 ticks (15 seconds). Setting this to 0 or a negative value will make it not burnable.
     *
     * @param burnTime the burn time in ticks when used as fuel, or 0 or negative to make it not burnable
     * @return this builder for chaining
     */
    public CabinetBuilder burnTime(int burnTime) {
        this.burnTime = burnTime;
        return this;
    }

    @Override
    protected Supplier<Block> doBuild() {
        addon.trackCabinet(name);
        final SoundType sound = soundType;
        final int fuel = burnTime;

        Supplier<Block> block = addon.registerBlock(name, () -> {
            Block.Properties props = Block.Properties.ofFullCopy(Blocks.BARREL);
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
