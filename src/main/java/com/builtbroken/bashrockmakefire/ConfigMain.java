package com.builtbroken.bashrockmakefire;

import com.builtbroken.bashrockmakefire.logic.EventHandler;
import net.minecraft.block.Block;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.text.MessageFormat;

@Config.LangKey("config.bashfiremakerock:title")
@Config(modid = BashFireMakeRock.MODID, name = "sbm/BashFireMakeRock")
@Mod.EventBusSubscriber(modid = BashFireMakeRock.MODID)
public class ConfigMain
{
    @Config.LangKey("config.bashfiremakerock:chance")
    @Config.Comment("Chance to start a fire. 1/X [Default : 1/2]")
    public static int chancePerClick = 2;

    @Config.LangKey("config.bashfiremakerock:blocks")
    @Config.Name("block_list")
    @Config.Comment("Blocks that can generate a fire tile when clicked. " +
            "Examples : minecraft:dirt, minecraft:stone,... 1 item per line")
    public static String[] blocks = {};

    @Config.LangKey("config.bashfiremakerock:blocks.allow")
    @Config.Name("allow_list")
    @Config.Comment("Sets the block list to be used as an allow only list. Set to false to use as a ban list.")
    public static boolean allowList = true;

    @Config.LangKey("config.bashfiremakerock:start")
    @Config.Name("number_of_clicks")
    @Config.Comment("Number of clicks before rolling chance for fire to generate.")
    public static int numberOfClickBeforeChance = 3;

    @Config.LangKey("config.bashfiremakerock:delay.min")
    @Config.Name("min_delay")
    @Config.Comment("Minimal time between clicks for it to be recorded.")
    public static double minimalDelay = 0.5;

    @Config.LangKey("config.bashfiremakerock:delay.max")
    @Config.Name("max_delay")
    @Config.Comment("Max time to wait for the next click before clearing recorded clicks.")
    public static double maxDelay = 3;

    @SubscribeEvent
    public static void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent event)
    {
        if (event.getModID().equals(BashFireMakeRock.MODID))
        {
            ConfigManager.sync(BashFireMakeRock.MODID, Config.Type.INSTANCE);
            initBlockData();
        }
    }

    /**
     * Called to load all block data for  {@link EventHandler#supportedBlocks} & {@link EventHandler#supportedBlockStates}
     */
    public static void initBlockData()
    {
        //Clear previous
        EventHandler.supportedBlocks.clear();
        EventHandler.supportedBlockStates.clear();

        //Load new
        for (final String entry : blocks)
        {
            //JSON NBT
            if (entry.contains("{"))
            {
                try
                {
                    NBTTagCompound compound = JsonToNBT.getTagFromJson(entry);
                    IBlockState state = NBTUtil.readBlockState(compound);
                    if (!state.getBlock().equals(Blocks.AIR))
                    {
                        EventHandler.supportedBlockStates.add(state);
                    }
                    else
                    {
                        BashFireMakeRock.LOGGER.error("Config: Failed to parse block state '" + entry + "'");
                    }
                } catch (Exception e)
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
                    IBlockState blockState = block.getDefaultState();
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

    private static IBlockState withProperty(IBlockState blockState, String name, String value)
    {
        for (IProperty property : blockState.getPropertyKeys())
        {
            if (property.getName().equalsIgnoreCase(name))
            {
                return withProperty(blockState, property, value);
            }
        }
        return null;
    }

    private static <T extends Comparable<T>> IBlockState withProperty(IBlockState blockState, IProperty<T> property, String value)
    {
        T valueObj = property.parseValue(value).get();
        return blockState.withProperty(property, valueObj);
    }

    private static Block getBlock(String entry)
    {
        ResourceLocation resourcelocation = new ResourceLocation(entry);
        return Block.REGISTRY.getObject(resourcelocation);
    }
}

