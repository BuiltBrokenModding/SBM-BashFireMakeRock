package com.builtbroken.bashrockmakefire;

import java.text.MessageFormat;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.tuple.Pair;

import com.builtbroken.bashrockmakefire.logic.EventHandler;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.state.IProperty;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.registries.ForgeRegistries;

public class ConfigMain
{
    public static final ForgeConfigSpec CONFIG_SPEC;
    public static final ConfigMain CONFIG;

    public final ForgeConfigSpec.ConfigValue<Integer> chancePerClick;
    public final ForgeConfigSpec.ConfigValue<List<? extends String>> blocks;
    public final ForgeConfigSpec.BooleanValue allowList;
    public final ForgeConfigSpec.ConfigValue<Integer> numberOfClicksBeforeChance;
    public final ForgeConfigSpec.ConfigValue<Double> minimalDelay;
    public final ForgeConfigSpec.ConfigValue<Double> maxDelay;

    static {
        Pair<ConfigMain,ForgeConfigSpec> specPair = new ForgeConfigSpec.Builder().configure(ConfigMain::new);
        CONFIG_SPEC = specPair.getRight();
        CONFIG = specPair.getLeft();

    }

    ConfigMain(ForgeConfigSpec.Builder builder)
    {
        chancePerClick = builder
                .comment("Chance to start a fire. 1/X [Default : 1/2]")
                .define("chance_per_click", 2);

        blocks = builder
                .comment("Blocks that can generate a fire tile when clicked. " +
                        "Examples : minecraft:dirt, minecraft:stone, ... 1 item per line")
                .define("block_list", Arrays.asList());

        allowList = builder
                .comment("Sets the block list to be used as an allow only list. Set to false to use as a ban list.")
                .define("allow_list", true);

        numberOfClicksBeforeChance = builder
                .comment("Number of clicks before rolling chance for fire to generate.")
                .define("number_of_clicks", 3);

        minimalDelay = builder
                .comment("Minimal time between clicks for it to be recorded.")
                .define("min_delay", 0.5);

        maxDelay = builder
                .comment("Max time to wait for the next click before clearing recorded clicks.")
                .define("max_delay", 3.0);
    }


    public static int chancePerClick()
    {
        return CONFIG.chancePerClick.get();
    }

    public static List<? extends String> blocks()
    {
        return CONFIG.blocks.get();
    }

    public static boolean allowList()
    {
        return CONFIG.allowList.get();
    }

    public static int numberOfClicksBeforeChance()
    {
        return CONFIG.numberOfClicksBeforeChance.get();
    }

    public static double minimalDelay()
    {
        return CONFIG.minimalDelay.get();
    }

    public static double maxDelay()
    {
        return CONFIG.maxDelay.get();
    }

    /**
     * Called to load all block data for  {@link EventHandler#supportedBlocks} & {@link EventHandler#supportedBlockStates}
     */
    public void initBlockData()
    {
        //Clear previous
        EventHandler.supportedBlocks.clear();
        EventHandler.supportedBlockStates.clear();

        //Load new
        for (final String entry : blocks.get())
        {
            //JSON NBT
            if (entry.contains("{"))
            {
                try
                {
                    CompoundNBT compound = JsonToNBT.getTagFromJson(entry);
                    BlockState state = NBTUtil.readBlockState(compound);
                    if (!state.isAir())
                    {
                        EventHandler.supportedBlockStates.add(state);
                    }
                    else
                    {
                        BashFireMakeRock.LOGGER.error("Config: Failed to parse block state '" + entry + "'");
                    }
                }
                catch (Exception e)
                {
                    BashFireMakeRock.LOGGER.error("Config: Failed to parse JSON block state entry '" + e + "'");
                }
            }
            //Command format
            else if (entry.contains("["))
            {
                //https://minecraft.gamepedia.com/Commands/setblock
                //minecraft:furnace[lit=true,facing=south]

                //Split into id and data
                final String id = entry.substring(0, entry.indexOf("["));
                final String[] props = entry.substring(entry.indexOf("[") + 1, entry.indexOf("]")).split(",");

                //Get block
                final Block block = getBlock(id);
                if (!block.equals(Blocks.AIR))
                {
                    //Build state
                    BlockState blockState = block.getDefaultState();
                    for (String prop : props)
                    {
                        //Try to get state property
                        String name = prop.substring(0, prop.indexOf("="));
                        String value = prop.substring(prop.indexOf("=") + 1);
                        blockState = withProperty(blockState, name, value);

                        //Failed to get property
                        if (blockState == null)
                        {
                            BashFireMakeRock.LOGGER.error(MessageFormat.format("Config: Failed to find block state key '{0}' while parsing '{1}", name, entry));
                            break;
                        }
                    }

                    if (blockState != null)
                    {
                        EventHandler.supportedBlockStates.add(blockState);
                    }
                }
                else
                {
                    BashFireMakeRock.LOGGER.info(MessageFormat.format("Config: Failed to find block '{0}' in the registry for '{1}", id, entry));
                }
            }
            //Block ID
            else
            {
                final Block block = getBlock(entry);
                if (!block.equals(Blocks.AIR))
                {
                    EventHandler.supportedBlocks.add(block);
                }
                else
                {
                    BashFireMakeRock.LOGGER.error(MessageFormat.format("Config: Failed to find block '{0}' in the registry", entry));
                }
            }
        }
    }

    private static BlockState withProperty(BlockState blockState, String name, String value)
    {
        for (IProperty property : blockState.getProperties())
        {
            if (property.getName().equalsIgnoreCase(name))
            {
                return withProperty(blockState, property, value);
            }
        }
        return null;
    }

    private static <T extends Comparable<T>> BlockState withProperty(BlockState blockState, IProperty<T> property, String value)
    {
        T valueObj = property.parseValue(value).get();
        return blockState.with(property, valueObj);
    }

    private static Block getBlock(String entry)
    {
        ResourceLocation resourcelocation = new ResourceLocation(entry);
        return ForgeRegistries.BLOCKS.getValue(resourcelocation);
    }
}

