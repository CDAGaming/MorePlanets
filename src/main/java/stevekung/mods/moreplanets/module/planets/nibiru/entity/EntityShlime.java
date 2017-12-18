package stevekung.mods.moreplanets.module.planets.nibiru.entity;

import java.util.List;
import java.util.Random;

import com.google.common.collect.Lists;

import micdoodle8.mods.galacticraft.api.entity.IEntityBreathable;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.*;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.pathfinding.PathEntity;
import net.minecraft.pathfinding.PathNavigate;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.*;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.IShearable;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import stevekung.mods.moreplanets.init.MPPotions;
import stevekung.mods.moreplanets.module.planets.nibiru.blocks.BlockNibiruTallGrass;
import stevekung.mods.moreplanets.module.planets.nibiru.blocks.NibiruBlocks;
import stevekung.mods.moreplanets.module.planets.nibiru.entity.ai.EntityAIShlimeEatGrass;
import stevekung.mods.moreplanets.module.planets.nibiru.items.NibiruItems;
import stevekung.mods.moreplanets.util.entity.ISpaceMob;
import stevekung.mods.moreplanets.util.entity.ai.EntityAITemptMP;
import stevekung.mods.moreplanets.util.entity.ai.PathNavigateGroundMP;

public class EntityShlime extends EntityAnimal implements IShearable, ISpaceMob, IEntityBreathable
{
    private InventoryCrafting inventoryCrafting = new InventoryCrafting(new Container()
    {
        @Override
        public boolean canInteractWith(EntityPlayer player)
        {
            return false;
        }
    }, 2, 1);

    private int jumpTicks = 0;
    private int jumpDuration = 0;
    private boolean wasJumping = false;
    private boolean wasOnGround = false;
    private int currentMoveTypeDuration = 0;
    private EnumMoveType moveType = EnumMoveType.HOP;
    private int sheepTimer;
    private EntityAIShlimeEatGrass entityAIEatGrass = new EntityAIShlimeEatGrass(this);
    public float squishAmount;
    public float squishFactor;
    public float prevSquishFactor;

    public EntityShlime(World world)
    {
        super(world);
        this.setSize(0.675F, 0.75F);
        this.jumpHelper = new ShlimeJumpHelper(this);
        this.moveHelper = new ShlimeMoveHelper(this);
        ((PathNavigateGroundMP)this.getNavigator()).setAvoidsWater(true);
        this.navigator.setHeightRequirement(1.5F);
        this.tasks.addTask(0, new EntityAISwimming(this));
        this.tasks.addTask(1, new AIPanic(this, 1.33D));
        this.tasks.addTask(2, new EntityAIMate(this, 0.8D));
        this.tasks.addTask(3, new EntityAITemptMP(this, 1.0D, new ItemStack(NibiruItems.INFECTED_WHEAT), false));
        this.tasks.addTask(3, new EntityAITemptMP(this, 1.0D, new ItemStack(NibiruItems.NIBIRU_FRUITS, 1, 6), false));
        this.tasks.addTask(4, new EntityAIFollowParent(this, 1.1D));
        this.tasks.addTask(5, this.entityAIEatGrass);
        this.tasks.addTask(6, new EntityAIWander(this, 0.6D));
        this.tasks.addTask(7, new EntityAIWatchClosest(this, EntityPlayer.class, 10.0F));
        this.tasks.addTask(8, new EntityAILookIdle(this));
        this.inventoryCrafting.setInventorySlotContents(0, new ItemStack(Items.dye, 1, 0));
        this.inventoryCrafting.setInventorySlotContents(1, new ItemStack(Items.dye, 1, 0));
        this.setMovementSpeed(0.0D);
    }

    @Override
    protected PathNavigate getNewNavigator(World world)
    {
        return new PathNavigateGroundMP(this, world);
    }

    @Override
    public boolean isPotionApplicable(PotionEffect potion)
    {
        return potion.getPotionID() == MPPotions.INFECTED_SPORE.id ? false : super.isPotionApplicable(potion);
    }

    @Override
    public boolean canBreath()
    {
        return true;
    }

    @Override
    public EnumMobType getMobType()
    {
        return EnumMobType.NIBIRU;
    }

    @Override
    public boolean getCanSpawnHere()
    {
        int i = MathHelper.floor_double(this.posX);
        int j = MathHelper.floor_double(this.getEntityBoundingBox().minY);
        int k = MathHelper.floor_double(this.posZ);
        BlockPos blockpos = new BlockPos(i, j, k);
        return this.worldObj.getBlockState(blockpos.down()).getBlock() == NibiruBlocks.INFECTED_GRASS && this.worldObj.getLight(blockpos) > 8 && this.getBlockPathWeight(new BlockPos(this.posX, this.getEntityBoundingBox().minY, this.posZ)) >= 0.0F;
    }

    @Override
    protected float getJumpUpwardsMotion()
    {
        return this.moveHelper.isUpdating() && this.moveHelper.getY() > this.posY + 0.5D ? 0.5F : this.moveType.getJumpHeight();
    }

    @Override
    protected void entityInit()
    {
        super.entityInit();
        this.dataWatcher.addObject(18, Byte.valueOf((byte)0));
        this.dataWatcher.addObject(19, new Byte((byte)0));
    }

    @Override
    public void onUpdate()
    {
        this.squishFactor += (this.squishAmount - this.squishFactor) * 0.5F;
        this.prevSquishFactor = this.squishFactor;
        super.onUpdate();

        if (this.onGround && !this.wasOnGround)
        {
            this.squishAmount = -0.5F;
        }
        else if (!this.onGround && this.wasOnGround)
        {
            this.squishAmount = 1.0F;
        }
        if (this.sheepTimer > 0 && this.sheepTimer <= 40)
        {
            Block blockDown = this.worldObj.getBlockState(this.getPosition().down()).getBlock();
            IBlockState block = this.worldObj.getBlockState(this.getPosition());

            if (blockDown == NibiruBlocks.INFECTED_GRASS)
            {
                this.worldObj.spawnParticle(EnumParticleTypes.BLOCK_CRACK, this.posX + (this.rand.nextFloat() - 0.5D) * this.width, this.getEntityBoundingBox().minY + 0.1D, this.posZ + (this.rand.nextFloat() - 0.5D) * this.width, 4.0D * (this.rand.nextFloat() - 0.5D), 0.5D, (this.rand.nextFloat() - 0.5D) * 4.0D, new int[] {Block.getStateId(NibiruBlocks.INFECTED_GRASS.getDefaultState())});
            }
            else if (blockDown == NibiruBlocks.GREEN_VEIN_GRASS)
            {
                this.worldObj.spawnParticle(EnumParticleTypes.BLOCK_CRACK, this.posX + (this.rand.nextFloat() - 0.5D) * this.width, this.getEntityBoundingBox().minY + 0.1D, this.posZ + (this.rand.nextFloat() - 0.5D) * this.width, 4.0D * (this.rand.nextFloat() - 0.5D), 0.5D, (this.rand.nextFloat() - 0.5D) * 4.0D, new int[] {Block.getStateId(NibiruBlocks.GREEN_VEIN_GRASS.getDefaultState())});
            }
            else if (block == NibiruBlocks.NIBIRU_TALL_GRASS.getDefaultState().withProperty(BlockNibiruTallGrass.VARIANT, BlockNibiruTallGrass.BlockType.INFECTED_TALL_GRASS))
            {
                this.worldObj.spawnParticle(EnumParticleTypes.BLOCK_CRACK, this.posX + (this.rand.nextFloat() - 0.5D) * this.width, this.getEntityBoundingBox().minY + 0.1D, this.posZ + (this.rand.nextFloat() - 0.5D) * this.width, 4.0D * (this.rand.nextFloat() - 0.5D), 0.5D, (this.rand.nextFloat() - 0.5D) * 4.0D, new int[] {Block.getStateId(NibiruBlocks.NIBIRU_TALL_GRASS.getDefaultState().withProperty(BlockNibiruTallGrass.VARIANT, BlockNibiruTallGrass.BlockType.INFECTED_TALL_GRASS))});
            }
            else if (block == NibiruBlocks.NIBIRU_TALL_GRASS.getDefaultState().withProperty(BlockNibiruTallGrass.VARIANT, BlockNibiruTallGrass.BlockType.GREEN_VEIN_TALL_GRASS))
            {
                this.worldObj.spawnParticle(EnumParticleTypes.BLOCK_CRACK, this.posX + (this.rand.nextFloat() - 0.5D) * this.width, this.getEntityBoundingBox().minY + 0.1D, this.posZ + (this.rand.nextFloat() - 0.5D) * this.width, 4.0D * (this.rand.nextFloat() - 0.5D), 0.5D, (this.rand.nextFloat() - 0.5D) * 4.0D, new int[] {Block.getStateId(NibiruBlocks.NIBIRU_TALL_GRASS.getDefaultState().withProperty(BlockNibiruTallGrass.VARIANT, BlockNibiruTallGrass.BlockType.GREEN_VEIN_TALL_GRASS))});
            }
        }
        this.alterSquishAmount();
    }

    @Override
    public void updateAITasks()
    {
        if (this.moveHelper.getSpeed() > 0.8D)
        {
            this.setMoveType(EnumMoveType.SPRINT);
        }
        else
        {
            this.setMoveType(EnumMoveType.HOP);
        }

        if (this.currentMoveTypeDuration > 0)
        {
            --this.currentMoveTypeDuration;
        }

        this.sheepTimer = this.entityAIEatGrass.getEatingGrassTimer();
        super.updateAITasks();

        if (this.onGround)
        {
            if (!this.wasOnGround)
            {
                this.setJumping(false, EnumMoveType.NONE);
                this.checkLandingDelay();
            }

            ShlimeJumpHelper jump = (ShlimeJumpHelper)this.jumpHelper;

            if (!jump.getIsJumping())
            {
                if (this.moveHelper.isUpdating() && this.currentMoveTypeDuration == 0)
                {
                    PathEntity pathentity = this.navigator.getPath();
                    Vec3 vec3 = new Vec3(this.moveHelper.getX(), this.moveHelper.getY(), this.moveHelper.getZ());

                    if (pathentity != null && pathentity.getCurrentPathIndex() < pathentity.getCurrentPathLength())
                    {
                        vec3 = pathentity.getPosition(this);
                    }
                    this.calculateRotationYaw(vec3.xCoord, vec3.zCoord);
                    this.doMovementAction(this.moveType);
                }
            }
            else if (!jump.canJump())
            {
                this.enableJumpControl();
            }
        }
        this.wasOnGround = this.onGround;
    }

    @Override
    public void onLivingUpdate()
    {
        super.onLivingUpdate();

        if (this.worldObj.isRemote)
        {
            this.sheepTimer = Math.max(0, this.sheepTimer - 1);
        }
        if (this.jumpTicks != this.jumpDuration)
        {
            if (this.jumpTicks == 0 && !this.worldObj.isRemote)
            {
                this.worldObj.setEntityState(this, (byte)1);
            }
            ++this.jumpTicks;
        }
        else if (this.jumpDuration != 0)
        {
            this.jumpTicks = 0;
            this.jumpDuration = 0;
        }
    }

    @Override
    protected void applyEntityAttributes()
    {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(10.0D);
        this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(0.30000001192092896D);
    }

    @Override
    protected String getHurtSound()
    {
        return "moreplanets:mob.shlime.hurt";
    }

    @Override
    protected String getDeathSound()
    {
        return "moreplanets:mob.shlime.death";
    }

    @Override
    public boolean attackEntityFrom(DamageSource source, float amount)
    {
        return this.isEntityInvulnerable(source) ? false : super.attackEntityFrom(source, amount);
    }

    @Override
    protected void dropFewItems(boolean drop, int fortune)
    {
        if (!this.getSheared())
        {
            this.entityDropItem(new ItemStack(Item.getItemFromBlock(Blocks.wool), 1, this.getFleeceColor().getMetadata()), 0.0F);
        }

        int i = this.rand.nextInt(2) + 1 + this.rand.nextInt(1 + fortune);

        for (int j = 0; j < i; ++j)
        {
            if (this.isBurning())
            {
                this.entityDropItem(new ItemStack(NibiruItems.NIBIRU_FOOD, 1, 1), 0.0F);
            }
            else
            {
                this.entityDropItem(new ItemStack(NibiruItems.NIBIRU_FOOD, 1, 0), 0.0F);
            }
        }
    }

    @Override
    protected void addRandomDrop()
    {
        this.entityDropItem(new ItemStack(NibiruItems.NIBIRU_ITEM, 1, 3), 0.0F);
    }

    @Override
    public EntityShlime createChild(EntityAgeable ageable)
    {
        EntityShlime entitysheep = (EntityShlime)ageable;
        EntityShlime entitysheep1 = new EntityShlime(this.worldObj);
        entitysheep1.setFleeceColor(this.getDyeColorMixFromParents(this, entitysheep));
        return entitysheep1;
    }

    @Override
    public void eatGrassBonus()
    {
        this.setSheared(false);

        if (this.isChild())
        {
            this.addGrowth(60);
        }
    }

    @Override
    public IEntityLivingData onInitialSpawn(DifficultyInstance difficulty, IEntityLivingData data)
    {
        this.setFleeceColor(this.getRandomSheepColor(this.worldObj.rand));
        return super.onInitialSpawn(difficulty, data);
    }

    @Override
    public boolean isBreedingItem(ItemStack itemStack)
    {
        return itemStack != null && (itemStack.getItem() == NibiruItems.INFECTED_WHEAT || itemStack.getItem() == NibiruItems.NIBIRU_FRUITS && itemStack.getItemDamage() == 6);
    }

    @Override
    public boolean isShearable(ItemStack itemStack, IBlockAccess world, BlockPos pos)
    {
        return !this.getSheared() && !this.isChild();
    }

    @Override
    public List<ItemStack> onSheared(ItemStack itemStack, IBlockAccess world, BlockPos pos, int fortune)
    {
        this.setSheared(true);
        int i = 1 + this.rand.nextInt(3);
        List<ItemStack> ret = Lists.newArrayList();

        for (int j = 0; j < i; ++j)
        {
            ret.add(new ItemStack(Item.getItemFromBlock(Blocks.wool), 1, this.getFleeceColor().getMetadata()));
        }
        this.playSound("mob.sheep.shear", 1.0F, 1.0F);
        return ret;
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound tagCompound)
    {
        super.writeEntityToNBT(tagCompound);
        tagCompound.setBoolean("Sheared", this.getSheared());
        tagCompound.setByte("Color", (byte)this.getFleeceColor().getMetadata());
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound tagCompund)
    {
        super.readEntityFromNBT(tagCompund);
        this.setSheared(tagCompund.getBoolean("Sheared"));
        this.setFleeceColor(EnumDyeColor.byMetadata(tagCompund.getByte("Color")));
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void handleStatusUpdate(byte id)
    {
        if (id == 1)
        {
            this.jumpDuration = 5;
            this.jumpTicks = 0;
        }
        else if (id == 10)
        {
            this.sheepTimer = 40;
        }
        else
        {
            super.handleStatusUpdate(id);
        }
    }

    @SideOnly(Side.CLIENT)
    public float getTailRotationAngleX(float partialTicks)
    {
        if (this.sheepTimer > 4 && this.sheepTimer <= 36)
        {
            float f = (this.sheepTimer - 4 - partialTicks) / 16.0F;
            return -(float)Math.PI / 5F + (float)Math.PI * 7F / 50F * MathHelper.sin(f * 28.7F);
        }
        else
        {
            return this.sheepTimer > 0 ? (float)Math.PI / 5F : -(240F / (float)Math.PI);
        }
    }

    public EnumDyeColor getFleeceColor()
    {
        return EnumDyeColor.byMetadata(this.dataWatcher.getWatchableObjectByte(19) & 15);
    }

    public void setFleeceColor(EnumDyeColor color)
    {
        byte b0 = this.dataWatcher.getWatchableObjectByte(19);
        this.dataWatcher.updateObject(19, Byte.valueOf((byte)(b0 & 240 | color.getMetadata() & 15)));
    }

    public boolean getSheared()
    {
        return (this.dataWatcher.getWatchableObjectByte(19) & 16) != 0;
    }

    private void setSheared(boolean sheared)
    {
        byte b0 = this.dataWatcher.getWatchableObjectByte(19);

        if (sheared)
        {
            this.dataWatcher.updateObject(19, Byte.valueOf((byte)(b0 | 16)));
        }
        else
        {
            this.dataWatcher.updateObject(19, Byte.valueOf((byte)(b0 & -17)));
        }
    }

    private int getMoveTypeDuration()
    {
        return this.moveType.getDuration();
    }

    private void setMoveType(EnumMoveType type)
    {
        this.moveType = type;
    }

    private void setMovementSpeed(double newSpeed)
    {
        this.getNavigator().setSpeed(newSpeed);
        this.moveHelper.setMoveTo(this.moveHelper.getX(), this.moveHelper.getY(), this.moveHelper.getZ(), newSpeed);
    }

    private void setJumping(boolean jump, EnumMoveType moveType)
    {
        super.setJumping(jump);

        if (!jump)
        {
            this.moveType = EnumMoveType.STEP;
        }
        else
        {
            this.setMovementSpeed(1.5D * moveType.getSpeed());
        }
        this.wasJumping = jump;
    }

    private void doMovementAction(EnumMoveType movetype)
    {
        this.setJumping(true, movetype);
        this.jumpDuration = movetype.getJumpDuration();
        this.jumpTicks = 0;
    }

    private boolean isWasJumping()
    {
        return this.wasJumping;
    }

    private void calculateRotationYaw(double x, double z)
    {
        this.rotationYaw = (float)(MathHelper.atan2(z - this.posZ, x - this.posX) * 180.0D / Math.PI) - 90.0F;
    }

    private void enableJumpControl()
    {
        ((ShlimeJumpHelper)this.jumpHelper).setCanJump(true);
    }

    private void disableJumpControl()
    {
        ((ShlimeJumpHelper)this.jumpHelper).setCanJump(false);
    }

    private void updateMoveTypeDuration()
    {
        this.currentMoveTypeDuration = this.getMoveTypeDuration();
    }

    private void checkLandingDelay()
    {
        this.updateMoveTypeDuration();
        this.disableJumpControl();
    }

    private EnumDyeColor getRandomSheepColor(Random random)
    {
        int i = random.nextInt(100);
        return i < 5 ? EnumDyeColor.BLACK : i < 10 ? EnumDyeColor.GRAY : i < 15 ? EnumDyeColor.SILVER : i < 18 ? EnumDyeColor.BROWN : random.nextInt(500) == 0 ? EnumDyeColor.PINK : EnumDyeColor.WHITE;
    }

    private EnumDyeColor getDyeColorMixFromParents(EntityAnimal father, EntityAnimal mother)
    {
        int i = ((EntityShlime)father).getFleeceColor().getDyeDamage();
        int j = ((EntityShlime)mother).getFleeceColor().getDyeDamage();
        this.inventoryCrafting.getStackInSlot(0).setItemDamage(i);
        this.inventoryCrafting.getStackInSlot(1).setItemDamage(j);
        ItemStack itemstack = CraftingManager.getInstance().findMatchingRecipe(this.inventoryCrafting, ((EntityShlime)father).worldObj);
        int k;

        if (itemstack != null && itemstack.getItem() == Items.dye)
        {
            k = itemstack.getMetadata();
        }
        else
        {
            k = this.worldObj.rand.nextBoolean() ? i : j;
        }
        return EnumDyeColor.byDyeDamage(k);
    }

    private void alterSquishAmount()
    {
        this.squishAmount *= 0.6F;
    }

    static enum EnumMoveType
    {
        NONE(0.0F, 0.0F, 10, 1),
        HOP(0.8F, 0.375F, 9, 6),
        STEP(1.0F, 0.45F, 7, 8),
        SPRINT(1.75F, 0.4F, 1, 4);

        private float speed;
        private float jumpHeight;
        private int duration;
        private int jumpDuration;

        private EnumMoveType(float typeSpeed, float jumpHeight, int typeDuration, int jumpDuration)
        {
            this.speed = typeSpeed;
            this.jumpHeight = jumpHeight;
            this.duration = typeDuration;
            this.jumpDuration = jumpDuration;
        }

        public float getSpeed()
        {
            return this.speed;
        }

        public float getJumpHeight()
        {
            return this.jumpHeight;
        }

        public int getDuration()
        {
            return this.duration;
        }

        public int getJumpDuration()
        {
            return this.jumpDuration;
        }
    }

    public class ShlimeJumpHelper extends EntityJumpHelper
    {
        private EntityShlime theEntity;
        private boolean canJump = false;

        public ShlimeJumpHelper(EntityShlime theEntity)
        {
            super(theEntity);
            this.theEntity = theEntity;
        }

        public boolean getIsJumping()
        {
            return this.isJumping;
        }

        public boolean canJump()
        {
            return this.canJump;
        }

        public void setCanJump(boolean canJump)
        {
            this.canJump = canJump;
        }

        @Override
        public void doJump()
        {
            if (this.isJumping)
            {
                this.theEntity.doMovementAction(EnumMoveType.STEP);
                this.isJumping = false;
            }
        }
    }

    static class ShlimeMoveHelper extends EntityMoveHelper
    {
        private EntityShlime sheep;

        public ShlimeMoveHelper(EntityShlime sheep)
        {
            super(sheep);
            this.sheep = sheep;
        }

        @Override
        public void onUpdateMoveHelper()
        {
            if (this.sheep.onGround && !this.sheep.isWasJumping())
            {
                this.sheep.setMovementSpeed(0.0D);
            }
            super.onUpdateMoveHelper();
        }
    }

    static class AIPanic extends EntityAIPanic
    {
        private EntityShlime theEntity;

        public AIPanic(EntityShlime theEntity, double speed)
        {
            super(theEntity, speed);
            this.theEntity = theEntity;
        }

        @Override
        public void updateTask()
        {
            super.updateTask();
            this.theEntity.setMovementSpeed(this.speed);
        }
    }
}