package me.ichun.mods.mobdismemberment.client.core;

import me.ichun.mods.ichunutil.client.render.RendererHelper;
import me.ichun.mods.ichunutil.common.iChunUtil;
import me.ichun.mods.mobamputation.common.MobAmputation;
import me.ichun.mods.mobdismemberment.client.entity.EntityGib;
import me.ichun.mods.mobdismemberment.client.particle.ParticleBlood;
import me.ichun.mods.mobdismemberment.common.MobDismemberment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityMinecartTNT;
import net.minecraft.entity.item.EntityTNTPrimed;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

public class EventHandlerClient
{
    public HashMap<EntityLivingBase, Integer> dismemberTimeout = new HashMap<>();
    public HashMap<Entity, Integer> exploTime = new HashMap<>();
    public ArrayList<Entity> explosionSources = new ArrayList<>();

    @SubscribeEvent
    public void onLivingDeath(LivingDeathEvent event)
    {
        if(event.getEntity().world.isRemote && (event.getEntityLiving() instanceof EntityZombie || event.getEntityLiving() instanceof EntitySkeleton || event.getEntityLiving() instanceof EntityCreeper) && !event.getEntityLiving().isChild())
        {
            dismemberTimeout.put(event.getEntityLiving(), 2);
        }
    }

    @SubscribeEvent
    public void onClientConnection(FMLNetworkEvent.ClientConnectedToServerEvent event)
    {
        exploTime.clear();
        dismemberTimeout.clear();
        explosionSources.clear();
    }

    @SubscribeEvent
    public void worldTick(TickEvent.ClientTickEvent event)
    {
        if(event.phase == TickEvent.Phase.END && Minecraft.getMinecraft().world != null)
        {
            Minecraft mc = Minecraft.getMinecraft();
            WorldClient world = mc.world;

            if(!mc.isGamePaused())
            {
                for(int i = 0; i < world.loadedEntityList.size(); i++)
                {
                    Entity ent = world.loadedEntityList.get(i);
                    if(ent instanceof EntityCreeper || ent instanceof EntityTNTPrimed || ent instanceof EntityMinecartTNT)
                    {
                        if(!explosionSources.contains(ent))
                        {
                            explosionSources.add(ent);
                        }
                    }
                    if((ent instanceof EntityZombie || ent instanceof EntitySkeleton || ent instanceof EntityCreeper) && !ent.isEntityAlive() && !dismemberTimeout.containsKey(ent))
                    {
                        dismemberTimeout.put((EntityLivingBase)ent, 2);
                    }
                }
                for(int i = explosionSources.size() - 1; i >= 0; i--)
                {
                    Entity ent = explosionSources.get(i);
                    if(ent.isDead)
                    {
                        if(ent instanceof EntityCreeper)
                        {
                            int igniteTime = ((EntityCreeper)ent).timeSinceIgnited;
                            int maxFuseTime = ((EntityCreeper)ent).fuseTime;
                            if(igniteTime >= maxFuseTime)
                            {
                                if(!exploTime.containsKey(ent))
                                {
                                    int time = iChunUtil.eventHandlerClient.ticks % 24000;
                                    if(time > 23959L)
                                    {
                                        time -= 23999L;
                                    }
                                    exploTime.put(ent, time);
                                }

                                dismemberTimeout.put((EntityLivingBase)ent, 2);
                            }
                        }
                        else if(ent instanceof EntityTNTPrimed || ent instanceof EntityMinecartTNT)
                        {
                            if(!exploTime.containsKey(ent))
                            {
                                int time = iChunUtil.eventHandlerClient.ticks % 24000;
                                if(time > 23959L)
                                {
                                    time -= 23999L;
                                }
                                exploTime.put(ent, time);
                            }
                        }

                        explosionSources.remove(i);
                    }
                }

                Iterator<Entry<EntityLivingBase, Integer>> ite = dismemberTimeout.entrySet().iterator();
                if(ite.hasNext())
                {
                    Entry<EntityLivingBase, Integer> e = ite.next();

                    e.setValue(e.getValue() - 1);

                    e.getKey().hurtTime = 0;
                    e.getKey().deathTime = 0;

                    Entity explo = null;
                    double dist = 1000D;
                    for(Entry<Entity, Integer> e1 : exploTime.entrySet())
                    {
                        double mobDist = e1.getKey().getDistance(e.getKey());
                        if(mobDist < 10D && mobDist < dist)
                        {
                            dist = mobDist;
                            explo = e1.getKey();
                            e.setValue(0);
                        }
                    }

                    if(e.getValue() <= 0)
                    {
                        if(dismember(e.getKey().world, e.getKey(), explo))
                        {
                            e.getKey().setDead();
                        }
                        ite.remove();
                    }
                }

                Iterator<Entry<Entity, Integer>> ite1 = exploTime.entrySet().iterator();
                int worldTime = iChunUtil.eventHandlerClient.ticks % 24000;
                while(ite1.hasNext())
                {
                    Entry<Entity, Integer> e = ite1.next();
                    if(e.getValue() + 40L < worldTime)
                    {
                        ite1.remove();
                    }
                }
            }
        }
    }

    public boolean dismember(World world, EntityLivingBase living, Entity explo)
    {
        if(living.isChild())
        {
            return false;
        }
        if(living instanceof EntityCreeper)
        {
            world.spawnEntity(new EntityGib(world, living, 0, explo));
            world.spawnEntity(new EntityGib(world, living, 3, explo));
            world.spawnEntity(new EntityGib(world, living, 6, explo));
            world.spawnEntity(new EntityGib(world, living, 7, explo));
            world.spawnEntity(new EntityGib(world, living, 8, explo));
            world.spawnEntity(new EntityGib(world, living, 9, explo));
        }
        else
        {
            for(int i = 0; i < 6; i++)
            {
                if(MobDismemberment.hasMobAmputation() && i <= 2)
                {
                    me.ichun.mods.mobamputation.client.entity.EntityGib[] gibs = MobAmputation.eventHandlerClient.amputationMap.get(living);
                    if(gibs != null && i < gibs.length)
                    {
                        if(!gibs[i].attached)
                        {
                            continue;
                        }
                    }
                }
                world.spawnEntity(new EntityGib(world, living, i, explo));
            }

            if(living instanceof EntityZombie && MobDismemberment.config.blood == 1)
            {
                for(int k = 0; k < (explo != null ? MobDismemberment.config.bloodCount * 10 : MobDismemberment.config.bloodCount); k++)
                {
                    float var4 = 0.3F;
                    double mX = (double)(-MathHelper.sin(living.rotationYaw / 180.0F * (float)Math.PI) * MathHelper.cos(living.rotationPitch / 180.0F * (float)Math.PI) * var4);
                    double mZ = (double)(MathHelper.cos(living.rotationYaw / 180.0F * (float)Math.PI) * MathHelper.cos(living.rotationPitch / 180.0F * (float)Math.PI) * var4);
                    double mY = (double)(-MathHelper.sin(living.rotationPitch / 180.0F * (float)Math.PI) * var4 + 0.1F);
                    var4 = 0.02F;
                    float var5 = living.getRNG().nextFloat() * (float)Math.PI * 2.0F;
                    var4 *= living.getRNG().nextFloat();

                    if(explo != null)
                    {
                        var4 *= 100D;
                    }

                    mX += Math.cos((double)var5) * (double)var4;
                    mY += (double)((living.getRNG().nextFloat() - living.getRNG().nextFloat()) * 0.1F);
                    mZ += Math.sin((double)var5) * (double)var4;

                    RendererHelper.spawnParticle(new ParticleBlood(living.world, living.posX, living.posY + 0.5D + (living.getRNG().nextDouble() * 0.7D), living.posZ, living.motionX + mX, living.motionY + mY, living.motionZ + mZ, false));
                }
            }
        }
        return true;
    }
}
