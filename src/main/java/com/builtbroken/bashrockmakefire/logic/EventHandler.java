package com.builtbroken.bashrockmakefire.logic;

import com.builtbroken.bashrockmakefire.BashFireMakeRock;
import com.builtbroken.bashrockmakefire.ConfigMain;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@Mod.EventBusSubscriber(modid = BashFireMakeRock.MODID)
public class EventHandler
{

    public static HashMap<BlockPos, ClickData> clickDataHashMap = new HashMap<>();
    public static List<Block> supportedBlocks = new ArrayList<>();
    public static List<IBlockState> supportedBlockStates = new ArrayList<>();

    @SubscribeEvent
    public static void onBlockPunch(PlayerInteractEvent.LeftClickBlock event)
    {
        try
        {
            //Get the position and block
            final BlockPos clickPos = event.getPos();
            final IBlockState blockTarget = event.getWorld().getBlockState(clickPos);
            if (isBlockSupported(blockTarget))
            {
                if (clickDataHashMap.containsKey(clickPos))
                {
                    if (clickDataHashMap.get(clickPos).click())
                    {
                        //Check if we can place fire
                        final BlockPos firePos = clickPos.up();
                        final IBlockState blockAbove = event.getWorld().getBlockState(firePos);
                        if (blockAbove.getBlock().isReplaceable(event.getWorld(), firePos))
                        {
                            event.setCanceled(true);
                            event.getWorld().setBlockState(firePos, Blocks.FIRE.getDefaultState());
                            if (event.getEntityPlayer().getGameProfile() != null)
                            {
                                BashFireMakeRock.LOGGER.info(
                                        event.getEntityPlayer().getGameProfile().getName() + " [" + event.getEntityPlayer().getGameProfile().getId() + "]"
                                                + " created fire at " + firePos
                                                + " in world " + event.getWorld().getWorldInfo().getWorldName()
                                                + " dim: " + event.getWorld().provider.getDimension());
                            }
                        }

                        //Remove as we are done
                        clickDataHashMap.remove(clickPos);
                    }
                }
                else
                {
                    clickDataHashMap.put(clickPos, new ClickData(clickPos));
                }
            }

            //Clear dead entries TODO check for performance hit on larger servers
            clickDataHashMap.keySet().stream()
                    .filter(e -> clickDataHashMap.get(e).isDead())
                    .collect(Collectors.toList())
                    .forEach(e -> clickDataHashMap.remove(e));
        }
        catch (Exception e)
        {
            String data =
                    event.getEntityPlayer().getGameProfile().getName() + " [" + event.getEntityPlayer().getGameProfile().getId() + "]"
                            + " created fire at " + event.getPos()
                            + " in world " + event.getWorld().getWorldInfo().getWorldName()
                            + " dim: " + event.getWorld().provider.getDimension();
            BashFireMakeRock.LOGGER.error("Unexpected error while handling click event for '" + data + "'", e);
            event.getEntityPlayer().sendMessage(new TextComponentTranslation(BashFireMakeRock.MODID + ":error.event.click"));
        }
    }

    /**
     * Checks if the block state is supported
     *
     * @param iBlockState
     * @return
     */
    public static boolean isBlockSupported(IBlockState iBlockState)
    {
        if (!supportedBlockStates.isEmpty())
        {
            boolean contained = supportedBlockStates.contains(iBlockState); //TODO check for super (E.g. ignore rotation)
            if (ConfigMain.allowList && contained || !ConfigMain.allowList && !contained)
            {
                return true;
            }
        }
        return isBlockSupported(iBlockState.getBlock());
    }

    /**
     * Checks if the block is supported
     *
     * @param block
     * @return
     */
    public static boolean isBlockSupported(Block block)
    {
        return supportedBlocks.isEmpty()
                || ConfigMain.allowList && supportedBlocks.contains(block)
                || !ConfigMain.allowList && !supportedBlocks.contains(block);
    }
}
