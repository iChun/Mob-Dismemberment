package me.ichun.mods.mobdismemberment.common;

import me.ichun.mods.ichunutil.common.core.config.ConfigHandler;
import me.ichun.mods.ichunutil.common.iChunUtil;
import me.ichun.mods.ichunutil.common.module.update.UpdateChecker;
import me.ichun.mods.mobdismemberment.client.core.EventHandlerClient;
import me.ichun.mods.mobdismemberment.client.entity.EntityGib;
import me.ichun.mods.mobdismemberment.client.render.RenderGib;
import me.ichun.mods.mobdismemberment.common.core.Config;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

@Mod(modid = MobDismemberment.MOD_ID, name = MobDismemberment.MOD_NAME,
        version = MobDismemberment.VERSION,
        guiFactory = iChunUtil.GUI_CONFIG_FACTORY,
        dependencies = "required-after:ichunutil@[" + iChunUtil.VERSION_MAJOR +".0.2," + (iChunUtil.VERSION_MAJOR + 1) + ".0.0)",
        acceptedMinecraftVersions = iChunUtil.MC_VERSION_RANGE,
        clientSideOnly = true
)
public class MobDismemberment
{
    public static final String VERSION = iChunUtil.VERSION_MAJOR + ".0.0";
    public static final String MOD_NAME = "MobDismemberment";
    public static final String MOD_ID = "mobdismemberment";

    @Mod.Instance(MOD_ID)
    public static MobDismemberment instance;

    public static Config config;

    public static EventHandlerClient eventHandlerClient;

    private static boolean hasMobAmputation;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        config = ConfigHandler.registerConfig(new Config(event.getSuggestedConfigurationFile()));

        eventHandlerClient = new EventHandlerClient();
        MinecraftForge.EVENT_BUS.register(eventHandlerClient);

        RenderingRegistry.registerEntityRenderingHandler(EntityGib.class, new RenderGib.RenderFactory());

        UpdateChecker.registerMod(new UpdateChecker.ModVersionInfo(MOD_NAME, iChunUtil.VERSION_OF_MC, VERSION, true));
    }

    @Mod.EventHandler
    public void onPostInit(FMLPostInitializationEvent event)
    {
        hasMobAmputation = Loader.isModLoaded("mobamputation");
    }

    public static boolean hasMobAmputation()
    {
        return hasMobAmputation;
    }
}
