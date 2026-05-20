![Delight Lib](https://cdn.modrinth.com/data/rmDY6fYt/images/5d6afc0c897d26557b68c4b6ae51872d42b0ae89.png)

***

# Getting Started

This guide details how to implement Delight Lib to streamline the creation of Farmer's Delight add-ons. It covers the fluent API for content registration and the internal architecture of the library.

## How Delight Lib Works

Delight Lib is designed to remove the boilerplate overhead of Minecraft modding by automating repetitive tasks: registration and resource generation.

### Centralized Registry Management

Instead of manually managing multiple DeferredRegister objects for items, blocks, and block entities, the DelightAddon class acts as a single point of control. When initialized, it handles the setup of all necessary registers and attaches them to the mod event bus automatically.

### The Builder and Memory Map Pattern

When using builders like `.food()` or `.cabinet()`, calling `.build()` performs two primary actions:
1. It registers the object and its associated components into the game registry.
2. It saves the object properties into an internal Memory Map.

### Automated Data Pipeline

The library automatically listens for the GatherDataEvent. During this phase, it reads the metadata stored in the Memory Map and uses custom DataProviders to generate:
- **Blockstate and Model JSONs**: Standard orientations for cabinets, crates, and crops.
- **Loot Tables**: Explosion resistant drops and complex crop harvesting logic.
- **Recipes**: Crafting table and Cooking Pot `.json` files.
- **Lang Files**: Automatic title casing of registry names for `en_us.json`.

## Quick Links

- [1. Dependencies](#1-dependencies)
- [2. Main Class Entrypoint](#2-main-class-entrypoint)
- [3. Creating Knives](#3-creating-knives)
- [4. Creating Food](#4-creating-food)
- [5. Creating Placeable Food Items](#5-creating-placeable-food-items)
- [6. Creating Crops](#6-creating-crops)
- [7. Creating Storage Blocks](#7-creating-storage-blocks)
- [8. Adding Recipes](#8-adding-recipes)
- [9. Generating Files](#9-generating-files)

## 1. Dependencies

Add the following to the build.gradle file to link Farmer's Delight and Delight Lib:

```groovy
dependencies {
    implementation "curse.maven:farmers-delight-XXXXXXX:YYYYYYY"
    implementation "curse.maven:delight-lib-XXXXXXX:YYYYYYY"
}
```

Declare the required dependencies in your `neoforge.mods.toml` or `mods.toml` file:

```toml
[[dependencies.yourmodid]]
modId="delightlib"
type="required"
versionRange="*"

[[dependencies.yourmodid]]
modId="farmersdelight"
type="required"
versionRange="*"
```

## 2. Main Class Entrypoint

Initialize the library within the main mod constructor using DelightAddon. This handles the registry and creative tab setup.

```java
@Mod("mymod")
public class MyMod {
    public MyMod(IEventBus bus, ModContainer container) {
        var addon = DelightAddon.create("mymod", bus)
            .withCreativeTab("My Mod", () -> new ItemStack(Items.BREAD));
        
        // Register content using the addon instance
    }
}
```

## 3. Creating Knives

The KnifeBuilder allows for the creation of tools with custom combat statistics and attributes.

```java
// Basic knife
addon.knife("copper_knife", Tiers.STONE).build();

// Specialized knife
addon.knife("netherite_knife", Tiers.NETHERITE)
    .attackDamage(1.5f)
    .attackSpeed(-1.8f)
    .fireResistant()
    .build();
```

## 4. Creating Food

The FoodBuilder provides a comprehensive API for standard foods, bowl meals, and drinks.

```java
// Standard snack
addon.food("fried_egg")
    .nutrition(4)
    .saturation(0.4f)
    .fast()
    .build();

// Heavy meal with Nourishment effect
addon.food("beef_stew")
    .nutrition(12)
    .saturation(0.8f)
    .bowlFood() // Sets bowl as remainder and stacks to 16
    .withNourishment(FoodDuration.MEDIUM)
    .build();

// Drinkable item
addon.food("apple_juice")
    .nutrition(2)
    .saturation(0.2f)
    .drinkable() // Sets bottle as remainder and stacks to 16
    .alwaysEdible()
    .build();
```

### Advanced Food Features

You can further customize food behavior with the following methods:
- **withEffect(Holder<MobEffect> effect, int duration, int amplifier, float chance)**: Adds a custom mob effect.
- **stacksTo(int size)**: Sets a custom maximum stack size.
- **hideEffectTooltip()**: Removes the automatic effect information from the item tooltip.
- **customTooltip()**: Signals that a custom tooltip will be provided via localization.

## 5. Creating Placeable Food Items

Placeable foods like pies or feasts can be sliceable or consumable in up to three stages, models and textures have to manually be added inside the `models/block` folder.

### Pies

Pies require a slice item to be registered first.

```java
addon.food("cherry_pie_slice").nutrition(3).saturation(0.3f).fast().build();
addon.placeableFood("cherry_pie")
    .asPie("cherry_pie_slice")
    .build();
```

### Feasts

Feasts can provide a serving item and optionally leave a container behind after being consumed.

```java
addon.food("stew_serving").nutrition(6).saturation(0.6f).bowlFood().build();
addon.placeableFood("stew_pot")
    .asFeast("stew_serving", true) // true indicates it leaves a container (leftovers)
    .build();
```

## 6. Creating Crops

Crops can be registered with growth stages and optional food properties.

```java
// Basic crop (generates a seed item automatically)
addon.crop("corn").build();

// Edible crop where the seed and the item are identical
addon.crop("onion")
    .asFood(1, 0.1f)
    .seedIsItem()
    .build();
```

## 7. Creating Storage Blocks

This library simplifies the creation of cabinets, crates, and bags. All storage blocks require a recipe to be defined during registration.

### Cabinets

Cabinets are interactive storage blocks that work similar to barrels.

```java
addon.cabinet("spruce_cabinet")
    .soundType(SoundType.WOOD)
    .burnTime(300)
    .recipe(b -> b.grid("SSS", "T T", "SSS")
        .define('S', "minecraft:spruce_slab")
        .define('T', "minecraft:spruce_trapdoor"))
    .build();
```

### Crates and Bags

Crates are sturdy wooden containers, while bags are wool based storage blocks.

```java
// Wooden crate
addon.crate("tomato_crate")
    .recipe(b -> b.grid("###", "###", "###")
        .define('#', "mymod:tomato"))
    .build();

// Wool bag
addon.bag("rice_bag")
    .recipe(b -> b.grid("###", "###", "###")
        .define('#', "farmersdelight:rice"))
    .build();
```

## 8. Adding Recipes

The library supports Cooking Pot, Shaped, and Shapeless recipes.

### Cooking Pot

```java
addon.cookingRecipe("beef_stew")
    .addIngredient("minecraft:cooked_beef")
    .addIngredient("minecraft:carrot")
    .addIngredient("minecraft:potato")
    .result("mymod:beef_stew")
    .container("minecraft:bowl")
    .experience(1.0f)
    .cookingTime(200)
    .recipeBookTab("meals")
    .build();
```

### Crafting Table

```java
// Shaped Recipe
addon.shapedRecipe("example_tool")
    .grid(" I ", " S ", " S ")
    .define('I', "minecraft:iron_ingot")
    .define('S', "minecraft:stick")
    .result("mymod:iron_staff")
    .build();

// Shapeless Recipe
addon.shapelessRecipe("mixed_seeds")
    .addIngredient("minecraft:wheat_seeds")
    .addTagIngredient("forge:seeds")
    .result("mymod:seed_bundle", 2)
    .build();
```

## 9. Generating Files

The library automates the creation of models, loot tables, and translations through DataGen. Run the following command in the terminal to synchronize these files:

`gradlew runData`

The generated files are output to src/generated/resources. Assets such as PNG textures should be placed in src/main/resources/assets/[modid]/textures/.