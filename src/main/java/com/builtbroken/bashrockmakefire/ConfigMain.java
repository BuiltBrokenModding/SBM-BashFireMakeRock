package com.builtbroken.bashrockmakefire;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import org.apache.commons.lang3.tuple.Pair;

@Mod.EventBusSubscriber(modid = BashFireMakeRock.MODID)
public class ConfigMain
{

    public static final ForgeConfigSpec CONFIG_SPEC;
    public static final ConfigMain CONFIG;

    public final ForgeConfigSpec.ConfigValue<Integer> chancePerClick;
    public final ForgeConfigSpec.ConfigValue<String> blocks;
    public final ForgeConfigSpec.BooleanValue allowList;
    public final ForgeConfigSpec.ConfigValue<Integer> numberOfClickBeforeChance;
    public final ForgeConfigSpec.ConfigValue<Double> minimalDelay;
    public final ForgeConfigSpec.ConfigValue<Integer> maxDelay;

    static {
        Pair<ConfigMain,ForgeConfigSpec> specPair = new ForgeConfigSpec.Builder().configure(ConfigMain::new);
        CONFIG_SPEC = specPair.getRight();
        CONFIG = specPair.getLeft();

    }
    //Couldn't figure out how to add the langkeys
    ConfigMain(ForgeConfigSpec.Builder builder)
    {
        chancePerClick = builder
                .comment("Chance to start a fire. 1/X [Default : 1/2]")
                .define("chancePerClick", 2);

        blocks = builder
                .comment("Blocks that can generate a fire tile when clicked. " +
                        "Examples : minecraft:dirt, minecraft:stone,... 1 item per line")
                .define("block_list", " ");

        allowList = builder
                .comment("Sets the block list to be used as an allow only list. Set to false to use as a ban list.")
                .define("allow_list", true);

        numberOfClickBeforeChance = builder
                .comment("Number of clicks before rolling chance for fire to generate.")
                .define("number_of_clicks",3);

        minimalDelay = builder
                .comment("Minimal time between clicks for it to be recorded.")
                .define("min_delay",0.5);

        maxDelay = builder
                .comment("Max time to wait for the next click before clearing recorded clicks.")
                .define("max_delay",3);
    }


    @SubscribeEvent
    public static void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent event)
    {
        if (event.getModID().equals(BashFireMakeRock.MODID))
        {
            //Can't find what goes here I: *Derpy dance*
            System.out.println("Herpe Derp");
        }
    }

    public static int chancePerClick()
    {
        return CONFIG.chancePerClick.get();
    }

    public static String blocks()
    {
        return CONFIG.blocks.get();
    }

    public static boolean allowList()
    {
        return CONFIG.allowList.get();
    }

    public static int numberOfClickBeforeChance()
    {
        return CONFIG.numberOfClickBeforeChance.get();
    }

    public static double minimalDelay()
    {
        return CONFIG.minimalDelay.get();
    }

    public static int maxDelay()
    {
        return CONFIG.maxDelay.get();
    }

}

