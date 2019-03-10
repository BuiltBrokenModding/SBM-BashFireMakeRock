package com.builtbroken.bashrockmakefire;

import com.builtbroken.bashrockmakefire.logic.EventHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLServerAboutToStartEvent;
import net.minecraftforge.fml.common.event.FMLServerStoppedEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(modid = BashFireMakeRock.MODID, name = "[SBM] BashFireMakeRock", version = BashFireMakeRock.VERSION, acceptableRemoteVersions = "*")
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
