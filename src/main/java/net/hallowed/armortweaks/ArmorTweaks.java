package net.hallowed.armortweaks;

import net.minecraftforge.event.OnDatapackSyncEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

@Mod(ArmorTweaks.MODID)
public class ArmorTweaks {
    public static final String MODID = "armortweaks";


    @Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
    public static final class ModBus {
        @SubscribeEvent
        public static void onCommonSetup(final FMLCommonSetupEvent event) {
            ArmorOverrides.load();
        }
    }

    @Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
    public static final class ForgeBus {
        @SubscribeEvent
        public static void onDatapackSync(final OnDatapackSyncEvent e) {
            ArmorOverrides.load();
        }
    }
}
