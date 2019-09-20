package com.builtbroken.bashrockmakefire.logic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.builtbroken.bashrockmakefire.BashFireMakeRock;
import com.builtbroken.bashrockmakefire.ConfigMain;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = BashFireMakeRock.MODID)
public class EventHandler
{
    public static HashMap<BlockPos, ClickData> clickDataHashMap = new HashMap<>();
    public static List<Block> supportedBlocks = new ArrayList<>();
    public static List<BlockState> supportedBlockStates = new ArrayList<>();

    @SubscribeEvent
    public static void onBlockPunch(PlayerInteractEvent.LeftClickBlock event)
    {
        if (!event.getWorld().isRemote)
        {
            try
            {
                //Get the position and block
                final BlockPos clickPos = event.getPos();
                final BlockState blockTarget = event.getWorld().getBlockState(clickPos);

                if (isBlockSupported(blockTarget))
                {
                    if (clickDataHashMap.containsKey(clickPos))
                    {
                        if (clickDataHashMap.get(clickPos).click())
                        {
                            //Check if we can place fire
                            final BlockPos firePos = clickPos.up();
                            final BlockState blockAbove = event.getWorld().getBlockState(firePos);
                            if (blockAbove.getBlockState().isValidPosition(event.getWorld(), firePos)) //TODO: isReplaceable
                            {
                                event.setCanceled(true);
                                event.getWorld().setBlockState(firePos, Blocks.FIRE.getDefaultState());
                                if (event.getPlayer().getGameProfile() != null)
                                {
                                    BashFireMakeRock.LOGGER.info(
                                            event.getPlayer().getGameProfile().getName() + " [" + event.getPlayer().getGameProfile().getId() + "]"
                                                    + " created fire at " + firePos
                                                    + " in world " + event.getWorld().getWorldInfo().getWorldName()
                                                    + " dim: " + event.getWorld().getDimension());
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

                //Clear dead entries
                clickDataHashMap.entrySet().removeIf(e -> e.getValue().isDead());
            }
            catch (Exception e)
            {
                String data =
                        event.getPlayer().getGameProfile().getName() + " [" + event.getPlayer().getGameProfile().getId() + "]"
                                + " created fire at " + event.getPos()
                                + " in world " + event.getWorld().getWorldInfo().getWorldName()
                                + " dim: " + event.getWorld().getDimension();
                BashFireMakeRock.LOGGER.error("Unexpected error while handling click event for '" + data + "'", e);
                event.getPlayer().sendMessage(new TranslationTextComponent(BashFireMakeRock.MODID + ":error.event.click"));
            }
        }
    }

    /**
     * Checks if the block state is supported
     *
     * @param iBlockState
     * @return
     */
    public static boolean isBlockSupported(BlockState iBlockState)
    {
        if (!supportedBlockStates.isEmpty())
        {
            boolean contained = supportedBlockStates.contains(iBlockState); //TODO check for super (E.g. ignore rotation)
            if (ConfigMain.allowList() && contained || !ConfigMain.allowList() && !contained)
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
                || ConfigMain.allowList() && supportedBlocks.contains(block)
                || !ConfigMain.allowList() && !supportedBlocks.contains(block);
    }
}
