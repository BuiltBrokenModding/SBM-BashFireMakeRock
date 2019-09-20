package com.builtbroken.bashrockmakefire;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.builtbroken.bashrockmakefire.logic.EventHandler;

import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.server.FMLServerAboutToStartEvent;
import net.minecraftforge.fml.event.server.FMLServerStoppedEvent;

@Mod(BashFireMakeRock.MODID)
@Mod.EventBusSubscriber
public class BashFireMakeRock
{
    public static final String MODID = "bashfiremakerock";

    //Version injection data
    public static final String MAJOR_VERSION = "@MAJOR@";
    public static final String MINOR_VERSION = "@MINOR@";
    public static final String REVISION_VERSION = "@REVIS@";
    public static final String BUILD_VERSION = "@BUILD@";
    public static final String MC_VERSION = "@MC@";
    public static final String VERSION = MC_VERSION + "-" + MAJOR_VERSION + "." + MINOR_VERSION + "." + REVISION_VERSION + "." + BUILD_VERSION;

    public static final Logger LOGGER = LogManager.getLogger(MODID);

    //Config ain't being generated but the code can somehow fetch its values
    public BashFireMakeRock()
    {
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, ConfigMain.CONFIG_SPEC);
    }

    @SubscribeEvent
    public static void serverStart(FMLServerAboutToStartEvent event)
    {
        EventHandler.clickDataHashMap.clear();
        ConfigMain.CONFIG.initBlockData();
    }

    @SubscribeEvent
    public static void serverStop(FMLServerStoppedEvent event)
    {
        EventHandler.clickDataHashMap.clear();
    }
}
