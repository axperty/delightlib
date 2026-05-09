package com.axperty.delightlib.api.builder;

import com.axperty.delightlib.api.DelightAddon;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.LinkedHashMap;
import java.util.Map;

public class ShapedRecipeBuilder {
    private final DelightAddon addon;
    private final String name;
    private String[] pattern;
    private final Map<Character, String> keys = new LinkedHashMap<>();
    private final Map<Character, Boolean> keyIsTag = new LinkedHashMap<>();
    private String resultId;
    private int resultCount = 1;
    private String group = "";
    private String category = "misc";

    public ShapedRecipeBuilder(DelightAddon addon, String name) {
        this.addon = addon;
        this.name = name;
    }

    public ShapedRecipeBuilder grid(String... rows) {
        if (rows.length < 1 || rows.length > 3) throw new IllegalArgumentException("Grid must have 1-3 rows");
        for (String row : rows) {
            if (row.length() < 1 || row.length() > 3) throw new IllegalArgumentException("Row must be 1-3 chars: '" + row + "'");
        }
        this.pattern = rows;
        return this;
    }

    public ShapedRecipeBuilder define(char key, String itemId) { keys.put(key, itemId); keyIsTag.put(key, false); return this; }
    public ShapedRecipeBuilder defineTag(char key, String tag) { keys.put(key, tag); keyIsTag.put(key, true); return this; }
    public ShapedRecipeBuilder result(String itemId) { this.resultId = itemId; return this; }
    public ShapedRecipeBuilder result(String itemId, int count) { this.resultId = itemId; this.resultCount = count; return this; }
    public ShapedRecipeBuilder group(String group) { this.group = group; return this; }
    public ShapedRecipeBuilder category(String category) { this.category = category; return this; }

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
            boolean isTag = keyIsTag.getOrDefault(entry.getKey(), false);
            keyObj.addProperty(String.valueOf(entry.getKey()), (isTag ? "#" : "") + entry.getValue());
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
