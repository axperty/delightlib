package com.axperty.delightlib.api.builder;

import com.axperty.delightlib.api.DelightAddon;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import net.minecraft.world.item.Item;

import java.util.function.Supplier;

public class CookingRecipeBuilder {
    private final DelightAddon addon;
    private final String name;
    private final JsonArray ingredients = new JsonArray();
    private String resultId;
    private int resultCount = 1;
    private String containerId = null;
    private float experience = 0.0f;
    private int cookingTime = 200;
    private String recipeBookTab = "misc";

    public CookingRecipeBuilder(DelightAddon addon, String name) {
        this.addon = addon;
        this.name = name;
    }

    /**
     * Adds an ingredient to the recipe. The itemId should be in the format "modid:itemname".
     *
     * @param itemId the ID of the item to add as an ingredient, in the format "modid:itemname"
     * @return this builder for chaining
     */
    public CookingRecipeBuilder addIngredient(String itemId) {
        JsonObject ing = new JsonObject();
        ing.addProperty("item", itemId);
        ingredients.add(ing);
        return this;
    }

    /**
     * Adds an ingredient to the recipe using a tag. The tag should be in the format "modid:tagname".
     *
     * @param tag the tag to add as an ingredient, in the format "modid:tagname"
     * @return this builder for chaining
     */
    public CookingRecipeBuilder addTagIngredient(String tag) {
        JsonObject ing = new JsonObject();
        ing.addProperty("tag", tag);
        ingredients.add(ing);
        return this;
    }

    /**
     * Sets the result of the recipe. The itemId should be in the format "modid:itemname".
     *
     * @param itemId the ID of the item to set as the result, in the format "modid:itemname"
     * @return this builder for chaining
     */
    public CookingRecipeBuilder result(String itemId) {
        this.resultId = itemId;
        return this;
    }

    /**
     * Sets the result of the recipe with a count. The itemId should be in the format "modid:itemname".
     *
     * @param itemId the ID of the item to set as the result, in the format "modid:itemname"
     * @param count  the count of the result item to produce
     * @return this builder for chaining
     */
    public CookingRecipeBuilder result(String itemId, int count) {
        this.resultId = itemId;
        this.resultCount = count;
        return this;
    }

    /**
     * Sets the result of the recipe using an Item supplier. This is a convenience method to avoid having to call builtInRegistryHolder().key().location() on the item yourself.
     *
     * @param item a supplier that provides the item to set as the result. The item ID will be derived from the item's registry name.
     * @return this builder for chaining
     */
    public CookingRecipeBuilder result(Supplier<Item> item) {
        this.resultId = item.get().builtInRegistryHolder().key().identifier().toString();
        return this;
    }

    /**
     * Sets the container item that will be returned when the recipe is crafted. The itemId should be in the format "modid:itemname". This is used for recipes where the ingredient is not consumed, like a bucket in a recipe that uses milk.
     *
     * @param containerId the ID of the item to set as the container, in the format "modid:itemname"
     * @return this builder for chaining
     */
    public CookingRecipeBuilder container(String containerId) {
        this.containerId = containerId;
        return this;
    }

    /**
     * Set the experience given to the player when they craft this recipe. This is a float value, where 1.0 is equal to 1 experience point. By default, recipes give 0 experience, but you can set this to a different value if you want the recipe to give experience when crafted. Note that the experience given by the recipe does not depend on the cooking time, so you can have a recipe that cooks very quickly but still gives a lot of experience, or vice versa.
     *
     * @param xp the amount of experience to give the player when they craft this recipe. This is a float value, where 1.0 is equal to 1 experience point. By default, recipes give 0 experience.
     * @return this builder for chaining
     */
    public CookingRecipeBuilder experience(float xp) {
        this.experience = xp;
        return this;
    }

    /**
     * Sets the cooking time for this recipe in ticks. By default, recipes take 200 ticks (10 seconds) to cook. You can set this to a different value if you want the recipe to cook faster or slower. Note that the cooking time does not affect the experience given by the recipe, so you can have a recipe that cooks very quickly but still gives a lot of experience, or vice versa.
     *
     * @param ticks the cooking time for this recipe in ticks. By default, recipes take 200 ticks (10 seconds) to cook. You can set this to a different value if you want the recipe to cook faster or slower. Note that the cooking time does not affect the experience given by the recipe, so you can have a recipe that cooks very quickly but still gives a lot of experience, or vice versa.
     * @return this builder for chaining
     */
    public CookingRecipeBuilder cookingTime(int ticks) {
        this.cookingTime = ticks;
        return this;
    }

    /**
     * Sets the recipe book tab for this recipe in the Farmer's Delight Cooking Pot recipe book.
     * Valid values are:
     * <ul>
     *   <li>{@code "meals"} – for food dishes and soups</li>
     *   <li>{@code "drinks"} – for drinkable recipes</li>
     *   <li>{@code "misc"} – default catch-all tab</li>
     * </ul>
     * Any other value will cause an {@link IllegalArgumentException} at startup.
     *
     * @param tab one of {@code "meals"}, {@code "drinks"}, or {@code "misc"}
     * @return this builder for chaining
     */
    public CookingRecipeBuilder recipeBookTab(String tab) {
        if (!tab.equals("meals") && !tab.equals("drinks") && !tab.equals("misc")) {
            throw new IllegalArgumentException(
                "Invalid recipe_book_tab '" + tab + "'. Must be one of: meals, drinks, misc");
        }
        this.recipeBookTab = tab;
        return this;
    }

    /**
     * Builds the recipe and adds it to the addon. After calling this method, the recipe will be available in-game and can be crafted by players. Note that you should only call this method once per recipe, as it registers the recipe with the addon.
     */
    public void build() {
        if (resultId == null) throw new IllegalStateException("CookingRecipeBuilder '" + name + "' needs a result");
        if (ingredients.isEmpty())
            throw new IllegalStateException("CookingRecipeBuilder '" + name + "' needs ingredients");

        JsonObject recipe = new JsonObject();
        recipe.addProperty("type", "farmersdelight:cooking");
        if (experience > 0) recipe.addProperty("experience", experience);
        recipe.add("ingredients", ingredients);
        if (recipeBookTab != null && !recipeBookTab.isEmpty()) recipe.addProperty("recipe_book_tab", recipeBookTab);

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
        if (cookingTime != 200) recipe.addProperty("cookingtime", cookingTime);

        addon.addRecipe("cooking/" + name, recipe);
    }
}
