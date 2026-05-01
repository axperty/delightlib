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

    public KnifeBuilder attackDamage(float damage) { this.attackDamage = damage; return this; }
    public KnifeBuilder attackSpeed(float speed) { this.attackSpeed = speed; return this; }
    public KnifeBuilder fireResistant() { this.fireResistant = true; return this; }

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
