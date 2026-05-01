package com.axperty.delightlib;

import com.mojang.logging.LogUtils;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.fml.common.Mod;
import org.slf4j.Logger;

import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(DelightLib.MOD_ID)
public class DelightLib {
    public static final String MOD_ID = "delightlib";
    private static final Logger LOGGER = LogUtils.getLogger();

    public DelightLib() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        LOGGER.info("Delight Lib loaded");
    }
}
