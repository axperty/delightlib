package com.axperty.delightlib.api.builder;

import net.minecraft.world.level.block.Block;

import java.util.function.Consumer;
import java.util.function.Supplier;

public abstract class RecipeRequiredBuilder<T extends RecipeRequiredBuilder<T>> {
    protected Consumer<ShapedRecipeBuilder> recipeConfig = null;

    protected abstract T self();

    public T recipe(Consumer<ShapedRecipeBuilder> recipeConfig) {
        this.recipeConfig = recipeConfig;
        return self();
    }

    public final Supplier<Block> build() {
        validate();
        return doBuild();
    }

    protected void validate() {
        if (recipeConfig == null) {
            throw new IllegalStateException("This block requires a shaped recipe. Call .recipe() before .build().");
        }
    }

    protected abstract Supplier<Block> doBuild();
}
