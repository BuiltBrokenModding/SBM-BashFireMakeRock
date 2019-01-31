package com.vandendaelen.object;

import com.vandendaelen.config.BFMRConfig;
import net.minecraft.util.math.BlockPos;

import java.util.Random;

public class ClickData {
    private long lastTimeClicked;
    private int timesClicked;
    private BlockPos pos;
    public static Random random = new Random();

    public ClickData(BlockPos pos) {
        this.pos = pos;
        this.lastTimeClicked = System.currentTimeMillis();
        timesClicked = 1;
    }

    public boolean click(){
        long diff = System.currentTimeMillis() - lastTimeClicked;
        if (diff >= BFMRConfig.DELAY.minimalDelay * 1000){
            timesClicked++;
            lastTimeClicked = System.currentTimeMillis();
            if (timesClicked > BFMRConfig.numberOfClickBeforeChance)
                return random.nextInt(BFMRConfig.chancePerClick) == 0;
        }
        return false;
    }

    public boolean isDead(){
        return System.currentTimeMillis() - lastTimeClicked > BFMRConfig.DELAY.maxDelay*1000;
    }

    public BlockPos getPos() {
        return pos;
    }
}
