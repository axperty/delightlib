package com.axperty.delightlib.api.builder;

import com.axperty.delightlib.api.DelightAddon;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ToolMaterial;
import vectorwing.farmersdelight.common.item.KnifeItem;
import vectorwing.farmersdelight.common.registry.ModItems;

import java.util.function.Supplier;

public class KnifeBuilder {
    private final DelightAddon addon;
    private final String name;
    private final ToolMaterial tier;
    private float attackDamage = 0.5F;
    private float attackSpeed = -2.0F;
    private boolean fireResistant = false;

    public KnifeBuilder(DelightAddon addon, String name, ToolMaterial tier) {
        this.addon = addon;
        this.name = name;
        this.tier = tier;
    }

    public KnifeBuilder attackDamage(float damage) { this.attackDamage = damage; return this; }
    public KnifeBuilder attackSpeed(float speed) { this.attackSpeed = speed; return this; }
    public KnifeBuilder fireResistant() { this.fireResistant = true; return this; }

    public Supplier<Item> build() {
        addon.trackKnife(name);
        final boolean fr = fireResistant;
        final ToolMaterial t = tier;

        return addon.registerItem(name, () -> {
            Item.Properties props = ModItems.knifeItem(t)
                .setId(ResourceKey.create(BuiltInRegistries.ITEM.key(), Identifier.fromNamespaceAndPath(addon.getModId(), name)));
            if (fr) props = props.fireResistant();
            return new KnifeItem(props);
        });
    }
}
