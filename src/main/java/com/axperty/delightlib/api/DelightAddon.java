package com.axperty.delightlib.api;

import com.axperty.delightlib.api.builder.*;
import com.axperty.delightlib.internal.DelightCabinetBlock;
import com.axperty.delightlib.internal.DelightCabinetBlockEntity;
import com.axperty.delightlib.internal.DelightDataGenerator;
import com.google.gson.JsonObject;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tier;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import net.neoforged.neoforge.items.wrapper.InvWrapper;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.function.Supplier;

public class DelightAddon {
    private static final Logger LOGGER = LoggerFactory.getLogger(DelightAddon.class);

    private final String modId;
    private final IEventBus modEventBus;

    private final DeferredRegister<Item> items;
    private final DeferredRegister<Block> blocks;
    private final DeferredRegister<BlockEntityType<?>> blockEntityTypes;
    private final DeferredRegister<CreativeModeTab> creativeTabs;

    private final LinkedHashSet<Supplier<Item>> creativeTabItems = new LinkedHashSet<>();
    private final List<Supplier<Block>> cabinetBlocks = new ArrayList<>();
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

    private DelightAddon(String modId, IEventBus modEventBus) {
        this.modId = modId;
        this.modEventBus = modEventBus;
        this.items = DeferredRegister.create(Registries.ITEM, modId);
        this.blocks = DeferredRegister.create(Registries.BLOCK, modId);
        this.blockEntityTypes = DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, modId);
        this.creativeTabs = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, modId);
    }

    public static DelightAddon create(String modId, IEventBus modEventBus) {
        DelightAddon addon = new DelightAddon(modId, modEventBus);
        addon.init();
        LOGGER.info("Delight Lib initialized for: {}", modId);
        return addon;
    }

    public DelightAddon withCreativeTab(String title, Supplier<ItemStack> icon) {
        creativeTabs.register("tab", () -> CreativeModeTab.builder()
                .title(Component.literal(title))
                .icon(icon != null ? icon : () -> ItemStack.EMPTY)
                .displayItems((params, output) -> creativeTabItems.forEach(s -> output.accept(new ItemStack(s.get()))))
                .build()
        );
        return this;
    }

    // Builder factories

    public KnifeBuilder knife(String name, Tier tier) { return new KnifeBuilder(this, name, tier); }
    public FoodBuilder food(String name) { return new FoodBuilder(this, name); }
    public PlaceableFoodBuilder placeableFood(String name) { return new PlaceableFoodBuilder(this, name); }
    public CabinetBuilder cabinet(String name) { return new CabinetBuilder(this, name); }
    public CookingRecipeBuilder cookingRecipe(String name) { return new CookingRecipeBuilder(this, name); }
    public ShapedRecipeBuilder shapedRecipe(String name) { return new ShapedRecipeBuilder(this, name); }
    public ShapelessRecipeBuilder shapelessRecipe(String name) { return new ShapelessRecipeBuilder(this, name); }
    public CrateBuilder crate(String name) { return new CrateBuilder(this, name); }
    public BagBuilder bag(String name) { return new BagBuilder(this, name); }
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
        Supplier<Item> registered = items.register(name, supplier);
        creativeTabItems.add(registered);
        return registered;
    }

    public Supplier<Block> registerBlock(String name, Supplier<Block> supplier) {
        return blocks.register(name, supplier);
    }

    public Supplier<Item> getItem(String name) {
        if (name.contains(":")) {
            return () -> net.minecraft.core.registries.BuiltInRegistries.ITEM.get(net.minecraft.resources.ResourceLocation.parse(name));
        } else {
            return () -> net.minecraft.core.registries.BuiltInRegistries.ITEM.get(net.minecraft.resources.ResourceLocation.fromNamespaceAndPath(modId, name));
        }
    }

    public void addCabinetBlock(Supplier<Block> block) {
        cabinetBlocks.add(block);
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
    public List<PlaceableFoodInfo> getPlaceableFoodInfos() { return Collections.unmodifiableList(placeableFoodInfos); }

    // Internal

    private void init() {
        cabinetBlockEntityType = blockEntityTypes.register("cabinet", () -> {
            Block[] valid = cabinetBlocks.stream().map(Supplier::get).toArray(Block[]::new);
            BlockEntityType.BlockEntitySupplier<DelightCabinetBlockEntity> factory =
                    (pos, state) -> new DelightCabinetBlockEntity(cabinetBlockEntityType.get(), pos, state);
            return valid.length == 0
                    ? BlockEntityType.Builder.of(factory).build(null)
                    : BlockEntityType.Builder.of(factory, valid).build(null);
        });

        items.register(modEventBus);
        blocks.register(modEventBus);
        blockEntityTypes.register(modEventBus);
        creativeTabs.register(modEventBus);

        modEventBus.addListener((RegisterCapabilitiesEvent event) -> {
            if (!cabinetBlocks.isEmpty()) {
                event.registerBlockEntity(Capabilities.ItemHandler.BLOCK,
                        cabinetBlockEntityType.get(), (be, ctx) -> new InvWrapper(be));
            }
        });

        modEventBus.addListener((net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent event) ->
            event.enqueueWork(() -> cabinetBlocks.forEach(s -> {
                if (s.get() instanceof DelightCabinetBlock cab) cab.setBlockEntityType(cabinetBlockEntityType);
            }))
        );

        modEventBus.addListener((GatherDataEvent event) ->
            event.getGenerator().addProvider(true,
                    new DelightDataGenerator(event.getGenerator().getPackOutput(), this))
        );
    }

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
