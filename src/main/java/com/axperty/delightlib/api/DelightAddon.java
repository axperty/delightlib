package com.axperty.delightlib.api;

import com.axperty.delightlib.api.builder.*;
import com.axperty.delightlib.internal.DelightCabinetBlock;
import com.axperty.delightlib.internal.DelightCabinetBlockEntity;
import com.google.gson.JsonObject;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.fabricmc.fabric.api.transfer.v1.item.InventoryStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemStorage;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tier;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.function.Supplier;

public class DelightAddon implements DelightApi {
    private static final Logger LOGGER = LoggerFactory.getLogger(DelightAddon.class);

    private final String modId;

    private final LinkedHashSet<Supplier<Item>> creativeTabItems = new LinkedHashSet<>();
    private final List<Supplier<Block>> cabinetBlocks = new ArrayList<>();
    private final List<Supplier<Block>> cutoutBlocks = new ArrayList<>();
    private Supplier<BlockEntityType<DelightCabinetBlockEntity>> cabinetBlockEntityType;
    private final Map<String, JsonObject> recipes = new LinkedHashMap<>();

    private final List<String> knifeNames = new ArrayList<>();
    private final List<String> cabinetNames = new ArrayList<>();
    private final List<String> blockNames = new ArrayList<>();
    private final List<String> crateNames = new ArrayList<>();
    private final List<String> bagNames = new ArrayList<>();
    private final Map<String, String> itemModelParents = new LinkedHashMap<>();
    private final Map<String, String> langEntries = new LinkedHashMap<>();
    private final List<CropInfo> cropInfos = new ArrayList<>();
    private final List<PlaceableFoodInfo> placeableFoodInfos = new ArrayList<>();
    
    private CreativeModeTab tab;
    private String tabTitle;
    private Supplier<ItemStack> tabIcon;

    private DelightAddon(String modId) {
        this.modId = modId;
    }

    @Deprecated
    public static DelightAddon create(String modId) {
        return (DelightAddon) DelightApi.create(modId);
    }

    static DelightAddon internalCreate(String modId) {
        DelightAddon addon = new DelightAddon(modId);
        LOGGER.info("Delight Lib initialized for: {}", modId);
        return addon;
    }

    @Override
    /* {@inheritDoc} */
    public DelightAddon withCreativeTab(String title, Supplier<ItemStack> icon) {
        this.tabTitle = title;
        this.tabIcon = icon != null ? icon : () -> ItemStack.EMPTY;
        return this;
    }

    public void build() {
        if (tabTitle != null) {
            tab = Registry.register(BuiltInRegistries.CREATIVE_MODE_TAB, new ResourceLocation(modId, "tab"), 
                FabricItemGroup.builder()
                    .title(Component.literal(tabTitle))
                    .icon(tabIcon)
                    .displayItems((params, output) -> creativeTabItems.forEach(s -> output.accept(new ItemStack(s.get()))))
                    .build()
            );
        }

        if (!cabinetBlocks.isEmpty()) {
            Block[] valid = cabinetBlocks.stream().map(Supplier::get).toArray(Block[]::new);
            
            BlockEntityType<DelightCabinetBlockEntity> type = Registry.register(
                BuiltInRegistries.BLOCK_ENTITY_TYPE,
                new ResourceLocation(modId, "cabinet"),
                FabricBlockEntityTypeBuilder.create((pos, state) -> new DelightCabinetBlockEntity(cabinetBlockEntityType.get(), pos, state), valid).build(null)
            );
            
            cabinetBlockEntityType = () -> type;

            ItemStorage.SIDED.registerForBlockEntities(
                (be, direction) -> InventoryStorage.of((net.minecraft.world.Container) be, direction),
                type
            );

            cabinetBlocks.forEach(s -> {
                if (s.get() instanceof DelightCabinetBlock cab) cab.setBlockEntityType(cabinetBlockEntityType);
            });
        }
    }

    // Builder factories

    @Override
    /* {@inheritDoc} */
    public KnifeBuilder knife(String name, Tier tier) { return new KnifeBuilder(this, name, tier); }

    @Override
    /* {@inheritDoc} */
    public FoodBuilder food(String name) { return new FoodBuilder(this, name); }

    @Override
    /* {@inheritDoc} */
    public PlaceableFoodBuilder placeableFood(String name) { return new PlaceableFoodBuilder(this, name); }

    @Override
    /* {@inheritDoc} */
    public CookingRecipeBuilder cookingRecipe(String name) { return new CookingRecipeBuilder(this, name); }

    @Override
    /* {@inheritDoc} */
    public ShapedRecipeBuilder shapedRecipe(String name) { return new ShapedRecipeBuilder(this, name); }

    @Override
    /* {@inheritDoc} */
    public ShapelessRecipeBuilder shapelessRecipe(String name) { return new ShapelessRecipeBuilder(this, name); }

    @Override
    /* {@inheritDoc} */
    public CabinetBuilder cabinet(String name) { return new CabinetBuilder(this, name); }

    @Override
    /* {@inheritDoc} */
    public CrateBuilder crate(String name) { return new CrateBuilder(this, name); }

    @Override
    /* {@inheritDoc} */
    public BagBuilder bag(String name) { return new BagBuilder(this, name); }

    @Override
    /* {@inheritDoc} */
    public CropBuilder crop(String name) { return new CropBuilder(this, name); }

    // Content tracking

    public void trackKnife(String name) {
        knifeNames.add(name);
        itemModelParents.put(name, "minecraft:item/handheld");
        langEntries.put("item." + modId + "." + name, toTitleCase(name));
    }

    public void trackFood(String name) {
        itemModelParents.put(name, "minecraft:item/generated");
        langEntries.put("item." + modId + "." + name, toTitleCase(name));
    }

    public void trackCabinet(String name) {
        cabinetNames.add(name);
        blockNames.add(name);
        langEntries.put("block." + modId + "." + name, toTitleCase(name));
        langEntries.put("container." + modId + "." + name, toTitleCase(name));
    }

    public void trackPlaceableFood(String name, PlaceableFoodInfo.FoodType type, Supplier<Item> sliceItem, Supplier<Item> servingItem, Supplier<Item> feastOutputItem) {
        placeableFoodInfos.add(new PlaceableFoodInfo(name, type, sliceItem, servingItem, feastOutputItem));
        blockNames.add(name);
        langEntries.put("block." + modId + "." + name, toTitleCase(name));
        langEntries.put("item." + modId + "." + name, toTitleCase(name));
    }

    public void trackCrate(String name) {
        blockNames.add(name);
        crateNames.add(name);
        langEntries.put("block." + modId + "." + name, toTitleCase(name));
    }

    public void trackBag(String name) {
        blockNames.add(name);
        bagNames.add(name);
        langEntries.put("block." + modId + "." + name, toTitleCase(name));
    }

    public void trackCrop(String cropName, String seedName, String blockName, boolean seedIsItem) {
        cropInfos.add(new CropInfo(cropName, seedName, blockName, seedIsItem));
        itemModelParents.put(cropName, "minecraft:item/generated");
        langEntries.put("item." + modId + "." + cropName, toTitleCase(cropName));
        if (!seedIsItem) {
            itemModelParents.put(seedName, "minecraft:item/generated");
            langEntries.put("item." + modId + "." + seedName, toTitleCase(seedName));
        }
        langEntries.put("block." + modId + "." + blockName, toTitleCase(blockName));
    }

    // Registry helpers

    public Supplier<Item> registerItem(String name, Supplier<Item> supplier) {
        Item item = Registry.register(BuiltInRegistries.ITEM, new ResourceLocation(modId, name), supplier.get());
        Supplier<Item> registered = () -> item;
        creativeTabItems.add(registered);
        return registered;
    }

    public Supplier<Block> registerBlock(String name, Supplier<Block> supplier) {
        Block block = Registry.register(BuiltInRegistries.BLOCK, new ResourceLocation(modId, name), supplier.get());
        return () -> block;
    }

    public Supplier<Item> getItem(String name) {
        if (name.contains(":")) {
            return () -> BuiltInRegistries.ITEM.get(new ResourceLocation(name));
        } else {
            return () -> BuiltInRegistries.ITEM.get(new ResourceLocation(modId, name));
        }
    }

    public void addCabinetBlock(Supplier<Block> block) {
        cabinetBlocks.add(block);
    }

    public void addCutoutBlock(Supplier<Block> block) {
        cutoutBlocks.add(block);
    }

    public void addRecipe(String path, JsonObject json) {
        recipes.put(path, json);
    }

    // Getters

    public String getModId() { return modId; }
    public List<String> getKnifeNames() { return Collections.unmodifiableList(knifeNames); }
    public List<String> getCabinetNames() { return Collections.unmodifiableList(cabinetNames); }
    public List<String> getCrateNames() { return Collections.unmodifiableList(crateNames); }
    public List<String> getBagNames() { return Collections.unmodifiableList(bagNames); }
    public List<String> getBlockNames() { return Collections.unmodifiableList(blockNames); }
    public Map<String, String> getItemModelParents() { return Collections.unmodifiableMap(itemModelParents); }
    public Map<String, String> getLangEntries() { return Collections.unmodifiableMap(langEntries); }
    public Map<String, JsonObject> getRecipes() { return Collections.unmodifiableMap(recipes); }
    public List<CropInfo> getCropInfos() { return Collections.unmodifiableList(cropInfos); }
    public List<Supplier<Block>> getCutoutBlocks() { return Collections.unmodifiableList(cutoutBlocks); }
    public List<PlaceableFoodInfo> getPlaceableFoodInfos() { return Collections.unmodifiableList(placeableFoodInfos); }

    private static String toTitleCase(String name) {
        String[] words = name.split("_");
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < words.length; i++) {
            if (i > 0) sb.append(" ");
            sb.append(Character.toUpperCase(words[i].charAt(0))).append(words[i].substring(1));
        }
        return sb.toString();
    }
}
