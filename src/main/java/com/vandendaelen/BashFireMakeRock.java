package com.vandendaelen;

import com.vandendaelen.config.BFMRConfig;
import com.vandendaelen.handler.EventHandler;
import com.vandendaelen.util.Reference;
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

@Mod(modid = Reference.MODID, name = Reference.MOD_NAME, version = Reference.VERSION.VERSION, acceptableRemoteVersions = "*")
public class BashFireMakeRock {
    public static final Logger LOGGER = LogManager.getLogger(Reference.MOD_NAME);

    @Mod.Instance(Reference.MODID)
    public static BashFireMakeRock instance;

    @Mod.EventHandler
    public static void postInit(FMLPostInitializationEvent event){
        EventHandler.authorizedBlocks.clear();
        for (int i = 0; i < BFMRConfig.blocks.length; i++){
            ResourceLocation resourcelocation = new ResourceLocation(BFMRConfig.blocks[i]);
            Block b = Block.REGISTRY.getObject(resourcelocation);
            if (!b.equals(Blocks.AIR))
                EventHandler.authorizedBlocks.add(b);
            else
                LOGGER.info(MessageFormat.format("{0} not found",BFMRConfig.blocks[i]));
        }
    }

    @Mod.EventHandler
    public static void serverStart(FMLServerAboutToStartEvent event){
        EventHandler.clickDataHashMap.clear();
    }

    @Mod.EventHandler
    public static void serverStop(FMLServerStoppedEvent event){
        EventHandler.clickDataHashMap.clear();
    }
}
