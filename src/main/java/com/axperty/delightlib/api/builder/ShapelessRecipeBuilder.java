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

    public ShapelessRecipeBuilder addIngredient(String itemId) {
        JsonObject ing = new JsonObject();
        ing.addProperty("item", itemId);
        ingredients.add(ing);
        return this;
    }

    public ShapelessRecipeBuilder addTagIngredient(String tag) {
        JsonObject ing = new JsonObject();
        ing.addProperty("tag", tag);
        ingredients.add(ing);
        return this;
    }

    public ShapelessRecipeBuilder result(String itemId) { this.resultId = itemId; return this; }
    public ShapelessRecipeBuilder result(String itemId, int count) { this.resultId = itemId; this.resultCount = count; return this; }
    public ShapelessRecipeBuilder group(String group) { this.group = group; return this; }
    public ShapelessRecipeBuilder category(String category) { this.category = category; return this; }

    public void build() {
        if (resultId == null) throw new IllegalStateException("ShapelessRecipeBuilder '" + name + "' needs a result");
        if (ingredients.isEmpty()) throw new IllegalStateException("ShapelessRecipeBuilder '" + name + "' needs ingredients");

        JsonObject recipe = new JsonObject();
        recipe.addProperty("type", "minecraft:crafting_shapeless");
        recipe.addProperty("category", category);
        if (group != null && !group.isEmpty()) recipe.addProperty("group", group);
        recipe.add("ingredients", ingredients);

        JsonObject result = new JsonObject();
        result.addProperty("count", resultCount);
        result.addProperty("item", resultId);
        recipe.add("result", result);

        addon.addRecipe(name, recipe);
    }
}
