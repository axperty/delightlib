package com.axperty.delightlib.api.builder;

import com.axperty.delightlib.api.DelightAddon;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

public class ShapelessRecipeBuilder {
    private final DelightAddon addon;
    private final String name;
    private final JsonArray ingredients = new JsonArray();
    private String resultId;
    private int resultCount = 1;
    private String group = "";
    private String category = "misc";

    public ShapelessRecipeBuilder(DelightAddon addon, String name) {
        this.addon = addon;
        this.name = name;
    }

    /**
     * Adds an ingredient to the recipe. The itemId should be in the format "modid:itemname". For example, if you call addIngredient("minecraft:iron_ingot"), then the resulting recipe will require an iron ingot as one of the ingredients.
     *
     * @param itemId the ID of the item to add as an ingredient, in the format "modid:itemname"
     * @return this builder for chaining
     */
    public ShapelessRecipeBuilder addIngredient(String itemId) {
        JsonObject ing = new JsonObject();
        ing.addProperty("item", itemId);
        ingredients.add(ing);
        return this;
    }

    /**
     * Adds an ingredient to the recipe using a tag. The tag should be in the format "modid:tagname". For example, if you call addTagIngredient("minecraft:ingots/iron"), then the resulting recipe will require any item that is tagged as an iron ingot as one of the ingredients.
     *
     * @param tag the tag to add as an ingredient, in the format "modid:tagname"
     * @return this builder for chaining
     */
    public ShapelessRecipeBuilder addTagIngredient(String tag) {
        JsonObject ing = new JsonObject();
        ing.addProperty("tag", tag);
        ingredients.add(ing);
        return this;
    }

    /**
     * Sets the result of the recipe. The itemId should be in the format "modid:itemname". For example, if you call result("minecraft:iron_sword"), then the resulting recipe will produce an iron sword when crafted.
     *
     * @param itemId the ID of the item to set as the result, in the format "modid:itemname"
     * @return this builder for chaining
     */
    public ShapelessRecipeBuilder result(String itemId) {
        this.resultId = itemId;
        return this;
    }

    /**
     * Sets the result of the recipe with a count. The itemId should be in the format "modid:itemname". For example, if you call result("minecraft:iron_sword", 2), then the resulting recipe will produce 2 iron swords when crafted.
     *
     * @param itemId the ID of the item to set as the result, in the format "modid:itemname"
     * @param count  the count of the result item to produce. For example, if you call result("minecraft:iron_sword", 2), then the resulting recipe will produce 2 iron swords when crafted.
     * @return
     */
    public ShapelessRecipeBuilder result(String itemId, int count) {
        this.resultId = itemId;
        this.resultCount = count;
        return this;
    }

    /**
     * Sets the group of the recipe. Recipes with the same group will be grouped together in the recipe book. For example, if you have multiple recipes that are all part of a "tools" group, then they will be displayed together in the recipe book under that group. The group is optional and can be left empty or null if you don't want to use it.
     *
     * @param group the group to set for this recipe. Recipes with the same group will be grouped together in the recipe book. For example, if you have multiple recipes that are all part of a "tools" group, then they will be displayed together in the recipe book under that group. The group is optional and can be left empty or null if you don't want to use it.
     * @return this builder for chaining
     */
    public ShapelessRecipeBuilder group(String group) {
        this.group = group;
        return this;
    }

    /**
     * Sets the category of the recipe book tab that this recipe will appear in. The category should be a valid recipe book category such as "building_blocks", "redstone", "tools", "combat", "food", etc. If not set, it will default to "misc". For example, if you set the category to "tools", then this recipe will appear in the tools tab of the recipe book.
     *
     * @param category the category of the recipe book tab that this recipe will appear in. The category should be a valid recipe book category such as "building_blocks", "redstone", "tools", "combat", "food", etc. If not set, it will default to "misc". For example, if you set the category to "tools", then this recipe will appear in the tools tab of the recipe book.
     * @return this builder for chaining
     */
    public ShapelessRecipeBuilder category(String category) {
        this.category = category;
        return this;
    }

    /**
     * Builds the recipe and adds it to the addon. This should be called after setting all the desired properties of the recipe. It will validate that a result and at least one ingredient have been set, and then it will create the JSON object for the recipe and add it to the addon with the specified name.
     */
    public void build() {
        if (resultId == null) throw new IllegalStateException("ShapelessRecipeBuilder '" + name + "' needs a result");
        if (ingredients.isEmpty())
            throw new IllegalStateException("ShapelessRecipeBuilder '" + name + "' needs ingredients");

        JsonObject recipe = new JsonObject();
        recipe.addProperty("type", "minecraft:crafting_shapeless");
        recipe.addProperty("category", category);
        if (group != null && !group.isEmpty()) recipe.addProperty("group", group);
        recipe.add("ingredients", ingredients);

        JsonObject result = new JsonObject();
        result.addProperty("count", resultCount);
        result.addProperty("id", resultId);
        recipe.add("result", result);

        addon.addRecipe(name, recipe);
    }
}
