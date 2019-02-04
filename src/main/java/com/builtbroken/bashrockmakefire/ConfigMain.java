package com.builtbroken.bashrockmakefire;

import com.builtbroken.bashrockmakefire.logic.EventHandler;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.text.MessageFormat;

@Config(modid = BashFireMakeRock.MODID, name = BashFireMakeRock.MOD_NAME)
public class ConfigMain
{

    public static Delay DELAY = new Delay();

    @Config.LangKey("bashfiremakerock.config.chance_per_click")
    @Config.Comment("Chance to start a fire. 1/X [Default : 1/2]")
    public static int chancePerClick = 2;

    @Config.LangKey("bashfiremakerock.config.blocks")
    @Config.Name("block_list")
    @Config.Comment("Blocks that can generate a fire tile when clicked. " +
            "Examples : minecraft:dirt, minecraft:stone,... 1 item per line")
    public static String[] blocks = {};

    @Config.LangKey("bashfiremakerock.config.blocks.allow")
    @Config.Name("allow_list")
    @Config.Comment("Sets the block list to be used as an allow only list. Set to false to use as a ban list.")
    public static boolean allowList = true;

    @Config.LangKey("bfmr.config.start")
    @Config.Name("number_of_clicks")
    @Config.Comment("Number of clicks before rolling chance for fire to generate.")
    public static int numberOfClickBeforeChance = 3;

    public static class Delay
    {

        @Config.LangKey("bashfiremakerock.config.delay.min")
        @Config.Name("min_delay")
        @Config.Comment("Minimal time between clicks for it to be recorded.")
        public double minimalDelay = 0.5;

        @Config.LangKey("bashfiremakerock.config.delay.max")
        @Config.Name("max_delay")
        @Config.Comment("Max time to wait for the next click before clearing recorded clicks.")
        public double maxDelay = 3;
    }

    @SubscribeEvent
    public static void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent event) {
        if(event.getModID() == BashFireMakeRock.MODID) {
            initBlockData();
        }
    }

    public static void initBlockData() {
        //Clear previous
        EventHandler.authorizedBlocks.clear();

        //Load new
        for (int i = 0; i < ConfigMain.blocks.length; i++)
        {
            ResourceLocation resourcelocation = new ResourceLocation(ConfigMain.blocks[i]);
            Block block = Block.REGISTRY.getObject(resourcelocation);
            if (!block.equals(Blocks.AIR))
            {
                EventHandler.authorizedBlocks.add(block);
            }
            else
            {
                BashFireMakeRock.LOGGER.info(MessageFormat.format("Config: Failed to find block '{0}' in the registry", ConfigMain.blocks[i]));
            }
        }
    }
}

