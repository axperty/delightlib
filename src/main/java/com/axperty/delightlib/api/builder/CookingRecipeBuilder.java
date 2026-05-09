package com.axperty.delightlib.api.builder;

import com.axperty.delightlib.api.DelightAddon;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.function.Supplier;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Item;

public class CookingRecipeBuilder {
    private final DelightAddon addon;
    private final String name;
    private final JsonArray ingredients = new JsonArray();
    private String resultId;
    private int resultCount = 1;
    private String containerId = null;
    private float experience = 1.0f;
    private int cookingTime = 200;
    private String recipeBookTab = "meals";

    public CookingRecipeBuilder(DelightAddon addon, String name) {
        this.addon = addon;
        this.name = name;
    }

    public CookingRecipeBuilder addIngredient(String itemId) {
        ingredients.add(itemId);
        return this;
    }

    public CookingRecipeBuilder addTagIngredient(String tag) {
        ingredients.add("#" + tag);
        return this;
    }

    public CookingRecipeBuilder result(String itemId) { this.resultId = itemId; return this; }
    public CookingRecipeBuilder result(String itemId, int count) { this.resultId = itemId; this.resultCount = count; return this; }

    public CookingRecipeBuilder result(Supplier<Item> item) {
        this.resultId = BuiltInRegistries.ITEM.getKey(item.get()).toString();
        return this;
    }

    public CookingRecipeBuilder container(String containerId) { this.containerId = containerId; return this; }
    public CookingRecipeBuilder experience(float xp) { this.experience = xp; return this; }
    public CookingRecipeBuilder cookingTime(int ticks) { this.cookingTime = ticks; return this; }
    public CookingRecipeBuilder recipeBookTab(String tab) { this.recipeBookTab = tab; return this; }

    public void build() {
        if (resultId == null) throw new IllegalStateException("CookingRecipeBuilder '" + name + "' needs a result");
        if (ingredients.isEmpty()) throw new IllegalStateException("CookingRecipeBuilder '" + name + "' needs ingredients");

        JsonObject recipe = new JsonObject();
        recipe.addProperty("type", "farmersdelight:cooking");
        recipe.addProperty("cookingtime", cookingTime);
        recipe.addProperty("experience", experience);
        recipe.add("ingredients", ingredients);
        recipe.addProperty("recipe_book_tab", recipeBookTab);

        JsonObject result = new JsonObject();
        result.addProperty("count", resultCount);
        result.addProperty("id", resultId);
        recipe.add("result", result);

        if (containerId != null) {
            JsonObject container = new JsonObject();
            container.addProperty("count", 1);
            container.addProperty("id", containerId);
            recipe.add("container", container);
        }

        addon.addRecipe("cooking/" + name, recipe);
    }
}
