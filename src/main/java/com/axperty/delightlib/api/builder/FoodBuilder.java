package com.axperty.delightlib.api.builder;

import com.axperty.delightlib.api.DelightAddon;
import com.axperty.delightlib.api.FoodDuration;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.Consumable;
import net.minecraft.world.item.component.Consumables;
import net.minecraft.world.item.consume_effects.ApplyStatusEffectsConsumeEffect;
import vectorwing.farmersdelight.common.item.ConsumableItem;
import vectorwing.farmersdelight.common.registry.ModEffects;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class FoodBuilder {
    private final DelightAddon addon;
    private final String name;
    private int nutrition = 4;
    private float saturation = 0.4f;
    private boolean fast = false;
    private boolean alwaysEdible = false;
    private boolean isDrinkable = false;
    private Item craftRemainder = null;
    private int maxStackSize = 64;
    private boolean hasFoodEffectTooltip = true;
    private boolean hasCustomTooltip = false;
    private final List<EffectEntry> effects = new ArrayList<>();
    private boolean isStackSizeSetManually = false;

    private record EffectEntry(Supplier<MobEffectInstance> effect, float chance) {}

    public FoodBuilder(DelightAddon addon, String name) {
        this.addon = addon;
        this.name = name;
    }

    public FoodBuilder nutrition(int nutrition) { this.nutrition = nutrition; return this; }
    public FoodBuilder saturation(float saturation) { this.saturation = saturation; return this; }
    public FoodBuilder fast() { this.fast = true; return this; }
    public FoodBuilder alwaysEdible() { this.alwaysEdible = true; return this; }

    public FoodBuilder bowlFood() {
        internalStacksTo(16);
        return craftRemainder(Items.BOWL);
    }

    public FoodBuilder drinkable() {
        this.isDrinkable = true;
        internalStacksTo(16);
        return craftRemainder(Items.GLASS_BOTTLE);
    }

    public FoodBuilder craftRemainder(Item remainder) {
        this.craftRemainder = remainder;
        return this;
    }

    private FoodBuilder internalStacksTo(int size) {
        if (!isStackSizeSetManually) {
            this.maxStackSize = size;
        }
        return this;
    }

    public FoodBuilder stacksTo(int size) {
        this.isStackSizeSetManually = true;
        this.maxStackSize = size;
        return this;
    }

    public FoodBuilder hideEffectTooltip() { this.hasFoodEffectTooltip = false; return this; }
    public FoodBuilder customTooltip() { this.hasCustomTooltip = true; return this; }

    public FoodBuilder withEffect(Holder<MobEffect> effect, int duration, int amplifier, float chance) {
        effects.add(new EffectEntry(() -> new MobEffectInstance(effect, duration, amplifier), chance));
        return this;
    }

    public FoodBuilder withNourishment(FoodDuration duration) {
        int ticks = duration.getTicks();
        effects.add(new EffectEntry(() -> new MobEffectInstance(ModEffects.NOURISHMENT, ticks, 0, false, false), 1.0f));
        return this;
    }

    public Supplier<Item> build() {
        addon.trackFood(name);

        FoodProperties.Builder foodBuilder = new FoodProperties.Builder()
                .nutrition(nutrition).saturationModifier(saturation);
        if (alwaysEdible) foodBuilder.alwaysEdible();

        final FoodProperties food = foodBuilder.build();
        final boolean effectTooltip = hasFoodEffectTooltip;
        final boolean customTip = hasCustomTooltip;
        final Item remainder = craftRemainder;
        final int stack = maxStackSize;

        Consumable.Builder consumableBuilder = isDrinkable ?
                Consumables.defaultDrink() :
                Consumables.defaultFood();
        if (fast) consumableBuilder.consumeSeconds(0.8F);
        for (EffectEntry e : effects) {
            consumableBuilder.onConsume(new ApplyStatusEffectsConsumeEffect(e.effect.get(), e.chance));
        }
        final Consumable consumable = consumableBuilder.build();

        return addon.registerItem(name, () -> {
            Item.Properties props = addon.defaultItemProperties(name).food(food).component(DataComponents.CONSUMABLE, consumable).stacksTo(stack);
            if (remainder != null) props = props.craftRemainder(remainder);
            return new ConsumableItem(props, effectTooltip, customTip);
        });
    }
}