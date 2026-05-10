package com.axperty.delightlib.api.builder;

import com.axperty.delightlib.api.DelightAddon;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.LinkedHashMap;
import java.util.Map;

public class ShapedRecipeBuilder {
    private final DelightAddon addon;
    private final String name;
    private final Map<Character, String> keys = new LinkedHashMap<>();
    private final Map<Character, Boolean> keyIsTag = new LinkedHashMap<>();
    private String[] pattern;
    private String resultId;
    private int resultCount = 1;
    private String group = "";
    private String category = "misc";

    public ShapedRecipeBuilder(DelightAddon addon, String name) {
        this.addon = addon;
        this.name = name;
    }

    /**
     * Sets the crafting grid pattern for this shaped recipe. Each string in the array represents a row in the crafting grid, and each character represents a key that corresponds to an ingredient defined with define() or defineTag(). The grid can have 1 to 3 rows, and each row can have 1 to 3 characters.
     *
     * @param rows the pattern for the crafting grid, as an array of strings. Each string represents a row, and each character represents a key for an ingredient.
     * @return this builder for chaining
     */
    public ShapedRecipeBuilder grid(String... rows) {
        if (rows.length < 1 || rows.length > 3) throw new IllegalArgumentException("Grid must have 1-3 rows");
        for (String row : rows) {
            if (row.length() < 1 || row.length() > 3)
                throw new IllegalArgumentException("Row must be 1-3 chars: '" + row + "'");
        }
        this.pattern = rows;
        return this;
    }

    /**
     * Defines a key for the crafting grid pattern, associating a character with an ingredient. The itemId should be in the format "modid:itemname". For example, if you have a pattern like [" A ", " B ", " C "] and you call define('A', "minecraft:iron_ingot"), define('B', "minecraft:stick"), and define('C', "minecraft:iron_ingot"), then the resulting recipe will require iron ingots in the top and bottom middle slots and a stick in the center slot.
     *
     * @param key    the character to associate with the ingredient in the crafting grid pattern
     * @param itemId the ID of the item to use as the ingredient for this key, in the format "modid:itemname"
     * @return this builder for chaining
     */
    public ShapedRecipeBuilder define(char key, String itemId) {
        keys.put(key, itemId);
        keyIsTag.put(key, false);
        return this;
    }

    /**
     * Defines a key for the crafting grid pattern using a tag, associating a character with an ingredient tag. The tag should be in the format "modid:tagname". For example, if you have a pattern like [" A ", " B ", " C "] and you call defineTag('A', "minecraft:ingots/iron"), defineTag('B', "minecraft:sticks"), and defineTag('C', "minecraft:ingots/iron"), then the resulting recipe will require any item that is tagged as an iron ingot in the top and bottom middle slots and any item that is tagged as a stick in the center slot.
     *
     * @param key the character to associate with the ingredient in the crafting grid pattern
     * @param tag the tag to use as the ingredient for this key, in the format "modid:tagname"
     * @return this builder for chaining
     */
    public ShapedRecipeBuilder defineTag(char key, String tag) {
        keys.put(key, tag);
        keyIsTag.put(key, true);
        return this;
    }

    /**
     * Sets the result of the recipe. The itemId should be in the format "modid:itemname". For example, if you call result("minecraft:iron_sword"), then the resulting recipe will produce an iron sword when crafted.
     *
     * @param itemId the ID of the item to set as the result of the recipe, in the format "modid:itemname"
     * @return this builder for chaining
     */
    public ShapedRecipeBuilder result(String itemId) {
        this.resultId = itemId;
        return this;
    }

    /**
     * Sets the result of the recipe with a count. The itemId should be in the format "modid:itemname". For example, if you call result("minecraft:iron_sword", 2), then the resulting recipe will produce 2 iron swords when crafted.
     *
     * @param itemId the ID of the item to set as the result of the recipe, in the format "modid:itemname"
     * @param count  the count of the result item to produce when the recipe is crafted
     * @return
     */
    public ShapedRecipeBuilder result(String itemId, int count) {
        this.resultId = itemId;
        this.resultCount = count;
        return this;
    }

    /**
     * Sets the group of the recipe. Recipes with the same group will be grouped together in the recipe book. For example, if you have multiple recipes for different types of wooden planks and you set their group to "wooden_planks", then they will all be grouped together in the recipe book under that group.
     *
     * @param group the group to set for this recipe. Recipes with the same group will be grouped together in the recipe book.
     * @return this builder for chaining
     */
    public ShapedRecipeBuilder group(String group) {
        this.group = group;
        return this;
    }

    /**
     * Sets the category of the recipe. The category is used for sorting recipes in the recipe book and in the creative inventory. For example, if you set the category to "building", then this recipe will be sorted with other building-related recipes. The default category is "misc".
     *
     * @param category the category to set for this recipe. The category is used for sorting recipes in the recipe book and in the creative inventory. The default category is "misc".
     * @return this builder for chaining
     */
    public ShapedRecipeBuilder category(String category) {
        this.category = category;
        return this;
    }

    /**
     * Builds the recipe and registers it with the addon. This method will first validate that all required information has been provided (such as the grid pattern, result, and key definitions), and then it will construct a JsonObject representing the recipe in the format expected by Minecraft. Finally, it will call addon.addRecipe() to register the recipe with the addon. After calling this method, the recipe will be available in-game and can be crafted by players.
     */
    public void build() {
        if (pattern == null) throw new IllegalStateException("ShapedRecipeBuilder '" + name + "' needs a grid pattern");
        if (resultId == null) throw new IllegalStateException("ShapedRecipeBuilder '" + name + "' needs a result");
        if (keys.isEmpty()) throw new IllegalStateException("ShapedRecipeBuilder '" + name + "' needs key definitions");

        JsonObject recipe = new JsonObject();
        recipe.addProperty("type", "minecraft:crafting_shaped");
        recipe.addProperty("category", category);
        if (group != null && !group.isEmpty()) recipe.addProperty("group", group);

        JsonObject keyObj = new JsonObject();
        for (Map.Entry<Character, String> entry : keys.entrySet()) {
            JsonObject val = new JsonObject();
            val.addProperty(keyIsTag.getOrDefault(entry.getKey(), false) ? "tag" : "item", entry.getValue());
            keyObj.add(String.valueOf(entry.getKey()), val);
        }
        recipe.add("key", keyObj);

        JsonArray patternArray = new JsonArray();
        for (String row : pattern) patternArray.add(row);
        recipe.add("pattern", patternArray);

        JsonObject result = new JsonObject();
        result.addProperty("count", resultCount);
        result.addProperty("id", resultId);
        recipe.add("result", result);

        addon.addRecipe(name, recipe);
    }
}
