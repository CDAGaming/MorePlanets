package stevekung.mods.moreplanets.module.planets.chalos.entity;

import javax.annotation.Nullable;

import micdoodle8.mods.galacticraft.api.entity.IEntityBreathable;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.*;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.pathfinding.PathNavigate;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import stevekung.mods.moreplanets.init.MPLootTables;
import stevekung.mods.moreplanets.module.planets.chalos.blocks.ChalosBlocks;
import stevekung.mods.moreplanets.module.planets.chalos.items.ChalosItems;
import stevekung.mods.moreplanets.util.entity.ai.EntityAITemptMP;
import stevekung.mods.moreplanets.util.entity.ai.PathNavigateGroundMP;

public class EntityCheeseCow extends EntityAnimal implements IEntityBreathable
{
    public EntityCheeseCow(World world)
    {
        super(world);
        this.setSize(0.9F, 1.3F);
    }

    @Override
    protected void initEntityAI()
    {
        this.tasks.addTask(0, new EntityAISwimming(this));
        this.tasks.addTask(1, new EntityAIPanic(this, 2.0D));
        this.tasks.addTask(2, new EntityAIMate(this, 1.0D));
        this.tasks.addTask(3, new EntityAITemptMP(this, 1.25D, false, new ItemStack(ChalosItems.CHEESE_FOOD, 1, 0)));
        this.tasks.addTask(4, new EntityAIFollowParent(this, 1.25D));
        this.tasks.addTask(5, new EntityAIWander(this, 1.0D));
        this.tasks.addTask(6, new EntityAIWatchClosest(this, EntityPlayer.class, 6.0F));
        this.tasks.addTask(7, new EntityAILookIdle(this));
    }

    @Override
    protected PathNavigate getNewNavigator(World world)
    {
        return new PathNavigateGroundMP(this, world);
    }

    @Override
    public boolean getCanSpawnHere()
    {
        return this.worldObj.getBlockState(this.getPosition().down()).getBlock() == ChalosBlocks.CHEESE_GRASS;
    }

    @Override
    protected void applyEntityAttributes()
    {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(12.0D);
        this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.20000000298023224D);
    }

    @Override
    protected int getExperiencePoints(EntityPlayer player)
    {
        return 1 + this.worldObj.rand.nextInt(5);
    }

    @Override
    public boolean isBreedingItem(ItemStack itemStack)
    {
        return itemStack != null && itemStack.getItem() == ChalosItems.CHEESE_FOOD && itemStack.getItemDamage() == 0;
    }

    @Override
    protected SoundEvent getAmbientSound()
    {
        return SoundEvents.ENTITY_COW_AMBIENT;
    }

    @Override
    protected SoundEvent getHurtSound()
    {
        return SoundEvents.ENTITY_COW_HURT;
    }

    @Override
    protected SoundEvent getDeathSound()
    {
        return SoundEvents.ENTITY_COW_DEATH;
    }

    @Override
    protected void playStepSound(BlockPos pos, Block block)
    {
        this.playSound(SoundEvents.ENTITY_COW_STEP, 0.15F, 1.0F);
    }

    @Override
    public EntityCheeseCow createChild(EntityAgeable entity)
    {
        return new EntityCheeseCow(this.worldObj);
    }

    @Override
    public boolean processInteract(EntityPlayer player, EnumHand hand, @Nullable ItemStack itemStack)
    {
        if (itemStack != null && itemStack.getItem() == Items.BUCKET && !player.capabilities.isCreativeMode)
        {
            if (itemStack.stackSize-- == 1)
            {
                player.inventory.setInventorySlotContents(player.inventory.currentItem, new ItemStack(ChalosItems.CHEESE_OF_MILK_FLUID_BUCKET));
            }
            else if (!player.inventory.addItemStackToInventory(new ItemStack(ChalosItems.CHEESE_OF_MILK_FLUID_BUCKET)))
            {
                player.dropItem(new ItemStack(ChalosItems.CHEESE_OF_MILK_FLUID_BUCKET, 1, 0), false);
            }
            return true;
        }
        else
        {
            return super.processInteract(player, hand, itemStack);
        }
    }

    @Override
    @Nullable
    protected ResourceLocation getLootTable()
    {
        return MPLootTables.CHEESE_COW;
    }

    @Override
    public boolean canBreath()
    {
        return true;
    }

    @Override
    public float getEyeHeight()
    {
        return this.isChild() ? this.height : 1.3F;
    }
}