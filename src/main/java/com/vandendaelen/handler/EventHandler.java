package com.vandendaelen.handler;

import com.vandendaelen.object.ClickData;
import com.vandendaelen.util.Reference;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Mod.EventBusSubscriber(modid = Reference.MODID)
public class EventHandler {

    public static HashMap<BlockPos, ClickData> clickDataHashMap = new HashMap<>();
    public static List<Block> authorizedBlocks = new ArrayList<>();

    @SubscribeEvent
    public static void onBlockPunch(PlayerInteractEvent.LeftClickBlock event){
        if (!event.getWorld().isRemote){
            RayTraceResult result = new RayTraceResult(event.getHitVec(),event.getFace(), event.getPos());
            if (result.typeOfHit == RayTraceResult.Type.BLOCK){
                BlockPos clickPos = result.getBlockPos();
                Block blockTarget = event.getWorld().getBlockState(clickPos).getBlock();
                if (authorizedBlocks.isEmpty() || authorizedBlocks.contains(blockTarget)) {
                    if (clickDataHashMap.containsKey(clickPos)) {
                        if (clickDataHashMap.get(clickPos).click()) {
                            BlockPos firePos = clickPos.up();
                            if (event.getWorld().getBlockState(firePos).getBlock().equals(Blocks.AIR)) {
                                event.setCanceled(true);
                                event.getWorld().setBlockState(firePos, Blocks.FIRE.getDefaultState());
                            }
                        }
                    } else {
                        clickDataHashMap.put(clickPos, new ClickData(clickPos));
                    }
                }
            }

            //Cleaning
            List<BlockPos> toBeRemoved = new ArrayList<>();
            for (Map.Entry<BlockPos, ClickData> entry: clickDataHashMap.entrySet()) {
                if (entry.getValue().isDead()){
                    toBeRemoved.add(entry.getKey());
                }
            }
            toBeRemoved.forEach(blockPos -> clickDataHashMap.remove(blockPos));
        }
    }

//    @SubscribeEvent
//    public static void onBlockPunch(PlayerInteractEvent.LeftClickBlock event){
//        RayTraceResult result = new RayTraceResult(event.getHitVec(),event.getFace(), event.getPos());
//        if (result.typeOfHit == RayTraceResult.Type.BLOCK){
//            BlockPos firePos = result.getBlockPos().up();
//            if (event.getWorld().getBlockState(firePos).getBlock().equals(Blocks.AIR)) {
//                event.setCanceled(true);
//                event.getWorld().setBlockState(firePos, Blocks.FIRE.getDefaultState());
//            }
//        }
//
//    }
}
