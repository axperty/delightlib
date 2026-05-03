package com.axperty.delightlib;

import com.axperty.delightlib.api.DelightAddon;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.minecraft.client.renderer.RenderType;

public class DelightLibClient {
    @Environment(EnvType.CLIENT)
    public static void registerClient(DelightAddon addon) {
        addon.getCutoutBlocks().forEach(block ->
            BlockRenderLayerMap.INSTANCE.putBlock(block.get(), RenderType.cutout())
        );
    }
}
