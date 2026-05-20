## Quick Links

### Getting Started

[Setting Up Your Tools](#1-setting-up-your-tools)

### Use Delight Lib Templates (Recommended)

[1. Downloading the Template](#1-downloading-the-template)

### Build Everything Yourself (More Difficult)

[1. Use Loader Templates](#1-use-loader-templates)

[2. Configuring Dependencies](#2-configuring-dependencies)

[3. Writing the Main Class](#3-writing-the-main-class)

### Customizing and Adding Things

[1. Creating Knives](#1-creating-knives)

[2. Creating Food](#2-creating-food)

[3. Creating Crops](#3-creating-crops)

[4. Creating Cabinets](#4-creating-cabinets)

[5. Creating Crates and Bags](#5-creating-crates-and-bags)

[6. Creating Crafting Table Recipes](#6-creating-crafting-table-recipes)

### Finalizing Your Add-on

[1. Generating Files](#8-generating-files)

[2. Adding Custom Textures](#9-adding-custom-textures)

[3. Adding Custom Translations](#10-adding-custom-translations)

[4. Run Your Add-on](#11-run-your-add-on)

[5. Building Your Add-on](#12-building-your-add-on)

[6. Things to Consider](#13-things-to-consider)

# Getting Started

## Setting Up Your Tools

You need a specific program to write and build add-ons for Farmer's Delight. You need to download an IDE [Integrated Development Environment](https://en.wikipedia.org/wiki/Integrated_development_environment) to customize your add-on. The recommended program is [IntelliJ IDEA](https://www.jetbrains.com/idea/) by JetBrains. It's free and includes an optional Ultimate subscription plan with extra features:

* [Download and install IntelliJ IDEA](https://www.jetbrains.com/idea/download/) from their official website for your operating system.
* If you need help installing, click on the `Installation instructions` link on the right side of the website.

# Use Delight Lib Templates (Recommended)

## 1. Downloading the Template

You can use Delight Lib templates which contain everything you need to start making your Farmer's Delight add-on in a few minutes instead of creating it manually, these templates include pre-registered items and blocks which you can customize later:

1. [Download Delight Lib Templates](https://github.com/axperty/delightlib-template) from the GitHub repository.
2. Select a Minecraft version and mod loader on the select box, they must look something like `1.2.3/4.56-loader`.
3. Click on the green `Code` button.
4. Click on `Download ZIP`, this will download the whole template for the version and loader you selected before.
5. Extract the downloaded `.zip` file into a new empty folder on your computer.
6. Open IntelliJ IDEA.
7. Click `Open` and select the folder where you extracted the mod template files.
8. The program will start downloading the required Minecraft files. This takes a few minutes. A progress bar will appear at the bottom of the screen.
9. Remember to follow step [Generating Files](#1-generating-files) every time you add or modify a custom item or block.
10. Skip to section [Customizing and Adding Things to Your Add-on](#customizing-and-adding-things-to-your-add-on) to continue creating your add-on.

Search through every project file to personalize your add-on:
- Replace `examplemod` with your desired add-on name.
- Replace `example` and `Example` with your author or organization information.

# Build Everything Yourself (More Difficult)

## 1. Use Loader Templates

Creating a Minecraft mod requires a mod template to start. Choose your preferred loader below:

### NeoForge Loader

1. Go to the [NeoForge Mod Generator](https://neoforged.net/mod-generator/) website.
2. On this page, customize your `Mod Name` and `Package Name`.
3. Your `Mod ID` will be set automatically, remember it.
4. Change the `Minecraft Version` dropdown to `1.21.1`.
5. Click the blue `Download Mod Project` button.
6. Extract the downloaded `.zip` file into a new empty folder on your computer.
7. Open IntelliJ IDEA.
8. Click `Open` and select the folder where you extracted the mod template files.
9. The program will start downloading the required Minecraft files. This takes a few minutes. A progress bar will appear at the bottom of the screen.

### Forge Loader

1. Go to the [Forge Files](https://files.minecraftforge.net/net/minecraftforge/forge/index_1.20.1.html) website.
2. On the `Download Recommended` section, click on the `Mdk` button.
3. Extract the downloaded `.zip` file into a new empty folder on your computer.
4. Open IntelliJ IDEA.
5. Click `Open` and select the folder where you extracted the mod template files.
6. The program will start downloading the required Minecraft files. This takes a few minutes. A progress bar will appear at the bottom of the screen.

### Fabric Loader

1. Go to the [Fabric Template Mod Generator](https://fabricmc.net/develop/template/) website.
2. On this page, customize your `Mod Name` and `Package Name`.
3. Your `mod ID` will be set automatically or you can use a custom one, remember it.
4. Change the `Minecraft Version` dropdown to `1.20.1`.
5. In the `Advanced Options` section, make sure to select `Mojang Mappings`.
6. Click on the blue `Download Template (.ZIP)` button.

## 2. Configuring Dependencies

Your mod needs to be linked to Farmer's Delight and Delight Lib to work and use their features.

1. Open the file named `build.gradle` in your project folder. 
2. Open the CurseForge web pages for your specific Farmer's Delight loader and Delight Lib. You will need to copy code from both sites:
    1. [Farmer's Delight (Forge/NeoForge)](https://www.curseforge.com/minecraft/mc-mods/farmers-delight/files/all?page=1&pageSize=20&showAlphaFiles=hide) or [Farmer's Delight Refabricated (Fabric)](https://www.curseforge.com/minecraft/mc-mods/farmers-delight-refabricated/files/all?page=1&pageSize=20&showAlphaFiles=hide).
	2. [Delight Lib (All loaders)](https://www.curseforge.com/minecraft/mc-mods/delight-lib/files/all?page=1&pageSize=20&showAlphaFiles=hide).
3. Click the `Filter by` and choose your Minecraft version and loader, this will be the version and loader you will use for your add-on.
4. Click the top file in the list to view the most recent release.
5. Expand the `Curse Maven Snippet` section and use the copy button to save the `implementation` or `modImplementation` text, repeat this for both mods.
6. Find the `dependencies` block inside your `build.gradle` file.
7. Paste both copied text lines into that section:
```groovy
dependencies {
    implementation "curse.maven:farmers-delight-XXXXXXX:YYYYYYY"
    implementation "curse.maven:delight-lib-XXXXXXX:YYYYYYY"
}
````

8. Find the `repositories` block inside your `build.gradle` file.
9. Add `cursemaven` as a repository like this:
```groovy
repositories {  
    maven {  
        url "https://cursemaven.com"  
    }  
}
```

10. Open your metadata file, this file is located in your project's folder and it has a different name depending what loader you're using:

- Forge: `mods.toml` (located in `src/main/resources/META-INF`)
- NeoForge: `neoforge.mods.toml` (located in `src/main/templates/META-INF`)
- Fabric: `fabric.mod.json` (located in `src/main/resources`)

9. Add these code lines for your loader:

- Forge/NeoForge:
```toml
[[dependencies.${mod_id}]]
modId="farmersdelight"
type="required"
versionRange="*"
ordering="NONE"
side="BOTH"

[[dependencies.${mod_id}]]
modId="delightlib"
type="required"
versionRange="*"
ordering="NONE"
side="BOTH"
```

- Fabric:
```json
"depends": {
    // ...rest of the dependencies go here.
    "farmersdelight": "*",
    "delightlib": "*"
  }
```
## 3. Writing the Main Class

1. Go to the `src/main/java` folder in IntelliJ IDEA.
2. Open the main class file. It should have your mod's name on it or a generic name.
3. Write the following code and replace the `mymod` and `MyMod` values for your mod's name. You will have to replace your existing code in your main class:

- Forge:
```java
package com.example.examplemod; // Replace this line with your mod's name.

import com.axperty.delightlib.api.DelightAddon;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod("mymod")
public class MyMod {
    public MyMod(IEventBus bus, ModContainer container) {
        var addon = DelightAddon.create("mymod", bus)
            .withCreativeTab("My Mod", () -> new ItemStack(Items.BREAD));
            
        // The rest of your code will go here when you add custom items or blocks.
    }
}
```

- NeoForge:
```java
package com.example.examplemod; // Replace this line with your mod's name.

import com.axperty.delightlib.api.DelightAddon;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;

@Mod("mymod")
public class MyMod {
    public MyMod(IEventBus bus, ModContainer container) {
        var addon = DelightAddon.create("mymod", bus)
            .withCreativeTab("My Mod", () -> new ItemStack(Items.BREAD));
            
        // The rest of your code will go here when you add custom items or blocks.
    }
    private void commonSetup(final FMLCommonSetupEvent event) {}
}
```

- Fabric:
```java
package com.example.examplemod; // Replace this line with your mod's name.

import com.axperty.delightlib.api.DelightAddon;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import net.fabricmc.api.ModInitializer;

public class MyMod implements ModInitializer {
    public static final String MOD_ID = "mymod";
    public static DelightAddon addon;

    @Override
    public void onInitialize() {
        addon = DelightAddon.create(MOD_ID)
                .withCreativeTab("My Mod", () -> new ItemStack(Items.BREAD));
                
        // The rest of your code will go here when you add custom items or blocks.
        addon.build();
    }
}
```

4. You might have to add `imports` to the top of your class as you start adding items or blocks.
5. Do not use spaces when replacing `DelightAddon.create("mymod", bus)` for your mod's name! All names must be in lowercase.
6. Now that you set up your main class, you are ready to add custom items or blocks. In the next steps choose any item you want to add. You do not have to add all of them. These are examples.

# Customizing and Adding Things to Your Add-on

All the examples shown below can be customized as you like, properties can be removed in some cases but it's recommended you use all of them. Comments were added to the code to explain what every single thing does, you're free to remove them in your project.

## 1. Creating Knives

To add a basic knife:
```java
addon.knife("example_knife", Tiers.STONE).build(); // Creates a knife of stone tier
```

To add an advanced knife with custom stats:
```java
addon.knife("example_knife", Tiers.DIAMOND) // Creates a knife of diamond tier
  .attackDamage(1.5f) // Adds extra damage to the base knife damage
  .attackSpeed(-1.8f) // Sets how fast the weapon swings, negative numbers mean it's slower than an empty hand
  .fireResistant() // Prevents the item from burning up when dropped into lava or fire
  .build(); // Registers the item
```

## 2. Creating Food

To add a simple food item like a stew:
```java
addon.food("example_stew") // Creates a stew
  .nutrition(4) // Determines how many hunger points it restores
  .saturation(0.4f) // Sets the hidden value that keeps a player full longer
  .bowlFood() // Returns a bowl after eating
  .build(); // Registers the item
```

To add a cooking pot recipe for the stew:
```java
addon.cookingRecipe("example_stew") // Creates a new recipe for the cooking pot
  .addIngredient("minecraft:cooked_beef") // Adds a required ingredient
  .addIngredient("minecraft:carrot") // Adds a second required ingredient
  .addIngredient("minecraft:potato") // Adds a third required ingredient

  // You're able to add up to six total ingredients for your recipe

  .result("examplemod:example_stew") // Sets the final item you receive when cooking finishes
  .container("minecraft:bowl") // Requires a specific container to hold the cooked item
  .experience(1.0f) // Amount of XP received after cooking
  .cookingTime(200) // Sets the cook duration in ticks, 200 ticks equals 10 seconds
  .recipeBookTab("meals") // Places this recipe into the meals category in the recipe book
  .build(); // Registers the recipe
```

To add a feast food item:
```java
addon.placeableFood("example_feast") // Creates a food item you can place
  .feast("example_feast_serving") // Makes the feast drop servings when right-clicked
  .feastOutput("minecraft:bone") // Sets the item dropped when the feast has no more servings, leave it empty to disable.
  .build(); // Registers the block
```

To add a serving item:
```java
addon.food("example_feast_serving") // Creates a food item
  .nutrition(6) // Determines how many hunger points it restores
  .saturation(0.6f) // Sets the hidden value that keeps a player full longer
  .bowlFood() // Returns a bowl after eating
  .build(); // Registers the item
```

To add a pie item:
```java
addon.placeableFood("example_pie") // Creates a food item you can place
  .pie("example_pie_slice") // Makes the pie drop slices when right-clicked with a knife
  .build(); // Registers the block
```

To add a slice item:
```java
addon.food("example_pie_slice") // Creates a food item
  .nutrition(6) // Determines how many hunger points it restores
  .saturation(0.6f) // Sets the hidden value that keeps a player full longer
  .build(); // Registers the item
```

To add a juice item:
```java
addon.food("example_juice")
  .nutrition(2) // Determines how many hunger points it restores
  .saturation(0.2f) // Sets the hidden value that keeps a player full longer
  .drinkable() // Sets a drinking animation and returns a glass bottle as leftover
  .alwaysEdible() // Allows to consume this even if the hunger bar is full
  .build(); // Registers the item
```
## 3. Creating Crops

To add a crop like corn:
```java
addon.crop("example_crop") // Creates a new crop, crop seeds, and the crop when planted
  .build(); // Registers the crop
```

To add an edible crop:
```java
addon.crop("example_crop") // Creates a new crop, crop seeds, and the crop when planted
  .asFood(2, 0.3f) // Makes the raw crop edible, first number is nutrition and second is saturation
  .build(); // Registers the crop
```

To add a crop where the item is the seed like onions:
```java
addon.crop("example_crop") // Creates a new crop, crop seeds, and the crop when planted
  .asFood(1, 0.1f) // Makes the raw crop edible, first number is nutrition and second is saturation
  .seedIsItem() // Lets players plant the harvested crop directly instead of using seeds
  .build();
```
## 4. Creating Cabinets

To add a custom cabinet:
```java
addon.cabinet("example_cabinet") // Creates a cabinet
  .soundType(SoundType.WOOD) // Plays wood sound when broken
  .burnTime(300) // Allows the cabinet be used as fuel, 300 ticks equals 15 seconds
  .recipe(b -> b.grid("O  ", "T T", "OOO") // Defines the exact crafting shape in the crafting table
  .define('O', "minecraft:spruce_log") // Tells the game what item the letter O represents
  .define('T', "minecraft:spruce_trapdoor")) // Tells the game what item the letter T represents
  .build(); // Registers the cabinet
```

## 5. Creating Crates and Bags

To create a crate block:
```java
addon.crate("example_crate") // Creates a crate
    .recipe(b -> b.grid("CCC", "CCC", "CCC") // Defines the exact crafting shape in the crafting table
    .define('C', "examplemod:example_item")) // Tells the game what item the letter C represents
    .build(); // Registers the crate
```

To create a bag block:
```java
addon.bag("example_bag") // Creates a bag
    .recipe(b -> b.grid("BBB", "BBB", "BBB") // Defines the exact crafting shape in the crafting table
    .define('B', "examplemod:example_item")) // Tells the game what item the letter B represents
    .build(); // Registers the bag
```

## 6. Creating Crafting Table Recipes

To add a crafting table shaped recipe:
```java
addon.shapedRecipe("example_block") // Creates a shaped recipe
  .grid("SSS", "SWS", "CCC") // Defines the exact crafting shape in the crafting table
  .define('S', "minecraft:stone") // Tells the game what item the letter S represents
  .define('W', "minecraft:water_bucket") // Tells the game what item the letter W represents
  .defineTag('C', "forge:vegetables/carrot") // Tells the game what tag item the letter C represents
  .result("examplemod:example_food", 2) // Sets the final item you receive and the amount
  .build(); // Registers the recipe
```

To add a crafting table shapeless recipe:
```java
addon.shapelessRecipe("example_item") // Creates a shapeless recipe
  .addIngredient("minecraft:apple") // Adds a required ingredient
  .addIngredient("minecraft:wheat") // Adds a second required ingredient
  .addTagIngredient("forge:seeds") // Adds a third required tag ingredient
  .result("examplemod:example_food", 2) // Sets the final item you receive and the amount
  .build(); // Registers the recipe
```

You can add tag items or blocks using `.defineTag()` or `.addTagIngredient()`, make sure to check tags depending on what mod loader you're using:
- [Forge Tags](https://docs.minecraftforge.net/en/1.21.x/resources/server/tagslist/)
- [NeoForge Tags](https://nekoyue.github.io/ForgeJavaDocs-NG/javadoc/1.20.6-neoforge/net/neoforged/neoforge/common/Tags.Items.html)
- [Fabric Tags](https://github.com/FabricMC/fabric-api/tree/HEAD/fabric-convention-tags-v2/src/generated/resources/data/c/tags)

### Need help?

If you're getting errors when launching your game after creating recipes, can't find a tag, or you're just having a difficult time to understand how this works, don't worry, ask me for help on my [Discord server](https://discord.gg/z2E7Q78v8X), I'll be happy to assist.

# Finalizing Your Add-on

## 1. Generating Files

Minecraft needs files to understand blocks and items. Delight Lib creates most of them automatically. You must do this every time you add a new item or block.

1. Open the Terminal tab at the bottom of IntelliJ IDEA. Type this command depending what loader you're using:
   
- Forge: `gradle runData`
- NeoForge: `gradle runData`
- Fabric: `gradle runDatagen` *Doesn't work? The Fabric loader needs a bit more set up for data generation, [check here](https://wiki.fabricmc.net/tutorial:datagen_setup)](https://wiki.fabricmc.net/tutorial:datagen_setup).*

2. The code will generate your item models, translations, loot tables, and tags inside the `src/generated/resources` folder. This takes a few minutes. Do not add custom texture files here. They will be deleted automatically every time you generate data.
3. You will see a `BUILD SUCCESSFUL in Xs` message in the console. This means all files were created without any issues.

### Need help?

If you see a `BUILD FAILED` message in the console, you will be able to see what caused the issue. But sometimes these issues are hard solve if you're a beginner, if you couldn't fix the exact issue, don't worry, ask me for help on my [Discord server](https://discord.gg/z2E7Q78v8X), I'll be happy to assist.

## 2. Adding Custom Textures

1. Create the following folder structure inside `src/main/resources/assets/examplemod/textures` and then create two folders, `block` and `item`:
   
<img width="200" alt="texture_folder_structure" src="https://github.com/user-attachments/assets/a650745e-c91a-4acf-bc8a-53ac62cd31ac" />

2. When you create a custom crate, bag, or cabinet, Delight Lib will look for textures that contain these prefixes inside your `assets/examplemod/textures/block` folder, this applies to each one of the blocks that you add:

- Knives need a texture with their own custom name on it:
  - `assets/examplemod/textures/item/example_knife.png

- Food items need a texture with their own custom name on it:
  - `assets/examplemod/textures/item/example_stew.png`

- Crops need  a texture for every stage of growth, `_stage1`, `_stage2`, `_stage3`, and so on:
  - `assets/examplemod/textures/block/[custom_name]_crop_stage0.png`
  - `assets/examplemod/textures/block/[custom_name]_crop_stage1.png`
  - `assets/examplemod/textures/block/[custom_name]_crop_stage2.png`
  - `assets/examplemod/textures/block/[custom_name]_crop_stage3.png`
  - `assets/examplemod/textures/block/[custom_name]_crop_stage4.png`
  - `assets/examplemod/textures/block/[custom_name]_crop_stage5.png`
  - `assets/examplemod/textures/block/[custom_name]_crop_stage6.png`
  - `assets/examplemod/textures/block/[custom_name]_crop_stage7.png`

- Cabinets need a `_cabinet_front`, `_cabinet_front_open`, and `_cabinet_side`, and `_cabinet_top` texture:
  - `assets/examplemod/textures/block/[custom_name]_cabinet_front.png`
  - `assets/examplemod/textures/block/[custom_name]_cabinet_front_open.png`
  - `assets/examplemod/textures/block/[custom_name]_cabinet_side.png`
  - `assets/examplemod/textures/block/[custom_name]_cabinet_top.png`

- Crates need a `_side`, `_top`, and `_bottom` texture:
  - `assets/examplemod/textures/block/[custom_name]_crate_side.png`
  - `assets/examplemod/textures/block/[custom_name]_crate_top.png`
  - `assets/examplemod/textures/block/[custom_name]_crate_bottom.png`

- Bags need a `_side`, `_top`, and `_bottom` texture:
  - `assets/examplemod/textures/block/[custom_name]_bag_side.png`
  - `assets/examplemod/textures/block/[custom_name]_bag_top.png`
  - `assets/examplemod/textures/block/[custom_name]_bag_bottom.png`

- Placeable Food Items:
  -  These are technically blocks, because placeable foods can have different textures, shapes, and sizes this means you need to create them manually using [Blockbench](https://web.blockbench.net/). [Watch these series of tutorials](https://www.youtube.com/watch?v=dsax5p4brN8&list=PLvULVkjBtg2SezfUA8kHcPUGpxIS26uJR) if you want to learn how to use it. If you don't want to add placeable food items now, you can skip this explanation and add standard food items instead.
  - Once you have your own food model, place the all the texture files you used inside this folder. For example:

   - `assets/examplemod/textures/block/[custom_name]_bowl.png`
   - `assets/examplemod/textures/block/[custom_name]_food_texture.png`

  - Now you have to export your model files in [Blockbench](https://web.blockbench.net/) by going to `File` > `Export` > `Export Block/Item Model` and save the `.json` file inside this folder structure:

<img width="200" alt="models_folder_structure" src="https://github.com/user-attachments/assets/ce630d85-69cc-4f2e-a563-421e7a8431d8" />

  - You also need a item texture for your placeable food model, so it shows correctly in your inventory, once you created the texture, save it inside `assets/examplemod/textures/item`.

3. If for some reason, your items have a black and purple texture, this means Minecraft wasn't able to find the texture, check if the image files are in their correct location or if their file name doesn't have a typo.

## 3. Adding Custom Translations

English translations are added automatically when you generate files for your items and blocks. You cannot have two `en_us.json` files in your add-on. The game will not launch.

1. Inside `src/main/resources`, create a folder named `assets`. You might have this folder if you added custom textures. You do not need to create it again.
2. Create a `.json` file with the language you want to translate. Check the list of in-game locale codes on the [Minecraft wiki](https://minecraft.wiki/w/Language#Languages).
3. The complete folder path must look like this: `src/main/resources/assets/mymod/lang/xx_xx.json`.
4. Open the language file you created.
5. If you registered an item as `garlic_bread` and a block as `garlic_crate`, you must translate them like this:

```json
{
	"item.mymod.garlic_bread": "Pan de ajo",
	"block.mymod.garlic_crate": "Cajón de ajo"
}
```

## 4. Run Your Add-on

You can run the Minecraft client to test and see all the features you've added to your add-on by following these steps:

1. Open the Terminal tab at the bottom of IntelliJ IDEA.
2. Type this command: `gradle runClient
3. The Minecraft client will open.
4. Create a new world in singleplayer.
5. Open the inventory tab in creative mode.
6. Click the arrow on the right side.
7. You will see two tabs, the one with the stove icon is Farmer's Delight and the other one is your add-on.
8. Click on your add-on.
9. All items and blocks you created will be displayed.
10. Test all of your items and blocks carefully, it's recommended to test them in survival mode.

### Need help?
If you see a `BUILD FAILED` message in the console, you will be able to see what caused the issue. But sometimes these issues are hard solve if you're a beginner, if you couldn't fix the exact issue, don't worry, ask me for help on my [Discord server](https://discord.gg/z2E7Q78v8X), I'll be happy to assist.

## 5. Building Your Add-on

If you want to publish your add-on on major Minecraft modding platforms such as CurseForge and Modrinth, you will need a `.jar` file that lets other users download your add-on.

1. Make sure to copy and paste the  [Farmer's Delight](https://github.com/vectorwing/FarmersDelight/blob/1.21/LICENSE) and [Delight Lib](https://github.com/axperty/delightlib/blob/1.3.1/1.21-neoforge/LICENSE) licenses before publishing as it's required for legal use of this software. [See the license here](https://github.com/axperty/delightlib/blob/1.3.1/1.21-neoforge/LICENSE).
2. Open the Terminal tab at the bottom of IntelliJ IDEA.
3. Type this command: `gradle build`
4.  You will see a `BUILD SUCCESSFUL in Xs` message in the console. This means your add-on file was created without any issues.
5. You can find the compiled `.jar` file in `build/libs`.
6. The file you see there is ready to publish.
7. Make sure your add-on works correctly before uploading it. You can test your mod locally by typing this command in the Terminal tab: `gradle runClient` or by creating an instance in your preferred Minecraft launcher, it's highly recommended to use the CurseForge App or Modrinth App for this:
- [Download and install CurseForge App](https://www.curseforge.com/download/app) from their official website.
- [Download and install Modrinth App](https://modrinth.com/app) from their official website.
8. When uploading your mod's file to a Minecraft modding platform, make sure to add Farmer's Delight/Farmer's Delight Refabricated and Delight Lib as a required dependency.

Modrinth App might have issues if you're using a Linux-based distribution, if it's unstable, please try [Prism Launcher](https://prismlauncher.org/download).

### Need help?
If you see a `BUILD FAILED` message in the console, you will be able to see what caused the issue. But sometimes these issues are hard solve if you're a beginner, if you couldn't fix the exact issue, don't worry, ask me for help on my [Discord server](https://discord.gg/z2E7Q78v8X), I'll be happy to assist.

## 6. Things to Consider

1. Make sure that your add-on contains the [Farmer's Delight](https://github.com/vectorwing/FarmersDelight/blob/1.21/LICENSE) and [Delight Lib](https://github.com/axperty/delightlib/blob/1.3.1/1.21-neoforge/LICENSE) licenses in your project before publishing to avoid copyright issues or project removal. Licenses are already included in [Delight Lib Templates](https://github.com/axperty/delightlib-template/blob/2.4.1/1.20-fabric/LICENSE).
2. It's not guaranteed that you will get approval when uploading your project. Platforms like [CurseForge](https://www.curseforge.com/) and [Modrinth](https://www.modrinth.com) carefully review every new submission. They will check your project to ensure it follows their specific rules before making it available for players to download. Check the [mod authors terms in CurseForge](https://legal.overwolf.com/docs/curseforge/mod-authors-terms/) and [content rules on Modrinth](https://modrinth.com/legal/rules) before publishing.
3. Delight Lib or any of my projects are not associated in any way with Farmer's Delight, Mojang, or Microsoft. The developer of Delight Lib provides this software strictly as a tool. They do not monitor or control your actions. You are the only person accountable for any consequences that result from running the program. If your use of the code causes problems or violates platform rules, you bear the sole blame. The creator built this project with good intentions. The clear expectation is that you will apply the software ethically and legally. Using the tool to cause harm or disrupt other services directly violates the core purpose of the project. For more information, please check the [Delight Lib license](https://github.com/axperty/delightlib/blob/1.3.1/1.21-neoforge/LICENSE).
4. If you need help or have questions about Delight Lib, contact me on my [Discord server](https://discord.gg/z2E7Q78v8X).