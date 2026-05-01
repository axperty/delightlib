package com.axperty.delightlib;

import com.mojang.logging.LogUtils;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import org.slf4j.Logger;

@Mod(DelightLib.MOD_ID)
public class DelightLib {
    public static final String MOD_ID = "delightlib";
    private static final Logger LOGGER = LogUtils.getLogger();

    public DelightLib(IEventBus modEventBus, ModContainer modContainer) {
        LOGGER.info("Delight Lib loaded");
    }
}
