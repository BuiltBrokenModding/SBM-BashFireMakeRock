package com.builtbroken.bashrockmakefire.logic;

import com.builtbroken.bashrockmakefire.ConfigMain;
import net.minecraft.util.math.BlockPos;

import java.util.Random;

public class ClickData
{

    private long lastTimeClicked;
    private int timesClicked;
    private BlockPos pos;
    public static Random random = new Random();

    public ClickData(BlockPos pos)
    {
        this.pos = pos;
        this.lastTimeClicked = System.currentTimeMillis();
        timesClicked = 1;
    }

    public boolean click()
    {
        long diff = System.currentTimeMillis() - lastTimeClicked;
        if (diff >= ConfigMain.minimalDelay * 1000)
        {
            timesClicked++;
            lastTimeClicked = System.currentTimeMillis();
            if (timesClicked > ConfigMain.numberOfClickBeforeChance)
            {
                return ConfigMain.chancePerClick <= 0 || random.nextInt(ConfigMain.chancePerClick) == 0;
            }
        }
        return false;
    }

    public boolean isDead()
    {
        return System.currentTimeMillis() - lastTimeClicked > ConfigMain.maxDelay * 1000;
    }

    public BlockPos getPos()
    {
        return pos;
    }
}
