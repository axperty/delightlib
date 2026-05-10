package com.axperty.delightlib.api.builder;

import com.axperty.delightlib.api.DelightAddon;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Tier;
import vectorwing.farmersdelight.common.item.KnifeItem;

import java.util.function.Supplier;

public class KnifeBuilder {
    private final DelightAddon addon;
    private final String name;
    private final Tier tier;
    private float attackDamage = 0.5F;
    private float attackSpeed = -2.0F;
    private boolean fireResistant = false;

    public KnifeBuilder(DelightAddon addon, String name, Tier tier) {
        this.addon = addon;
        this.name = name;
        this.tier = tier;
    }

    /**
     * Sets the attack damage of the knife. This is added on top of the base damage provided by the material tier. For example, a value of 0.5 will make the knife do 0.5 more damage than a sword made of the same material.
     *
     * @param damage the additional attack damage of the knife, added on top of the material's base damage. For example, a value of 0.5 will make the knife do 0.5 more damage than a sword made of the same material.
     * @return this builder for chaining
     */
    public KnifeBuilder attackDamage(float damage) {
        this.attackDamage = damage;
        return this;
    }

    /**
     * Sets the attack speed of the knife. This is added on top of the base speed provided by the material tier. For example, a value of -1.0 will make the knife attack 1 second slower than a sword made of the same material, while a value of 1.0 will make it attack 1 second faster.
     *
     * @param speed the additional attack speed of the knife, added on top of the material's base speed. For example, a value of -1.0 will make the knife attack 1 second slower than a sword made of the same material, while a value of 1.0 will make it attack 1 second faster.
     * @return this builder for chaining
     */
    public KnifeBuilder attackSpeed(float speed) {
        this.attackSpeed = speed;
        return this;
    }

    /**
     * Makes the knife fire resistant, meaning it won't burn in lava or fire and can be used to extinguish fires. By default, knives are not fire resistant.
     *
     * @return this builder for chaining
     */
    public KnifeBuilder fireResistant() {
        this.fireResistant = true;
        return this;
    }

    /**
     * Builds the knife item and registers it. After calling this method, the knife will be available in-game and can be used like any other item. Note that you should only call this method once per knife, as it registers the item with the addon.
     *
     * @return a supplier that provides the registered knife item. This can be used to reference the item in other builders or for any other purpose where you need a reference to the item instance.
     */
    public Supplier<Item> build() {
        addon.trackKnife(name);
        final float ad = attackDamage, as = attackSpeed;
        final boolean fr = fireResistant;
        final Tier t = tier;

        return addon.registerItem(name, () -> {
            Item.Properties props = new Item.Properties().attributes(KnifeItem.createAttributes(t, ad, as));
            if (fr) props = props.fireResistant();
            return new KnifeItem(t, props);
        });
    }
}
