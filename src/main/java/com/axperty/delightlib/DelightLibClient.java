package com.axperty.delightlib;

import com.axperty.delightlib.api.DelightAddon;
import net.fabricmc.api.ClientModInitializer;


public class DelightLibClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {}

    public static void registerClient(DelightAddon addon) {
        // Cutout blocks are now handled via datagen model json (render_type = cutout)
    }
}