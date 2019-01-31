package com.vandendaelen.config;

import com.vandendaelen.util.Reference;
import net.minecraftforge.common.config.Config;

@Config(modid = Reference.MODID,name = Reference.MOD_NAME)
public class BFMRConfig {
    public static Delay DELAY = new Delay();

    @Config.LangKey("bfmr.config.chance_per_click")
    @Config.Comment("Chance to start a fire. 1/X [Default : 1/2]")
    public static int chancePerClick = 2;

    @Config.LangKey("bfmr.config.blocks")
    @Config.Comment("Examples : minecraft:dirt, minecraft:stone,... 1 item per line")
    public static String[] blocks = {};

    @Config.LangKey("bfmr.config.start")
    public static int numberOfClickBeforeChance = 1;

    public static class Delay{
        @Config.LangKey("bfmr.config.delay.min")
        @Config.Comment("seconds")
        public double minimalDelay = 0.5;

        @Config.LangKey("bfmr.config.delay.max")
        @Config.Comment("seconds")
        public double maxDelay = 3;
    }
}

