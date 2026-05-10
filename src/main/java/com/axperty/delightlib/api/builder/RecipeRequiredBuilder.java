package com.axperty.delightlib.api.builder;

import net.minecraft.world.level.block.Block;

import java.util.function.Consumer;
import java.util.function.Supplier;

public abstract class RecipeRequiredBuilder<T extends RecipeRequiredBuilder<T>> {
    protected Consumer<ShapedRecipeBuilder> recipeConfig = null;

    /**
     * Returns 'this' cast to the correct builder type. This is used to allow method chaining in the builder pattern while maintaining the correct return types.
     *
     * @return The current builder instance, cast to the correct type.
     */
    protected abstract T self();

    /**
     * Sets the recipe configuration for this block. This method must be called before build(), otherwise an exception will be thrown.
     *
     * @param recipeConfig A Consumer that takes a ShapedRecipeBuilder and configures it with the desired recipe. This will be called during the build process to create the recipe for this block.
     * @return The current builder instance, for method chaining.
     */
    public T recipe(Consumer<ShapedRecipeBuilder> recipeConfig) {
        this.recipeConfig = recipeConfig;
        return self();
    }

    /**
     * Builds the block and registers it with the addon. This method will first validate that all required information has been provided (such as the recipe configuration), and then call the abstract doBuild() method to perform the actual building and registration of the block. The doBuild() method must be implemented by subclasses to define how the block is created and registered.
     *
     * @return A Supplier that provides the built Block instance. This allows for lazy initialization of the block, which can be important for certain registration timing requirements in Minecraft modding.
     */
    public final Supplier<Block> build() {
        validate();
        return doBuild();
    }

    /**
     * Validates that all required information has been provided before building the block. In this base class, it checks that the recipeConfig is not null, since a shaped recipe is required for these types of blocks. If the validation fails, it throws an IllegalStateException with a message indicating what is missing. Subclasses can override this method to add additional validation checks if needed.
     */
    protected void validate() {
        if (recipeConfig == null) {
            throw new IllegalStateException("This block requires a shaped recipe. Call .recipe() before .build().");
        }
    }

    /**
     * Performs the actual building and registration of the block. This method is abstract and must be implemented by subclasses to define how the block is created, registered with the addon, and how the recipe is set up. The implementation should return a Supplier that provides the built Block instance, allowing for lazy initialization.
     *
     * @return A Supplier that provides the built Block instance.
     */
    protected abstract Supplier<Block> doBuild();
}
