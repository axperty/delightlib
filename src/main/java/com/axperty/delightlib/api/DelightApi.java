package com.axperty.delightlib.api;

import com.axperty.delightlib.api.builder.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tiers;

import java.util.function.Supplier;

public interface DelightApi {
    /**
     * Creates a new instance of the Delight API for your mod. You should call this method in your mod's constructor or common setup method to initialize the API and start registering your content. The mod ID you provide will be used as a namespace for all registry entries created through this API, so make sure it is unique and does not conflict with other mods.
     *
     * @param modId       the mod ID to use as a namespace for all registry entries created through this API. This should be the same mod ID that you use in your mod's entrypoint and build configuration, and it should be unique to avoid conflicts with other mods.
     * @return a new instance of the Delight API that you can use to register your content. You should only create one instance of the API per mod, and you can store it in a static field for easy access throughout your mod's code.
     */
    static DelightApi create(String modId) {
        return DelightAddon.internalCreate(modId);
    }

    /**
     * Sets the creative tab for all items and blocks registered through this API. This is optional, but it can be useful for organizing your content in the creative inventory. If not set, your items and blocks will appear in the miscellaneous tab.
     *
     * @param title the title of the creative tab to use for all items and blocks registered through this API. This can be any string and does not have to be unique, as it will be combined with the mod ID to create a unique identifier for the creative tab.
     * @param icon  a supplier that provides the icon for the creative tab. This should return an ItemStack that represents the icon you want to use for the creative tab. It can be an item from your mod or any other item in the game.
     * @return this API instance for chaining. Note that you should call this method before registering any items or blocks, as it will set the creative tab for all content registered through this API. If you call it after registering some content, only the content registered after the call will be affected.
     */
    DelightApi withCreativeTab(String title, Supplier<ItemStack> icon);

    // Builder factories

    /**
     * Starts building a new knife item. Knives are used for slicing placeable foods and can also be used as a weapon if given appropriate stats.
     *
     * @param name the name of the knife item, in snake_case. This will be used as the registry name and should be unique within this mod.
     * @param tier the tier of the knife, which determines its durability and mining level. This is required for knives to function properly in slicing placeable foods, but you can use a custom tier with 0 mining level and durability if you just want a weapon-like item without the slicing functionality.
     * @return a KnifeBuilder to further configure and build the knife item
     */
    KnifeBuilder knife(String name, Tiers tier);

    /**
     * Starts building a new food item. This is a simple item that can be eaten by the player, and can also be used as an ingredient in placeable foods and cooking recipes. It does not have any special functionality on its own, but it can be referenced by other builders to create more complex content.
     *
     * @param name the name of the food item, in snake_case. This will be used as the registry name and should be unique within this mod.
     * @return a FoodBuilder to further configure and build the food item
     */
    FoodBuilder food(String name);

    /**
     * Starts building a new placeable food block. Placeable foods are blocks that can be placed in the world and interacted with to consume them. They can be defined as either pies or feasts, which determines how they are sliced and what items they give when consumed. Placeable foods also have special interactions with knives, which can be used to slice them and get different items depending on the type of placeable food.
     *
     * @param name the name of the placeable food, in snake_case. This will be used as the registry name for both the block and the item form of this placeable food, and should be unique within this mod.
     * @return a PlaceableFoodBuilder to further configure and build the placeable food block and its corresponding item
     */
    PlaceableFoodBuilder placeableFood(String name);


    /**
     * Starts building a new cooking recipe. Cooking recipes are used in furnaces, smokers, and campfires to cook food items. They can have a result item, an optional container item that is returned when the recipe is crafted, experience given to the player, cooking time, and a recipe book tab for organization.
     *
     * @param name the name of the recipe, in snake_case. This will be used as the filename for the generated JSON recipe file and should be unique within this mod.
     * @return a CookingRecipeBuilder to further configure and build the cooking recipe
     */
    CookingRecipeBuilder cookingRecipe(String name);

    /**
     * Starts building a new shaped crafting recipe. Shaped recipes are the standard type of crafting recipe that require a specific arrangement of ingredients in the crafting grid. They can have a result item, a group for recipe book organization, and a recipe book category.
     *
     * @param name the name of the recipe, in snake_case. This will be used as the filename for the generated JSON recipe file and should be unique within this mod.
     * @return a ShapedRecipeBuilder to further configure and build the shaped crafting recipe
     */
    ShapedRecipeBuilder shapedRecipe(String name);

    /**
     * Starts building a new shapeless crafting recipe. Shapeless recipes are crafting recipes that do not require a specific arrangement of ingredients in the crafting grid. They can have a result item, a group for recipe book organization, and a recipe book category.
     *
     * @param name the name of the recipe, in snake_case. This will be used as the filename for the generated JSON recipe file and should be unique within this mod.
     * @return a ShapelessRecipeBuilder to further configure and build the shapeless crafting recipe
     */
    ShapelessRecipeBuilder shapelessRecipe(String name);

    /**
     * Starts building a new cabinet block. Cabinets are storage blocks that can hold items and have a built-in inventory GUI. They can also be used as fuel if given a burn time, and they have special interactions with knives and placeable foods, allowing players to store sliced pieces of placeable foods inside them.
     *
     * @param name the name of the cabinet, in snake_case. This will be used as the registry name for both the block and the item form of this cabinet, and should be unique within this mod.
     * @return a CabinetBuilder to further configure and build the cabinet block and its corresponding item
     */
    CabinetBuilder cabinet(String name);

    /**
     * Starts building a new Crate block. Crates are simple storage blocks that can hold items but do not have a built-in inventory GUI like cabinets. They can be used for storing items in the world and can also be used as fuel if given a burn time.
     *
     * @param name the name of the crate, in snake_case. This will be used as the registry name for both the block and the item form of this crate, and should be unique within this mod.
     * @return a CrateBuilder to further configure and build the crate block and its corresponding item
     */
    CrateBuilder crate(String name);

    /**
     * Starts building a new Bag block. Bags are wearable storage items that can hold items and be equipped in the chest slot. They can also be used as fuel if given a burn time, and they have special interactions with cabinets, allowing players to store them inside cabinets for easy access.
     *
     * @param name the name of the bag, in snake_case. This will be used as the registry name for both the block and the item form of this bag, and should be unique within this mod.
     * @return a BagBuilder to further configure and build the bag block and its corresponding item
     */
    BagBuilder bag(String name);

    /**
     * Starts building a new Crop. Crops are a special type of content that consist of a seed item, a crop block, and an optional crop item that can be obtained by slicing the crop block with a knife. Crops can be grown in farmland and harvested for their seeds and crop items, and they can also be used as ingredients in placeable foods and cooking recipes.
     *
     * @param name the name of the crop, in snake_case. This will be used as the registry name for the seed item (if the seed is an item), the crop block, and the crop item (if it exists), and should be unique within this mod.
     * @return a CropBuilder to further configure and build the crop's seed item, crop block, and crop item
     */
    CropBuilder crop(String name);

    /**
     * Finalizes the addon registration process. In Fabric, this method must be called at the end of your registration block to register the creative tab, block entities (like cabinets), and other complex content that relies on all items and blocks being built first.
     */
    void build();
}