package com.axperty.delightlib.api.builder;

import com.axperty.delightlib.api.DelightAddon;
import com.axperty.delightlib.api.FoodDuration;
import net.minecraft.core.Holder;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.Consumable;
import net.minecraft.world.item.component.Consumables;
import net.minecraft.world.item.consume_effects.ApplyStatusEffectsConsumeEffect;
import net.minecraft.core.component.DataComponents;
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

    public FoodBuilder(DelightAddon addon, String name) {
        this.addon = addon;
        this.name = name;
    }

    /**
     * Sets the nutrition value of this food. This is the amount of hunger points that will be restored when consuming this food. For reference, a cooked porkchop has a nutrition of 8, while an apple has a nutrition of 4.
     *
     * @param nutrition the amount of hunger points restored by this food when consumed. For reference, a cooked porkchop has a nutrition of 8, while an apple has a nutrition of 4.
     * @return this builder for chaining
     */
    public FoodBuilder nutrition(int nutrition) {
        this.nutrition = nutrition;
        return this;
    }

    /**
     * Sets the saturation modifier of this food. This is a multiplier applied to the nutrition value to determine how much saturation is restored when consuming this food. For example, a saturation of 0.5 will restore half as much saturation as the nutrition value, while a saturation of 1.0 will restore the same amount of saturation as the nutrition value. For reference, a cooked porkchop has a saturation of 0.8, while an apple has a saturation of 0.3.
     *
     * @param saturation the saturation modifier of this food, which is a multiplier applied to the nutrition value to determine how much saturation is restored when consuming this food. For example, a saturation of 0.5 will restore half as much saturation as the nutrition value, while a saturation of 1.0 will restore the same amount of saturation as the nutrition value. For reference, a cooked porkchop has a saturation of 0.8, while an apple has a saturation of 0.3.
     * @return this builder for chaining
     */
    public FoodBuilder saturation(float saturation) {
        this.saturation = saturation;
        return this;
    }

    /**
     * Makes this food fast to eat, meaning it will be consumed in 16 ticks (0.8 seconds) instead of the default 32 ticks (1.6 seconds). This is useful for foods that are meant to be eaten quickly, like snacks or small bites.
     *
     * @return this builder for chaining
     */
    public FoodBuilder fast() {
        this.fast = true;
        return this;
    }

    /**
     * Makes this food always edible, meaning it can be consumed even when the player is not hungry. By default, foods can only be consumed when the player's hunger bar is not full, but setting this will allow the food to be eaten regardless of the hunger level. This is useful for foods that provide benefits other than hunger restoration, such as healing or buffs.
     *
     * @return this builder for chaining
     */
    public FoodBuilder alwaysEdible() {
        this.alwaysEdible = true;
        return this;
    }

    /**
     * Defines this food as a bowl food, meaning that when consumed, it will leave behind an empty bowl in the player's inventory. This is useful for foods that are meant to be served in bowls, like soups or stews. Note that this will set the maximum stack size of this food to 16, since bowl foods cannot be stacked beyond that due to the way the crafting remainder works. If you want to have a different stack size, you can call stacksTo() after this to override it.
     *
     * @return this builder for chaining
     */
    public FoodBuilder bowlFood() {
        return internalStacksTo(16)
                .craftRemainder(Items.BOWL);
    }

    /**
     * Defines this food as a drink, meaning that it will be consumed like a potion and will leave behind an empty glass bottle in the player's inventory. This is useful for foods that are meant to be consumed as drinks, like juices or potions. Note that this will set the maximum stack size of this food to 16, since drinkable foods cannot be stacked beyond that due to the way the crafting remainder works. If you want to have a different stack size, you can call stacksTo() after this to override it.
     *
     * @return this builder for chaining
     */
    public FoodBuilder drinkable() {
        this.isDrinkable = true;
        return internalStacksTo(16).craftRemainder(Items.GLASS_BOTTLE);
    }

    /**
     * Sets the crafting remainder of this food. The crafting remainder is the item that will be left behind in the player's inventory when this food is consumed. This is useful for foods that are meant to be served in containers, like bowls or bottles, but can also be used for any food if you want to have a specific item left behind when consumed. Note that setting a crafting remainder will limit the maximum stack size of this food to 16, since foods with crafting remainders cannot be stacked beyond that due to the way the crafting remainder works. If you want to have a different stack size, you can call stacksTo() after this to override it.
     *
     * @param remainder the item that will be left behind in the player's inventory when this food is consumed. This is useful for foods that are meant to be served in containers, like bowls or bottles, but can also be used for any food if you want to have a specific item left behind when consumed. Note that setting a crafting remainder will limit the maximum stack size of this food to 16, since foods with crafting remainders cannot be stacked beyond that due to the way the crafting remainder works. If you want to have a different stack size, you can call stacksTo() after this to override it.
     * @return this builder for chaining
     */
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

    /**
     * Sets the maximum stack size of this food. By default, foods have a maximum stack size of 64, but certain properties like being a bowl food or having a crafting remainder will limit the stack size to 16. If you want to have a different stack size, you can call this method to override the default or property-imposed stack size. Note that setting a stack size larger than 16 for foods with crafting remainders or bowl foods may cause issues with the crafting remainder behavior, so use this with caution if you have those properties set.
     *
     * @param size the maximum stack size for this food. By default, foods have a maximum stack size of 64, but certain properties like being a bowl food or having a crafting remainder will limit the stack size to 16. If you want to have a different stack size, you can call this method to override the default or property-imposed stack size. Note that setting a stack size larger than 16 for foods with crafting remainders or bowl foods may cause issues with the crafting remainder behavior, so use this with caution if you have those properties set.
     * @return this builder for chaining
     */
    public FoodBuilder stacksTo(int size) {
        isStackSizeSetManually = true;
        this.maxStackSize = size;
        return this;
    }

    /**
     * Hides the food effect tooltip for this food. By default, foods that have effects will show a tooltip listing those effects when you hover over the food in the inventory. If you want to hide that tooltip for this food, you can call this method. This is useful for foods that have effects but you don't want to reveal them in the tooltip, or for foods that have custom tooltips and you want to avoid showing the default effect tooltip.
     *
     * @return this builder for chaining
     */
    public FoodBuilder hideEffectTooltip() {
        this.hasFoodEffectTooltip = false;
        return this;
    }

    /**
     * Enables a custom tooltip for this food. By default, foods will show the standard tooltip with the name, nutrition, saturation, and effects. If you want to provide a completely custom tooltip for this food (for example, using a custom ItemStackTooltipComponent), you can call this method to indicate that this food has a custom tooltip. This will prevent the default tooltip from being shown and allow your custom tooltip to be displayed instead. Note that you will need to implement the logic for providing the custom tooltip yourself, as this method only indicates that there is a custom tooltip without defining what it is.
     *
     * @return this builder for chaining
     */
    public FoodBuilder customTooltip() {
        this.hasCustomTooltip = true;
        return this;
    }

    /**
     * Adds a mob effect to this food. When the food is consumed, it will apply the given mob effect to the player with the specified duration, amplifier, and chance. The duration is in ticks (20 ticks = 1 second), and the amplifier is the level of the effect (0 for level 1, 1 for level 2, etc.). The chance is a float between 0.0 and 1.0 representing the probability that the effect will be applied when consuming the food. For example, a chance of 0.5 means there is a 50% chance that the effect will be applied when consuming the food.
     *
     * @param effect    the mob effect to apply when consuming this food. This should be a Holder<MobEffect> that references the desired mob effect, such as MobEffects.MOVEMENT_SPEED or a custom effect from your mod.
     * @param duration  the duration of the effect in ticks (20 ticks = 1 second). For example, a duration of 200 would mean the effect lasts for 10 seconds.
     * @param amplifier the amplifier (level) of the effect, where 0 is level 1, 1 is level 2, etc. For example, an amplifier of 1 would mean the effect is applied at level 2.
     * @param chance    the probability that the effect will be applied when consuming the food, as a float between 0.0 and 1.0. For example, a chance of 0.5 means there is a 50% chance that the effect will be applied when consuming the food.
     * @return this builder for chaining
     */
    public FoodBuilder withEffect(Holder<MobEffect> effect, int duration, int amplifier, float chance) {
        effects.add(new EffectEntry(() -> new MobEffectInstance(effect, duration, amplifier), chance));
        return this;
    }

    /**
     * Adds the nourishment mob effect to this food with a 100% chance of being applied
     *
     * @param duration the duration of the effect in ticks (20 ticks = 1 second). For example, a duration of 200 would mean the effect lasts for 10 seconds.
     * @return this builder for chaining
     */
    public FoodBuilder withNourishment(FoodDuration duration) {
        int ticks = duration.getTicks();
        effects.add(new EffectEntry(() -> new MobEffectInstance(ModEffects.NOURISHMENT, ticks, 0, false, false), 1.0f));
        return this;
    }

    /**
     * Builds the food item and registers it. After calling this method, the food will be available in-game and can be used like any other item. Note that you should only call this method once per food, as it registers the item with the addon.
     *
     * @return a supplier that provides the registered food item. This can be used to reference the item in other builders or for any other purpose where you need a reference to the item instance.
     */
    public Supplier<Item> build() {
        addon.trackFood(name);

        FoodProperties.Builder foodBuilder = new FoodProperties.Builder()
                .nutrition(nutrition).saturationModifier(saturation);
        if (alwaysEdible) foodBuilder.alwaysEdible();

        final FoodProperties food = foodBuilder.build();

        Consumable.Builder consumableBuilder = isDrinkable ? Consumables.defaultDrink() : Consumables.defaultFood();
        if (fast) consumableBuilder.consumeSeconds(0.8F);
        for (EffectEntry e : effects) {
            consumableBuilder.onConsume(new ApplyStatusEffectsConsumeEffect(e.effect.get(), e.chance));
        }
        final Consumable consumable = consumableBuilder.build();

        final boolean effectTooltip = hasFoodEffectTooltip;
        final boolean customTip = hasCustomTooltip;
        final Item remainder = craftRemainder;
        final int stack = maxStackSize;

        return addon.registerItem(name, () -> {
            Item.Properties props = new Item.Properties().setId(addon.itemKey(name)).food(food).component(DataComponents.CONSUMABLE, consumable).stacksTo(stack);
            if (remainder != null) props = props.craftRemainder(remainder);
            return new ConsumableItem(props, effectTooltip, customTip);
        });
    }

    private record EffectEntry(Supplier<MobEffectInstance> effect, float chance) {
    }
}
