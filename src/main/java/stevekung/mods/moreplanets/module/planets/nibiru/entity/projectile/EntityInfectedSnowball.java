package stevekung.mods.moreplanets.module.planets.nibiru.entity.projectile;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityBlaze;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import stevekung.mods.moreplanets.core.MorePlanetsCore;
import stevekung.mods.moreplanets.init.MPPotions;
import stevekung.mods.moreplanets.util.EnumParticleTypesMP;

public class EntityInfectedSnowball extends EntityThrowable
{
    public EntityInfectedSnowball(World world)
    {
        super(world);
    }

    public EntityInfectedSnowball(World world, EntityLivingBase thrower)
    {
        super(world, thrower);
    }

    public EntityInfectedSnowball(World world, double x, double y, double z)
    {
        super(world, x, y, z);
    }

    @Override
    protected void onImpact(MovingObjectPosition moving)
    {
        if (moving.entityHit != null)
        {
            int i = 0;

            if (moving.entityHit instanceof EntityBlaze)
            {
                i = 3;
            }
            if (moving.entityHit instanceof EntityLivingBase)
            {
                ((EntityLivingBase)moving.entityHit).addPotionEffect(new PotionEffect(MPPotions.INFECTED_SPORE.id, 100, 0));
            }
            moving.entityHit.attackEntityFrom(DamageSource.causeThrownDamage(this, this.getThrower()), i);
        }

        for (int j = 0; j < 8; ++j)
        {
            MorePlanetsCore.PROXY.spawnParticle(EnumParticleTypesMP.INFECTED_SNOWBALL, this.posX, this.posY, this.posZ);
        }
        if (!this.worldObj.isRemote)
        {
            this.setDead();
        }
    }
}