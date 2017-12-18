package stevekung.mods.moreplanets.module.planets.diona.entity;

import java.util.Iterator;
import java.util.List;
import java.util.Random;

import micdoodle8.mods.galacticraft.api.GalacticraftRegistry;
import micdoodle8.mods.galacticraft.core.GalacticraftCore;
import micdoodle8.mods.galacticraft.core.entities.IBoss;
import micdoodle8.mods.galacticraft.core.network.PacketSimple;
import micdoodle8.mods.galacticraft.core.network.PacketSimple.EnumSimplePacket;
import micdoodle8.mods.galacticraft.core.tile.TileEntityDungeonSpawner;
import micdoodle8.mods.galacticraft.core.util.ConfigManagerCore;
import micdoodle8.mods.galacticraft.core.util.GCCoreUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.World;
import net.minecraftforge.common.ChestGenHooks;
import net.minecraftforge.fml.common.network.NetworkRegistry.TargetPoint;
import stevekung.mods.moreplanets.core.MorePlanetsCore;
import stevekung.mods.moreplanets.init.MPPotions;
import stevekung.mods.moreplanets.module.planets.diona.items.DionaItems;
import stevekung.mods.moreplanets.util.EnumParticleTypesMP;
import stevekung.mods.moreplanets.util.IMorePlanetsBossDisplayData;
import stevekung.mods.moreplanets.util.entity.EntitySlimeBaseMP;
import stevekung.mods.moreplanets.util.helper.ItemLootHelper;
import stevekung.mods.moreplanets.util.tileentity.TileEntityTreasureChestMP;

public class EntityInfectedCrystallizeSlimeBoss extends EntitySlimeBaseMP implements IMorePlanetsBossDisplayData, IBoss
{
    private TileEntityDungeonSpawner spawner;
    public int deathTicks = 0;
    public int entitiesWithin;
    public int entitiesWithinLast;
    public boolean barrier;
    public EntityInfectedCrystallizeTentacle tentacle;

    public EntityInfectedCrystallizeSlimeBoss(World world)
    {
        super(world);
    }

    @Override
    public void knockBack(Entity entity, float strength, double xRatio, double zRatio) {}

    @Override
    public boolean canBePushed()
    {
        return false;
    }

    @Override
    protected void applyEntityAttributes()
    {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.followRange).setBaseValue(this.getDetectRange());
    }

    @Override
    public IEntityLivingData onInitialSpawn(DifficultyInstance difficulty, IEntityLivingData data)
    {
        EntityInfectedCrystallizeTentacle tentacle1 = new EntityInfectedCrystallizeTentacle(this.worldObj);
        tentacle1.setLocationAndAngles(this.posX + 5.0F, this.posY + 2.5F, this.posZ + 5.0F, 0.0F, 0.0F);
        this.worldObj.spawnEntityInWorld(tentacle1);

        EntityInfectedCrystallizeTentacle tentacle2 = new EntityInfectedCrystallizeTentacle(this.worldObj);
        tentacle2.setLocationAndAngles(this.posX - 5.0F, this.posY + 2.5F, this.posZ - 5.0F, 0.0F, 0.0F);
        this.worldObj.spawnEntityInWorld(tentacle2);

        EntityInfectedCrystallizeTentacle tentacle3 = new EntityInfectedCrystallizeTentacle(this.worldObj);
        tentacle3.setLocationAndAngles(this.posX + 5.0F, this.posY + 2.5F, this.posZ - 5.0F, 0.0F, 0.0F);
        this.worldObj.spawnEntityInWorld(tentacle3);

        EntityInfectedCrystallizeTentacle tentacle4 = new EntityInfectedCrystallizeTentacle(this.worldObj);
        tentacle4.setLocationAndAngles(this.posX - 5.0F, this.posY + 2.5F, this.posZ + 5.0F, 0.0F, 0.0F);
        this.worldObj.spawnEntityInWorld(tentacle4);
        this.setSlimeSize(5);
        return data;
    }

    @Override
    public float getSizeBased()
    {
        return 1.01000005F;
    }

    @Override
    protected void onDeathUpdate()
    {
        ++this.deathTicks;

        if (this.deathTicks >= 180 && this.deathTicks <= 200)
        {
            float f = (this.rand.nextFloat() - 0.5F) * 1.5F;
            float f1 = (this.rand.nextFloat() - 0.5F) * 2.0F;
            float f2 = (this.rand.nextFloat() - 0.5F) * 1.5F;
            this.worldObj.spawnParticle(EnumParticleTypes.EXPLOSION_HUGE, this.posX + f, this.posY + 2.0D + f1, this.posZ + f2, 0.0D, 0.0D, 0.0D);
        }

        int i;
        int j;

        if (!this.worldObj.isRemote)
        {
            if (this.deathTicks >= 180 && this.deathTicks % 5 == 0)
            {
                GalacticraftCore.packetPipeline.sendToAllAround(new PacketSimple(EnumSimplePacket.C_PLAY_SOUND_EXPLODE, GCCoreUtil.getDimensionID(this.worldObj), new Object[] { }), new TargetPoint(GCCoreUtil.getDimensionID(this.worldObj), this.posX, this.posY, this.posZ, 40.0D));
            }
            if (this.deathTicks > 150 && this.deathTicks % 5 == 0)
            {
                i = 120;

                while (i > 0)
                {
                    j = EntityXPOrb.getXPSplit(i);
                    i -= j;
                    this.worldObj.spawnEntityInWorld(new EntityXPOrb(this.worldObj, this.posX, this.posY, this.posZ, j));
                }
            }

            if (this.deathTicks == 40)
            {
                GalacticraftCore.packetPipeline.sendToAllAround(new PacketSimple(EnumSimplePacket.C_PLAY_SOUND_BOSS_DEATH, GCCoreUtil.getDimensionID(this.worldObj), new Object[] { this.getSoundPitch() - 0.1F }), new TargetPoint(GCCoreUtil.getDimensionID(this.worldObj), this.posX, this.posY, this.posZ, 40.0D));
            }
        }

        if (this.deathTicks == 200 && !this.worldObj.isRemote)
        {
            i = 120;

            while (i > 0)
            {
                j = EntityXPOrb.getXPSplit(i);
                i -= j;
                this.worldObj.spawnEntityInWorld(new EntityXPOrb(this.worldObj, this.posX, this.posY, this.posZ, j));
            }

            TileEntityTreasureChestMP chest = null;

            if (this.spawner != null && this.spawner.getChestPos() != null)
            {
                TileEntity chestTest = this.worldObj.getTileEntity(this.spawner.getChestPos());

                if (chestTest != null && chestTest instanceof TileEntityTreasureChestMP)
                {
                    chest = (TileEntityTreasureChestMP) chestTest;
                }
            }

            if (chest == null)
            {
                chest = TileEntityTreasureChestMP.findClosest(this, 4);
            }

            if (chest != null)
            {
                double dist = this.getDistanceSq(chest.getPos().getX() + 0.5, chest.getPos().getY() + 0.5, chest.getPos().getZ() + 0.5);

                if (dist < 1000 * 1000)
                {
                    if (!chest.locked)
                    {
                        chest.locked = true;
                    }

                    for (int k = 0; k < chest.getSizeInventory(); k++)
                    {
                        chest.setInventorySlotContents(k, null);
                    }

                    ChestGenHooks info = ChestGenHooks.getInfo(ItemLootHelper.COMMON_SPACE_DUNGEON);

                    // Generate twice, since it's an extra special chest
                    WeightedRandomChestContent.generateChestContents(this.rand, info.getItems(this.rand), chest, info.getCount(this.rand));
                    WeightedRandomChestContent.generateChestContents(this.rand, info.getItems(this.rand), chest, info.getCount(this.rand));
                    WeightedRandomChestContent.generateChestContents(this.rand, info.getItems(this.rand), chest, info.getCount(this.rand));

                    ItemStack schematic = this.getGuaranteedLoot(this.rand);
                    int slot = this.rand.nextInt(chest.getSizeInventory());
                    chest.setInventorySlotContents(slot, schematic);
                }
            }

            this.entityDropItem(new ItemStack(DionaItems.DIONA_DUNGEON_KEY, 1, 0), 0.5F);
            this.setDead();

            if (this.spawner != null)
            {
                this.spawner.isBossDefeated = true;
                this.spawner.boss = null;
                this.spawner.spawned = false;
            }
        }
    }

    private ItemStack getGuaranteedLoot(Random rand)
    {
        List<ItemStack> stackList = GalacticraftRegistry.getDungeonLoot(4);
        return stackList.get(rand.nextInt(stackList.size())).copy();
    }

    @Override
    public void fall(float distance, float damageMultiplier) {}

    @Override
    public void onUpdate()
    {
        super.onUpdate();
        List<EntityInfectedCrystallizeTentacle> list = this.worldObj.getEntitiesWithinAABB(EntityInfectedCrystallizeTentacle.class, this.getEntityBoundingBox().expand(32.0F, 32.0F, 32.0F));
        this.updateTentacle();

        if (list.size() > 0)
        {
            this.barrier = true;
        }
        else
        {
            this.barrier = false;
        }
    }

    @Override
    protected void dropFewItems(boolean drop, int fortune)
    {
        int j = 3 + this.rand.nextInt(4);

        if (fortune > 0)
        {
            j += this.rand.nextInt(fortune + 1);
        }
        for (int k = 0; k < j; ++k)
        {
            this.entityDropItem(new ItemStack(DionaItems.DIONA_ITEM, 1, 4), 0.0F);
        }
    }

    @Override
    public void onLivingUpdate()
    {
        if (this.spawner != null)
        {
            List<EntityPlayer> playersWithin = this.worldObj.getEntitiesWithinAABB(EntityPlayer.class, this.spawner.getRangeBounds());
            this.entitiesWithin = playersWithin.size();

            if (this.entitiesWithin == 0 && this.entitiesWithinLast != 0)
            {
                List<EntityPlayer> entitiesWithin2 = this.worldObj.getEntitiesWithinAABB(EntityPlayer.class, this.spawner.getRangeBoundsPlus11());

                for (EntityPlayer p : entitiesWithin2)
                {
                    p.addChatMessage(new ChatComponentText(GCCoreUtil.translate("gui.skeleton_boss.message")));
                }
                this.setDead();
                return;
            }
            this.entitiesWithinLast = this.entitiesWithin;
        }
        super.onLivingUpdate();
    }

    private void updateTentacle()
    {
        if (this.tentacle != null)
        {
            if (this.tentacle.isDead)
            {
                this.tentacle = null;
            }
        }

        List<EntityInfectedCrystallizeTentacle> list = this.worldObj.getEntitiesWithinAABB(EntityInfectedCrystallizeTentacle.class, this.getEntityBoundingBox().expand(32.0F, 32.0F, 32.0F));
        EntityInfectedCrystallizeTentacle tentacle = null;
        double distance1 = Double.MAX_VALUE;
        Iterator iterator = list.iterator();

        while (iterator.hasNext())
        {
            EntityInfectedCrystallizeTentacle tentacle1 = (EntityInfectedCrystallizeTentacle)iterator.next();
            double distance = tentacle1.getDistanceSqToEntity(this);

            if (distance < distance1)
            {
                distance1 = distance;
                tentacle = tentacle1;
            }
            this.tentacle = tentacle;
        }
    }

    @Override
    public void setDead()
    {
        int i = this.getSlimeSize();

        if (this.spawner != null)
        {
            this.spawner.isBossDefeated = false;
            this.spawner.boss = null;
            this.spawner.spawned = false;
        }
        if (!this.worldObj.isRemote && i > 1 && this.getHealth() <= 0.0F)
        {
            int j = 8 + this.rand.nextInt(8);

            for (int k = 0; k < j; ++k)
            {
                float f = (k % 2 - 0.5F) * i / 4.0F;
                float f1 = (k / 2 - 0.5F) * i / 4.0F;
                EntityInfectedCrystallizeSlimeMinion entityslime = new EntityInfectedCrystallizeSlimeMinion(this.worldObj);

                if (this.hasCustomName())
                {
                    entityslime.setCustomNameTag(this.getCustomNameTag());
                }
                if (this.isNoDespawnRequired())
                {
                    entityslime.enablePersistence();
                }
                entityslime.setSlimeSize(i / 2);
                entityslime.setLocationAndAngles(this.posX + f, this.posY + 0.5D, this.posZ + f1, this.rand.nextFloat() * 360.0F, 0.0F);
                this.worldObj.spawnEntityInWorld(entityslime);
            }
        }
        this.isDead = true;
    }

    @Override
    public void onKillCommand()
    {
        this.setHealth(0.0F);
    }

    @Override
    protected void dealDamage(EntityLivingBase entity)
    {
        if (this.canEntityBeSeen(entity) && this.getDistanceSqToEntity(entity) < this.getDetectRange() && entity.attackEntityFrom(DamageSource.causeMobDamage(this), this.getAttackStrength()))
        {
            this.applyEnchantments(this, entity);
            entity.addPotionEffect(new PotionEffect(MPPotions.INFECTED_CRYSTALLIZE.id, 200, 1));
        }
    }

    @Override
    protected int getAttackStrength()
    {
        return 12;
    }

    @Override
    protected String getHurtSound()
    {
        return "mob.slime.big";
    }

    @Override
    protected String getDeathSound()
    {
        return "mob.slime.big";
    }

    @Override
    public boolean attackEntityFrom(DamageSource damageSource, float damage)
    {
        if (this.isEntityInvulnerable(damageSource))
        {
            return false;
        }
        else if (this.barrier && !damageSource.isCreativePlayer())
        {
            return false;
        }
        else if (super.attackEntityFrom(damageSource, damage))
        {
            Entity entity = damageSource.getEntity();

            if (this.riddenByEntity != entity && this.ridingEntity != entity)
            {
                if (entity != this)
                {
                    this.setRevengeTarget((EntityLivingBase) entity);
                }
                return true;
            }
            else
            {
                return true;
            }
        }
        else
        {
            return false;
        }
    }

    @Override
    public void onBossSpawned(TileEntityDungeonSpawner spawner)
    {
        this.spawner = spawner;
    }

    @Override
    public boolean isPotionApplicable(PotionEffect potion)
    {
        return potion.getPotionID() == MPPotions.INFECTED_CRYSTALLIZE.id ? false : super.isPotionApplicable(potion);
    }

    @Override
    protected double getDetectRange()
    {
        return 512.0D;
    }

    @Override
    public int getJumpDelay()
    {
        return this.rand.nextInt(2) + 3;
    }

    @Override
    protected EntitySlimeBaseMP createInstance()
    {
        return new EntityInfectedCrystallizeSlimeBoss(this.worldObj);
    }

    @Override
    protected void createParticles()
    {
        int i = this.getSlimeSize();

        for (int j = 0; j < i * 50; ++j)
        {
            float f = this.rand.nextFloat() * (float)Math.PI * 2.5F;
            float f1 = this.rand.nextFloat() * 0.65F + 0.65F;
            float f2 = MathHelper.sin(f) * i * 0.65F * f1;
            float f3 = MathHelper.cos(f) * i * 0.65F * f1;
            double d0 = this.posX + f2;
            double d1 = this.posZ + f3;
            MorePlanetsCore.PROXY.spawnParticle(EnumParticleTypesMP.INFECTED_CRYSTALLIZE_SLIME, d0, this.getEntityBoundingBox().minY, d1);
        }
    }

    @Override
    protected void overrideHealth()
    {
        this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(500.0F * ConfigManagerCore.dungeonBossHealthMod);
    }

    @Override
    public float getBossMaxHealth()
    {
        return this.getMaxHealth();
    }

    @Override
    public float getBossHealth()
    {
        return this.getHealth();
    }

    @Override
    public IChatComponent getBossDisplayName()
    {
        return this.getDisplayName();
    }
}