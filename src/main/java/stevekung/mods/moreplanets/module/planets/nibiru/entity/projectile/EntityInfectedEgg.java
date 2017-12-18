package stevekung.mods.moreplanets.module.planets.nibiru.entity.projectile;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import stevekung.mods.moreplanets.core.MorePlanetsCore;
import stevekung.mods.moreplanets.module.planets.nibiru.entity.EntityInfectedChicken;
import stevekung.mods.moreplanets.util.EnumParticleTypesMP;

public class EntityInfectedEgg extends EntityThrowable
{
    public EntityInfectedEgg(World world)
    {
        super(world);
    }

    public EntityInfectedEgg(World world, EntityLivingBase thrower)
    {
        super(world, thrower);
    }

    public EntityInfectedEgg(World world, double x, double y, double z)
    {
        super(world, x, y, z);
    }

    @Override
    protected void onImpact(MovingObjectPosition moving)
    {
        if (moving.entityHit != null)
        {
            moving.entityHit.attackEntityFrom(DamageSource.causeThrownDamage(this, this.getThrower()), 0.0F);
        }

        if (!this.worldObj.isRemote && this.rand.nextInt(8) == 0)
        {
            int i = 1;

            if (this.rand.nextInt(32) == 0)
            {
                i = 4;
            }

            for (int j = 0; j < i; ++j)
            {
                EntityInfectedChicken entitychicken = new EntityInfectedChicken(this.worldObj);
                entitychicken.setGrowingAge(-24000);
                entitychicken.setLocationAndAngles(this.posX, this.posY, this.posZ, this.rotationYaw, 0.0F);
                this.worldObj.spawnEntityInWorld(entitychicken);
            }
        }

        for (int k = 0; k < 8; ++k)
        {
            MorePlanetsCore.PROXY.spawnParticle(EnumParticleTypesMP.INFECTED_EGG, this.posX, this.posY, this.posZ, (this.rand.nextFloat() - 0.5D) * 0.08D, (this.rand.nextFloat() - 0.5D) * 0.08D, (this.rand.nextFloat() - 0.5D) * 0.08D);
        }
        if (!this.worldObj.isRemote)
        {
            this.setDead();
        }
    }
}