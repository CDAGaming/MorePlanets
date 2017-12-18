package stevekung.mods.moreplanets.module.planets.nibiru.entity;

import java.util.List;
import java.util.Random;

import micdoodle8.mods.galacticraft.api.GalacticraftRegistry;
import micdoodle8.mods.galacticraft.api.entity.IEntityBreathable;
import micdoodle8.mods.galacticraft.core.GalacticraftCore;
import micdoodle8.mods.galacticraft.core.entities.IBoss;
import micdoodle8.mods.galacticraft.core.network.PacketSimple;
import micdoodle8.mods.galacticraft.core.network.PacketSimple.EnumSimplePacket;
import micdoodle8.mods.galacticraft.core.tile.TileEntityDungeonSpawner;
import micdoodle8.mods.galacticraft.core.util.ConfigManagerCore;
import micdoodle8.mods.galacticraft.core.util.GCCoreUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.*;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.PotionEffect;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.world.World;
import net.minecraftforge.common.ChestGenHooks;
import net.minecraftforge.fml.common.network.NetworkRegistry.TargetPoint;
import stevekung.mods.moreplanets.init.MPPotions;
import stevekung.mods.moreplanets.module.planets.nibiru.items.NibiruItems;
import stevekung.mods.moreplanets.util.IMorePlanetsBossDisplayData;
import stevekung.mods.moreplanets.util.entity.ISpaceMob;
import stevekung.mods.moreplanets.util.helper.ItemLootHelper;
import stevekung.mods.moreplanets.util.tileentity.TileEntityTreasureChestMP;

public class EntityMiniVeinFloater extends EntityMob implements IMorePlanetsBossDisplayData, IBoss, IEntityBreathable, ISpaceMob
{
    private TileEntityDungeonSpawner spawner;
    public int deathTicks = 0;
    public int entitiesWithin;
    public int entitiesWithinLast;
    public boolean useVineAttacking;

    public EntityMiniVeinFloater(World world)
    {
        super(world);
        this.isImmuneToFire = true;
        this.tasks.addTask(1, new EntityAISwimming(this));
        this.tasks.addTask(2, new EntityAIWander(this, 1.0D));
        this.tasks.addTask(3, new EntityAIWatchClosest(this, EntityPlayer.class, 8.0F));
        this.tasks.addTask(3, new EntityAILookIdle(this));
        this.targetTasks.addTask(1, new EntityAIHurtByTarget(this, false));
        this.targetTasks.addTask(2, new EntityAINearestAttackableTarget(this, EntityPlayer.class, false, true));
        this.setSize(3.0F, 8.0F);
    }

    @Override
    public void onUpdate()
    {
        super.onUpdate();
        this.motionY *= 0.5D;

        if (this.getHealth() <= 0.0F)
        {
            this.useVineAttacking = false;
            return;
        }

        int tick = this.ticksExisted;
        tick %= 300;

        if (!this.isDead)
        {
            if (tick > 150)
            {
                this.useVineAttacking = true;
            }
            else
            {
                this.useVineAttacking = false;
            }
        }

        if (this.useVineAttacking)
        {
            int range = 32;
            List<EntityPlayer> entitiesAroundBH = this.worldObj.getEntitiesWithinAABB(EntityPlayer.class, AxisAlignedBB.fromBounds(this.posX - range, this.posY - range, this.posZ - range, this.posX + range, this.posY + range, this.posZ + range));

            for (EntityPlayer entity : entitiesAroundBH)
            {
                if (!entity.capabilities.isCreativeMode)
                {
                    double motionX = this.posX - entity.posX;
                    double motionY = this.posY - entity.posY + 4.0D;
                    double motionZ = this.posZ - entity.posZ;
                    entity.motionX = motionX * 0.025F;
                    entity.motionY = motionY * 0.025F;
                    entity.motionZ = motionZ * 0.025F;
                    List<EntityPlayer> entityNearBH = this.worldObj.getEntitiesWithinAABB(entity.getClass(), AxisAlignedBB.fromBounds(this.posX - 1.0D, this.posY - 1.0D, this.posZ - 1.0D, this.posX + 5.0D, this.posY + 6.5D, this.posZ + 5.0D));

                    for (EntityPlayer near : entityNearBH)
                    {
                        near.attackEntityFrom(DamageSource.causeMobDamage(this), 4.0F);
                    }
                }
            }
        }
    }

    @Override
    public boolean canBeCollidedWith()
    {
        return true;
    }

    @Override
    protected void collideWithEntity(Entity entity) {}

    @Override
    public void onKillCommand()
    {
        this.setHealth(0.0F);
    }

    @Override
    public void fall(float distance, float damageMultiplier) {}

    @Override
    public boolean isPotionApplicable(PotionEffect potion)
    {
        return potion.getPotionID() == MPPotions.INFECTED_SPORE.id ? false : super.isPotionApplicable(potion);
    }

    @Override
    public boolean isInWater()
    {
        return false;
    }

    @Override
    public boolean handleWaterMovement()
    {
        return false;
    }

    @Override
    public void knockBack(Entity entity, float strength, double xRatio, double zRatio) {}

    @Override
    protected void applyEntityAttributes()
    {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(750.0F * ConfigManagerCore.dungeonBossHealthMod);
        this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(0.0D);
    }

    @Override
    protected void updateAITasks()
    {
        if (this.ticksExisted % 60 == 0)
        {
            this.heal(5.0F);
        }
    }

    @Override
    public boolean canBePushed()
    {
        return false;
    }

    @Override
    protected void onDeathUpdate()
    {
        ++this.deathTicks;

        if (this.deathTicks >= 180 && this.deathTicks <= 200)
        {
            float f = (this.rand.nextFloat() - 0.5F) * 5.5F;
            float f1 = (this.rand.nextFloat() - 0.5F) * 28.0F;
            float f2 = (this.rand.nextFloat() - 0.5F) * 5.5F;
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
                i = 200;

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
            i = 200;

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
                chest = TileEntityTreasureChestMP.findClosest(this, 6);
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

            this.entityDropItem(new ItemStack(NibiruItems.NIBIRU_DUNGEON_KEY, 1, this.rand.nextBoolean() ? 1 : 2), 0.5F);
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
        List<ItemStack> stackList = GalacticraftRegistry.getDungeonLoot(5);
        return stackList.get(rand.nextInt(stackList.size())).copy();
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

    @Override
    public void setDead()
    {
        if (this.spawner != null)
        {
            this.spawner.isBossDefeated = false;
            this.spawner.boss = null;
            this.spawner.spawned = false;
        }
        super.setDead();
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound nbt)
    {
        super.writeEntityToNBT(nbt);
        nbt.setBoolean("UseVineAttacking", this.useVineAttacking);
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound nbt)
    {
        super.readEntityFromNBT(nbt);
        this.useVineAttacking = nbt.getBoolean("UseVineAttacking");
    }

    @Override
    public void onBossSpawned(TileEntityDungeonSpawner spawner)
    {
        this.spawner = spawner;
    }

    @Override
    public EnumMobType getMobType()
    {
        return EnumMobType.NIBIRU;
    }

    @Override
    public boolean canBreath()
    {
        return true;
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