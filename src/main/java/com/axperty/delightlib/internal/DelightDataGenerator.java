package com.axperty.delightlib.internal;

import com.axperty.delightlib.api.CropInfo;
import com.axperty.delightlib.api.DelightAddon;
import com.axperty.delightlib.api.PlaceableFoodInfo;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class DelightDataGenerator implements DataProvider {
    private final PackOutput output;
    private final DelightAddon addon;

    public DelightDataGenerator(PackOutput output, DelightAddon addon) {
        this.output = output;
        this.addon = addon;
    }

    @Override
    public CompletableFuture<?> run(CachedOutput cache) {
        List<CompletableFuture<?>> futures = new ArrayList<>();
        futures.addAll(generateTags(cache));
        futures.addAll(generateItemModels(cache));
        futures.addAll(generateCabinetAssets(cache));
        futures.addAll(generateCrateAndBagAssets(cache));
        futures.addAll(generateCropAssets(cache));
        futures.addAll(generatePlaceableFoodAssets(cache));
        futures.addAll(generatePlaceableFoodData(cache));
        futures.addAll(generateLootTables(cache));
        futures.add(generateLang(cache));
        futures.addAll(generateRecipes(cache));
        return CompletableFuture.allOf(futures.toArray(CompletableFuture[]::new));
    }

    // Tags

    private List<CompletableFuture<?>> generateTags(CachedOutput cache) {
        List<CompletableFuture<?>> futures = new ArrayList<>();
        String modId = addon.getModId();

        if (!addon.getKnifeNames().isEmpty()) {
            futures.add(saveTag(cache, "c", "items", "tools/knifes", addon.getKnifeNames(), modId));
            futures.add(saveTag(cache, "farmersdelight", "items", "tools/knives", addon.getKnifeNames(), modId));
        }
        if (!addon.getCabinetNames().isEmpty()) {
            futures.add(saveTag(cache, "farmersdelight", "blocks", "cabinets", addon.getCabinetNames(), modId));
            futures.add(saveTag(cache, "farmersdelight", "items", "cabinets", addon.getCabinetNames(), modId));
            futures.add(saveTag(cache, "minecraft", "blocks", "mineable/axe", addon.getCabinetNames(), modId));
        }
        return futures;
    }

    private CompletableFuture<?> saveTag(CachedOutput cache, String ns, String type, String tagPath, List<String> names, String modId) {
        JsonObject tag = new JsonObject();
        JsonArray values = new JsonArray();
        for (String name : names) values.add(modId + ":" + name);
        tag.add("values", values);
        return DataProvider.saveStable(cache, tag,
                output.getOutputFolder().resolve("data").resolve(ns).resolve("tags").resolve(type).resolve(tagPath + ".json"));
    }

    // Item models

    private List<CompletableFuture<?>> generateItemModels(CachedOutput cache) {
        List<CompletableFuture<?>> futures = new ArrayList<>();
        String modId = addon.getModId();
        for (Map.Entry<String, String> entry : addon.getItemModelParents().entrySet()) {
            JsonObject model = new JsonObject();
            model.addProperty("parent", entry.getValue());
            JsonObject tex = new JsonObject();
            tex.addProperty("layer0", modId + ":item/" + entry.getKey());
            model.add("textures", tex);
            futures.add(save(cache, "assets", modId, "models/item/" + entry.getKey() + ".json", model));
        }
        return futures;
    }

    // Cabinet assets

    private List<CompletableFuture<?>> generateCabinetAssets(CachedOutput cache) {
        List<CompletableFuture<?>> futures = new ArrayList<>();
        String modId = addon.getModId();
        for (String name : addon.getCabinetNames()) {
            // Closed
            JsonObject closed = blockModel("minecraft:block/orientable",
                    modId, name + "_front", name + "_side", name + "_top");
            futures.add(save(cache, "assets", modId, "models/block/" + name + ".json", closed));

            // Open
            JsonObject open = blockModel("minecraft:block/orientable",
                    modId, name + "_front_open", name + "_side", name + "_top");
            futures.add(save(cache, "assets", modId, "models/block/" + name + "_open.json", open));

            // Item
            JsonObject item = new JsonObject();
            item.addProperty("parent", modId + ":block/" + name);
            futures.add(save(cache, "assets", modId, "models/item/" + name + ".json", item));

            // Blockstate
            futures.add(save(cache, "assets", modId, "blockstates/" + name + ".json", cabinetBlockstate(modId, name)));
        }
        return futures;
    }

    private JsonObject blockModel(String parent, String modId, String front, String side, String top) {
        JsonObject m = new JsonObject();
        m.addProperty("parent", parent);
        JsonObject t = new JsonObject();
        t.addProperty("front", modId + ":block/" + front);
        t.addProperty("side", modId + ":block/" + side);
        t.addProperty("top", modId + ":block/" + top);
        m.add("textures", t);
        return m;
    }

    private JsonObject cabinetBlockstate(String modId, String name) {
        JsonObject bs = new JsonObject();
        JsonObject variants = new JsonObject();
        String[] facings = {"north", "south", "west", "east"};
        int[] yRots = {0, 180, 270, 90};
        for (int i = 0; i < facings.length; i++) {
            for (boolean open : new boolean[]{false, true}) {
                JsonObject v = new JsonObject();
                v.addProperty("model", modId + ":block/" + name + (open ? "_open" : ""));
                if (yRots[i] != 0) v.addProperty("y", yRots[i]);
                variants.add("facing=" + facings[i] + ",open=" + open, v);
            }
        }
        bs.add("variants", variants);
        return bs;
    }

    // Crop assets

    private List<CompletableFuture<?>> generateCropAssets(CachedOutput cache) {
        List<CompletableFuture<?>> futures = new ArrayList<>();
        String modId = addon.getModId();

        for (CropInfo crop : addon.getCropInfos()) {
            // Blockstate (age 0-7)
            JsonObject bs = new JsonObject();
            JsonObject variants = new JsonObject();
            for (int age = 0; age <= 7; age++) {
                JsonObject v = new JsonObject();
                v.addProperty("model", modId + ":block/" + crop.blockName() + "_stage" + age);
                variants.add("age=" + age, v);
            }
            bs.add("variants", variants);
            futures.add(save(cache, "assets", modId, "blockstates/" + crop.blockName() + ".json", bs));

            // Block models
            for (int age = 0; age <= 7; age++) {
                JsonObject m = new JsonObject();
                m.addProperty("parent", "minecraft:block/crop");
                m.addProperty("render_type", "minecraft:cutout");
                JsonObject tex = new JsonObject();
                tex.addProperty("crop", modId + ":block/" + crop.blockName() + "_stage" + age);
                m.add("textures", tex);
                futures.add(save(cache, "assets", modId, "models/block/" + crop.blockName() + "_stage" + age + ".json", m));
            }

            // Crop loot table
            futures.add(save(cache, "data", modId, "loot_tables/blocks/" + crop.blockName() + ".json",
                    cropLootTable(modId, crop)));
        }
        return futures;
    }

    private JsonObject cropLootTable(String modId, CropInfo crop) {
        JsonObject loot = new JsonObject();
        loot.addProperty("type", "minecraft:block");
        JsonArray pools = new JsonArray();

        // Pool 1: drop crop when mature, else seed
        JsonObject pool1 = new JsonObject();
        pool1.addProperty("rolls", 1);
        JsonArray entries1 = new JsonArray();

        JsonObject alternatives = new JsonObject();
        alternatives.addProperty("type", "minecraft:alternatives");
        JsonArray children = new JsonArray();

        // Mature: drop crop
        JsonObject matureEntry = new JsonObject();
        matureEntry.addProperty("type", "minecraft:item");
        matureEntry.addProperty("name", modId + ":" + crop.cropName());
        JsonArray matureCond = new JsonArray();
        JsonObject ageCond = new JsonObject();
        ageCond.addProperty("condition", "minecraft:block_state_property");
        ageCond.addProperty("block", modId + ":" + crop.blockName());
        JsonObject props = new JsonObject();
        props.addProperty("age", "7");
        ageCond.add("properties", props);
        matureCond.add(ageCond);
        matureEntry.add("conditions", matureCond);
        children.add(matureEntry);

        // Not mature: drop seed
        JsonObject seedEntry = new JsonObject();
        seedEntry.addProperty("type", "minecraft:item");
        seedEntry.addProperty("name", modId + ":" + crop.seedName());
        children.add(seedEntry);

        alternatives.add("children", children);
        entries1.add(alternatives);
        pool1.add("entries", entries1);
        pools.add(pool1);

        // Pool 2: bonus seed when mature
        JsonObject pool2 = new JsonObject();
        pool2.addProperty("rolls", 1);
        JsonArray conds2 = new JsonArray();
        conds2.add(ageCond);
        pool2.add("conditions", conds2);
        JsonArray entries2 = new JsonArray();
        JsonObject bonusSeed = new JsonObject();
        bonusSeed.addProperty("type", "minecraft:item");
        bonusSeed.addProperty("name", modId + ":" + crop.seedName());
        JsonArray functions = new JsonArray();
        JsonObject fortune = new JsonObject();
        fortune.addProperty("function", "minecraft:apply_bonus");
        fortune.addProperty("enchantment", "minecraft:fortune");
        fortune.addProperty("formula", "minecraft:binomial_with_bonus_count");
        JsonObject params = new JsonObject();
        params.addProperty("extra", 3);
        params.addProperty("probability", 0.5714286);
        fortune.add("parameters", params);
        functions.add(fortune);
        bonusSeed.add("functions", functions);
        entries2.add(bonusSeed);
        pool2.add("entries", entries2);
        pools.add(pool2);

        loot.add("pools", pools);
        return loot;
    }

    // Loot tables

    private List<CompletableFuture<?>> generateLootTables(CachedOutput cache) {
        List<CompletableFuture<?>> futures = new ArrayList<>();
        String modId = addon.getModId();
        for (String name : addon.getBlockNames()) {
            boolean isPlaceableFood = addon.getPlaceableFoodInfos().stream().anyMatch(f -> f.name().equals(name));
            if (isPlaceableFood) continue;
            JsonObject loot = new JsonObject();
            loot.addProperty("type", "minecraft:block");
            JsonArray pools = new JsonArray();
            JsonObject pool = new JsonObject();
            pool.addProperty("rolls", 1);
            JsonArray entries = new JsonArray();
            JsonObject entry = new JsonObject();
            entry.addProperty("type", "minecraft:item");
            entry.addProperty("name", modId + ":" + name);
            entries.add(entry);
            pool.add("entries", entries);
            JsonArray conds = new JsonArray();
            JsonObject cond = new JsonObject();
            cond.addProperty("condition", "minecraft:survives_explosion");
            conds.add(cond);
            pool.add("conditions", conds);
            pools.add(pool);
            loot.add("pools", pools);
            futures.add(save(cache, "data", modId, "loot_tables/blocks/" + name + ".json", loot));
        }
        return futures;
    }

    // Lang files

    private CompletableFuture<?> generateLang(CachedOutput cache) {
        Map<String, String> lang = addon.getLangEntries();
        if (lang.isEmpty()) return CompletableFuture.completedFuture(null);
        JsonObject json = new JsonObject();
        lang.forEach(json::addProperty);
        return save(cache, "assets", addon.getModId(), "lang/en_us.json", json);
    }

    // Recipes

    private List<CompletableFuture<?>> generateRecipes(CachedOutput cache) {
        List<CompletableFuture<?>> futures = new ArrayList<>();
        String modId = addon.getModId();
        for (Map.Entry<String, JsonObject> entry : addon.getRecipes().entrySet()) {
            futures.add(save(cache, "data", modId, "recipes/" + entry.getKey() + ".json", entry.getValue()));
        }
        return futures;
    }

    // Helpers

    private CompletableFuture<?> save(CachedOutput cache, String root, String modId, String path, JsonObject json) {
        return DataProvider.saveStable(cache, json, output.getOutputFolder().resolve(root).resolve(modId).resolve(path));
    }

    @Override
    public String getName() { return "Delight Lib: " + addon.getModId(); }

    // Crate and Bag assets

    private List<CompletableFuture<?>> generateCrateAndBagAssets(CachedOutput cache) {
        List<CompletableFuture<?>> futures = new ArrayList<>();
        String modId = addon.getModId();

        // Crates
        for (String name : addon.getCrateNames()) {
            futures.add(save(cache, "assets", modId, "models/block/" + name + ".json",
                    blockModel("minecraft:block/cube_bottom_top", modId, name + "_side", name + "_top", name + "_bottom")));
            JsonObject item = new JsonObject();
            item.addProperty("parent", modId + ":block/" + name);
            futures.add(save(cache, "assets", modId, "models/item/" + name + ".json", item));
            futures.add(save(cache, "assets", modId, "blockstates/" + name + ".json", simpleBlockstate(modId, name)));
        }

        // Bags
        for (String name : addon.getBagNames()) {
            futures.add(save(cache, "assets", modId, "models/block/" + name + ".json",
                    blockModel("minecraft:block/cube_bottom_top", modId, name + "_side", name + "_top", name + "_bottom")));
            JsonObject item = new JsonObject();
            item.addProperty("parent", modId + ":block/" + name);
            futures.add(save(cache, "assets", modId, "models/item/" + name + ".json", item));
            futures.add(save(cache, "assets", modId, "blockstates/" + name + ".json", simpleBlockstate(modId, name)));
        }

        return futures;
    }

    private List<CompletableFuture<?>> generatePlaceableFoodAssets(CachedOutput cache) {
        List<CompletableFuture<?>> futures = new ArrayList<>();
        String modId = addon.getModId();
        for (PlaceableFoodInfo food : addon.getPlaceableFoodInfos()) {
            JsonObject bs = new JsonObject();
            JsonObject variants = new JsonObject();
            String[] facings = {"east", "north", "south", "west"};
            int[] yRots = {90, 0, 180, 270};

            if (food.type() == PlaceableFoodInfo.FoodType.PIE) {
                for (int bites = 0; bites <= 3; bites++) {
                    for (int i = 0; i < facings.length; i++) {
                        JsonObject v = new JsonObject();
                        String modelSuffix = bites == 0 ? "" : "_slice" + bites;
                        v.addProperty("model", modId + ":block/" + food.name() + modelSuffix);
                        if (yRots[i] != 0) v.addProperty("y", yRots[i]);
                        variants.add("bites=" + bites + ",facing=" + facings[i], v);
                    }
                }
            } else {
                for (int servings = 0; servings <= 4; servings++) {
                    for (int i = 0; i < facings.length; i++) {
                        JsonObject v = new JsonObject();
                        String modelSuffix;
                        if (servings == 0) {
                            modelSuffix = "_leftovers";
                        } else {
                            modelSuffix = "_stage" + (4 - servings);
                        }
                        v.addProperty("model", modId + ":block/" + food.name() + modelSuffix);
                        if (yRots[i] != 0) v.addProperty("y", yRots[i]);
                        variants.add("facing=" + facings[i] + ",servings=" + servings, v);
                    }
                }
            }

            bs.add("variants", variants);
            futures.add(save(cache, "assets", modId, "blockstates/" + food.name() + ".json", bs));

            // Item model
            JsonObject item = new JsonObject();
            item.addProperty("parent", "minecraft:item/generated");
            JsonObject tex = new JsonObject();
            tex.addProperty("layer0", modId + ":item/" + food.name());
            item.add("textures", tex);
            futures.add(save(cache, "assets", modId, "models/item/" + food.name() + ".json", item));
        }
        return futures;
    }

    private JsonObject simpleBlockstate(String modId, String name) {
        JsonObject bs = new JsonObject();
        JsonObject variants = new JsonObject();
        JsonObject v = new JsonObject();
        v.addProperty("model", modId + ":block/" + name);
        variants.add("", v);
        bs.add("variants", variants);
        return bs;
    }

    private List<CompletableFuture<?>> generatePlaceableFoodData(CachedOutput cache) {
        List<CompletableFuture<?>> futures = new ArrayList<>();
        String modId = addon.getModId();
        for (PlaceableFoodInfo food : addon.getPlaceableFoodInfos()) {
            if (food.type() == PlaceableFoodInfo.FoodType.PIE) {
                // This seems useless for Farmer's Delight Refabricated
                // Pie loot table
                //JsonObject loot = new JsonObject();
                //loot.addProperty("type", "minecraft:block");
                //loot.addProperty("random_sequence", modId + ":blocks/" + food.name());
                //futures.add(save(cache, "data", modId, "loot_tables/blocks/" + food.name() + ".json", loot));

                // Pie cutting recipe
                JsonObject recipe = new JsonObject();
                recipe.addProperty("type", "farmersdelight:cutting");

                JsonArray ingredients = new JsonArray();
                JsonObject ing = new JsonObject();
                ing.addProperty("item", modId + ":" + food.name());
                ingredients.add(ing);
                recipe.add("ingredients", ingredients);

                JsonArray result = new JsonArray();
                JsonObject res = new JsonObject();
                res.addProperty("count", 4);
                String sliceId = food.sliceItem() != null ?
                        net.minecraft.core.registries.BuiltInRegistries.ITEM.getKey(food.sliceItem().get()).toString() :
                        modId + ":" + food.name() + "_slice";
                res.addProperty("item", sliceId);
                result.add(res);
                recipe.add("result", result);

                JsonObject tool = new JsonObject();
                tool.addProperty("tag", "c:tools/knives");
                recipe.add("tool", tool);

                futures.add(save(cache, "data", modId, "recipes/cutting/" + food.name() + ".json", recipe));
            } else {
                // Feast loot table
                JsonObject loot = new JsonObject();
                loot.addProperty("type", "minecraft:block");

                JsonArray pools = new JsonArray();

                // Pool 1: Block itself if servings == 4
                JsonObject pool1 = new JsonObject();
                pool1.addProperty("name", "pool1");
                pool1.addProperty("rolls", 1);

                JsonArray entries1 = new JsonArray();
                JsonObject entry1 = new JsonObject();
                entry1.addProperty("type", "minecraft:item");
                entry1.addProperty("name", modId + ":" + food.name());
                entries1.add(entry1);
                pool1.add("entries", entries1);

                JsonArray conds1 = new JsonArray();
                JsonObject cond1 = new JsonObject();
                cond1.addProperty("condition", "minecraft:block_state_property");
                cond1.addProperty("block", modId + ":" + food.name());
                JsonObject props1 = new JsonObject();
                props1.addProperty("servings", "4");
                cond1.add("properties", props1);
                conds1.add(cond1);
                pool1.add("conditions", conds1);

                pools.add(pool1);

                // Pool 2: Bowl if servings != 4
                JsonObject pool2 = new JsonObject();
                pool2.addProperty("name", "pool2");
                pool2.addProperty("rolls", 1);

                JsonArray entries2 = new JsonArray();
                JsonObject entry2 = new JsonObject();
                entry2.addProperty("type", "minecraft:item");
                entry2.addProperty("name", "minecraft:bowl");
                entries2.add(entry2);
                pool2.add("entries", entries2);

                JsonArray conds2 = new JsonArray();
                JsonObject inverted2 = new JsonObject();
                inverted2.addProperty("condition", "minecraft:inverted");
                inverted2.add("term", cond1);
                conds2.add(inverted2);
                pool2.add("conditions", conds2);

                pools.add(pool2);

                // Pool 3: Custom output item if servings != 4
                JsonObject pool3 = new JsonObject();
                pool3.addProperty("name", "pool3");
                pool3.addProperty("rolls", 1);

                JsonArray entries3 = new JsonArray();
                JsonObject entry3 = new JsonObject();
                entry3.addProperty("type", "minecraft:item");
                String outputItemId = food.feastOutputItem() != null ?
                        net.minecraft.core.registries.BuiltInRegistries.ITEM.getKey(food.feastOutputItem().get()).toString() :
                        (food.servingItem() != null ? net.minecraft.core.registries.BuiltInRegistries.ITEM.getKey(food.servingItem().get()).toString() : "minecraft:air");
                entry3.addProperty("name", outputItemId);
                entries3.add(entry3);
                pool3.add("entries", entries3);

                pool3.add("conditions", conds2);

                pools.add(pool3);

                loot.add("pools", pools);
                futures.add(save(cache, "data", modId, "loot_tables/blocks/" + food.name() + ".json", loot));
            }
        }
        return futures;
    }
}
