package me.ichun.mods.mobdismemberment.client.render;

import me.ichun.mods.ichunutil.common.core.util.EntityHelper;
import me.ichun.mods.ichunutil.common.core.util.ObfHelper;
import me.ichun.mods.mobdismemberment.client.entity.EntityGib;
import me.ichun.mods.mobdismemberment.common.MobDismemberment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.client.registry.IRenderFactory;
import org.lwjgl.opengl.GL11;

public class RenderGib extends Render<EntityGib>
{
    public ModelGib modelGib;

    public RenderGib(RenderManager manager)
    {
        super(manager);
        modelGib = new ModelGib();
    }

    @Override
    protected ResourceLocation getEntityTexture(EntityGib gib)
    {
        Render render = Minecraft.getMinecraft().getRenderManager().getEntityRenderObject(gib.parent);
        return ObfHelper.getEntityTexture(render, render.getClass(), gib.parent);
    }

    @Override
    public void doRender(EntityGib gib, double par2, double par4, double par6, float par8, float par9)
    {
        GlStateManager.disableCull();
        GlStateManager.pushMatrix();
        bindEntityTexture(gib);
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GlStateManager.color(1.0F, 1.0F, 1.0F, MathHelper.clamp(gib.groundTime >= MobDismemberment.config.gibGroundTime ? 1.0F - (gib.groundTime - MobDismemberment.config.gibGroundTime + par9) / 20F : 1.0F, 0F, 1F));
        GlStateManager.alphaFunc(GL11.GL_GREATER, 0.003921569F);

        GlStateManager.translate(par2, par4, par6);

        GlStateManager.translate(0.0F, gib.type == 0 ? 4F / 16F : gib.type <= 2 && gib.parent instanceof EntitySkeleton ? 1F / 16F : 2F / 16F, 0.0F);

        GlStateManager.rotate(EntityHelper.interpolateRotation(gib.prevRotationYaw, gib.rotationYaw, par9), 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(EntityHelper.interpolateRotation(gib.prevRotationPitch, gib.rotationPitch, par9), -1.0F, 0.0F, 0.0F);

        GlStateManager.translate(0.0F, 24F / 16F - gib.height * 0.5F, 0.0F);

        GlStateManager.scale(-1.0F, -1.0F, 1.0F);

        modelGib.render(gib, 0.0F, 0.0F, -0.1F, 0.0F, 0.0F, 0.0625F);

        GlStateManager.alphaFunc(GL11.GL_GREATER, 0.1F);
        GlStateManager.disableBlend();
        GlStateManager.popMatrix();
        GlStateManager.enableCull();
    }

    public static class RenderFactory implements IRenderFactory<EntityGib>
    {
        @Override
        public Render<? super EntityGib> createRenderFor(RenderManager manager)
        {
            return new RenderGib(manager);
        }
    }
}
