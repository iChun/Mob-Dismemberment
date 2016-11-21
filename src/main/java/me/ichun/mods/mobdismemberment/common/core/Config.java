package me.ichun.mods.mobdismemberment.common.core;

import me.ichun.mods.ichunutil.common.core.config.ConfigBase;
import me.ichun.mods.ichunutil.common.core.config.annotations.ConfigProp;
import me.ichun.mods.ichunutil.common.core.config.annotations.IntBool;
import me.ichun.mods.ichunutil.common.core.config.annotations.IntMinMax;
import me.ichun.mods.mobdismemberment.common.MobDismemberment;

import java.io.File;

public class Config extends ConfigBase
{
    @ConfigProp(category = "clientOnly")
    @IntMinMax(min = 0)
    public int gibTime = 1000;

    @ConfigProp(category = "clientOnly")
    @IntMinMax(min = 0)
    public int gibGroundTime = 100;

    @ConfigProp(category = "clientOnly")
    @IntBool
    public int blood = 1;

    @ConfigProp(category = "clientOnly")
    @IntMinMax(min = 1, max = 1000)
    public int bloodCount = 100;

    @ConfigProp(category = "clientOnly")
    @IntBool
    public int greenBlood = 0;

    @ConfigProp(category = "clientOnly")
    @IntBool
    public int gibPushing = 1;

    public Config(File file)
    {
        super(file);
    }

    @Override
    public String getModId()
    {
        return MobDismemberment.MOD_ID;
    }

    @Override
    public String getModName()
    {
        return "Mob Dismemberment";
    }
}
