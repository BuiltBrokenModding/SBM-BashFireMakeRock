package com.builtbroken.bashrockmakefire;

import com.builtbroken.bashrockmakefire.logic.EventHandler;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerAboutToStartEvent;
import net.minecraftforge.fml.common.event.FMLServerStoppedEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.text.MessageFormat;

@Mod(modid = BashFireMakeRock.MODID, name = BashFireMakeRock.MOD_NAME, version = BashFireMakeRock.VERSION, acceptableRemoteVersions = "*")
public class BashFireMakeRock
{

    public static final String MOD_NAME = "BashFireMakeRock";
    public static final String MODID = "bashfiremakerock";

    //Version injection data
    public static final String MAJOR_VERSION = "@MAJOR@";
    public static final String MINOR_VERSION = "@MINOR@";
    public static final String REVISION_VERSION = "@REVIS@";
    public static final String BUILD_VERSION = "@BUILD@";
    public static final String MC_VERSION = "@MC@";
    public static final String VERSION = MC_VERSION + "-" + MAJOR_VERSION + "." + MINOR_VERSION + "." + REVISION_VERSION + "." + BUILD_VERSION;


    public static final Logger LOGGER = LogManager.getLogger(MOD_NAME);

    @Mod.EventHandler
    public static void serverStart(FMLServerAboutToStartEvent event)
    {
        EventHandler.clickDataHashMap.clear();
        ConfigMain.initBlockData();
    }

    @Mod.EventHandler
    public static void serverStop(FMLServerStoppedEvent event)
    {
        EventHandler.clickDataHashMap.clear();
    }
}
