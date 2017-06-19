package me.ichun.mods.mobdismemberment.client.particle;

import me.ichun.mods.mobdismemberment.common.MobDismemberment;
import net.minecraft.client.particle.Particle;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.World;

import java.util.List;

public class ParticleBlood extends Particle
{
    public ParticleBlood(World world, double d, double d1, double d2, double d3, double d4, double d5, boolean isPlayer)
    {
        super(world, d, d1, d2, d3, d4, d5);
        particleGravity = 0.06F;
        particleRed = 1.0F;
        particleGreen = MobDismemberment.config.greenBlood == 1 && !isPlayer ? 1.0F : 0.0F;
        particleBlue = 0.0F;
        particleScale *= 1.2F;
        multiplyVelocity(1.2F);
        motionY += rand.nextFloat() * 0.15F;
        motionZ *= 0.4F / (rand.nextFloat() * 0.9F + 0.1F);
        motionX *= 0.4F / (rand.nextFloat() * 0.9F + 0.1F);
        particleMaxAge = (int)(200F + (20F / (rand.nextFloat() * 0.9F + 0.1F)));
        setSize(0.01F, 0.01F);
        setParticleTextureIndex(19 + rand.nextInt(4));
    }

    @Override
    public void onUpdate()
    {
        if(particleAge++ >= particleMaxAge)
        {
            setExpired();
            return;
        }
        prevPosX = posX;
        prevPosY = posY;
        prevPosZ = posZ;
        if(motionX != 0.0D && motionZ != 0.0D && !onGround)
        {
            motionY -= (double)this.particleGravity;
            move(motionX, motionY, motionZ);
            motionX *= 0.98000001907348633D;
            motionY *= 0.98000001907348633D;
            motionZ *= 0.98000001907348633D;
            if(onGround)
            {
                motionX *= 0.69999998807907104D;
                motionZ *= 0.69999998807907104D;
                posY += 0.2D;
            }
        }
    }

    @Override
    public void move(double x, double y, double z) // Fix mojangbug
    {
        double d3 = x;
        double d4 = y;
        double d5 = z;
        if(this.canCollide)
        {
            List<AxisAlignedBB> list = this.world.getCollisionBoxes((Entity)null, this.getBoundingBox().addCoord(x, y, z));

            for(AxisAlignedBB axisalignedbb : list)
            {
                y = axisalignedbb.calculateYOffset(this.getBoundingBox(), y);
            }

            this.setBoundingBox(this.getBoundingBox().offset(0.0D, y, 0.0D));

            for(AxisAlignedBB axisalignedbb1 : list)
            {
                x = axisalignedbb1.calculateXOffset(this.getBoundingBox(), x);
            }

            this.setBoundingBox(this.getBoundingBox().offset(x, 0.0D, 0.0D));

            for(AxisAlignedBB axisalignedbb2 : list)
            {
                z = axisalignedbb2.calculateZOffset(this.getBoundingBox(), z);
            }

            this.setBoundingBox(this.getBoundingBox().offset(0.0D, 0.0D, z));
        }
        else
        {
            this.setBoundingBox(this.getBoundingBox().offset(x, y, z));
        }

        this.resetPositionToBB();
        this.onGround = d4 != y && d4 < 0.0D;

        if(d3 != x)
        {
            this.motionX = 0.0D;
        }

        if(d5 != z)
        {
            this.motionZ = 0.0D;
        }
    }

    @Override
    public int getFXLayer()
    {
        return 0;
    }

}
