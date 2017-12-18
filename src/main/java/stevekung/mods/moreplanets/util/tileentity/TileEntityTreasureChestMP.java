package stevekung.mods.moreplanets.util.tileentity;

import java.util.Iterator;
import java.util.List;

import micdoodle8.mods.galacticraft.api.item.IKeyable;
import micdoodle8.mods.galacticraft.api.world.IGalacticraftWorldProvider;
import micdoodle8.mods.galacticraft.core.GalacticraftCore;
import micdoodle8.mods.galacticraft.core.network.PacketSimple;
import micdoodle8.mods.galacticraft.core.network.PacketSimple.EnumSimplePacket;
import micdoodle8.mods.galacticraft.core.tile.TileEntityAdvanced;
import micdoodle8.mods.galacticraft.core.util.GCCoreUtil;
import micdoodle8.mods.miccore.Annotations.NetworkedField;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.*;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.world.IInteractionObject;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class TileEntityTreasureChestMP extends TileEntityAdvanced implements IKeyable, IInteractionObject, ISidedInventory
{
    private ItemStack[] chestContents = new ItemStack[27];
    public float lidAngle;
    public float prevLidAngle;
    public int numPlayersUsing;
    private int ticksSinceSync;
    private int tier;
    private String name;
    private Block block;

    @NetworkedField(targetSide = Side.CLIENT)
    public boolean locked = true;

    public TileEntityTreasureChestMP(int tier, String name, Block block)
    {
        this.tier = tier;
        this.name = name;
        this.block = block;
    }

    @Override
    public int getSizeInventory()
    {
        return 27;
    }

    @Override
    public ItemStack getStackInSlot(int index)
    {
        return this.chestContents[index];
    }

    @Override
    public ItemStack decrStackSize(int index, int count)
    {
        if (this.chestContents[index] != null)
        {
            ItemStack itemstack;

            if (this.chestContents[index].stackSize <= count)
            {
                itemstack = this.chestContents[index];
                this.chestContents[index] = null;
                this.markDirty();
                return itemstack;
            }
            else
            {
                itemstack = this.chestContents[index].splitStack(count);

                if (this.chestContents[index].stackSize == 0)
                {
                    this.chestContents[index] = null;
                }
                this.markDirty();
                return itemstack;
            }
        }
        else
        {
            return null;
        }
    }

    @Override
    public ItemStack removeStackFromSlot(int index)
    {
        if (this.chestContents[index] != null)
        {
            ItemStack itemstack = this.chestContents[index];
            this.chestContents[index] = null;
            return itemstack;
        }
        else
        {
            return null;
        }
    }

    @Override
    public void setInventorySlotContents(int index, ItemStack stack)
    {
        this.chestContents[index] = stack;

        if (stack != null && stack.stackSize > this.getInventoryStackLimit())
        {
            stack.stackSize = this.getInventoryStackLimit();
        }
        this.markDirty();
    }

    @Override
    public String getName()
    {
        return StatCollector.translateToLocal("container." + this.name + ".treasurechest.name");
    }

    @Override
    public boolean hasCustomName()
    {
        return false;
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt)
    {
        super.readFromNBT(nbt);
        this.locked = nbt.getBoolean("isLocked");
        this.tier = nbt.getInteger("tier");
        NBTTagList nbttaglist = nbt.getTagList("Items", 10);
        this.chestContents = new ItemStack[this.getSizeInventory()];

        for (int i = 0; i < nbttaglist.tagCount(); ++i)
        {
            NBTTagCompound nbttagcompound1 = nbttaglist.getCompoundTagAt(i);
            int j = nbttagcompound1.getByte("Slot") & 255;

            if (j >= 0 && j < this.chestContents.length)
            {
                this.chestContents[j] = ItemStack.loadItemStackFromNBT(nbttagcompound1);
            }
        }
    }

    @Override
    public void writeToNBT(NBTTagCompound nbt)
    {
        super.writeToNBT(nbt);
        nbt.setBoolean("isLocked", this.locked);
        nbt.setInteger("tier", this.tier);
        NBTTagList nbttaglist = new NBTTagList();

        for (int i = 0; i < this.chestContents.length; ++i)
        {
            if (this.chestContents[i] != null)
            {
                NBTTagCompound nbttagcompound1 = new NBTTagCompound();
                nbttagcompound1.setByte("Slot", (byte)i);
                this.chestContents[i].writeToNBT(nbttagcompound1);
                nbttaglist.appendTag(nbttagcompound1);
            }
        }
        nbt.setTag("Items", nbttaglist);
    }

    @Override
    public int getInventoryStackLimit()
    {
        return 64;
    }

    @Override
    public boolean isUseableByPlayer(EntityPlayer player)
    {
        return this.worldObj.getTileEntity(this.pos) != this ? false : player.getDistanceSq(this.pos.getX() + 0.5D, this.pos.getY() + 0.5D, this.pos.getZ() + 0.5D) <= 64.0D;
    }

    @Override
    public void update()
    {
        int i = this.pos.getX();
        int j = this.pos.getY();
        int k = this.pos.getZ();
        ++this.ticksSinceSync;
        float f;

        if (this.locked)
        {
            this.numPlayersUsing = 0;
        }

        if (!this.worldObj.isRemote && this.numPlayersUsing != 0 && (this.ticksSinceSync + i + j + k) % 200 == 0)
        {
            this.numPlayersUsing = 0;
            f = 5.0F;
            List list = this.worldObj.getEntitiesWithinAABB(EntityPlayer.class, new AxisAlignedBB(i - f, j - f, k - f, i + 1 + f, j + 1 + f, k + 1 + f));
            Iterator iterator = list.iterator();

            while (iterator.hasNext())
            {
                EntityPlayer entityplayer = (EntityPlayer)iterator.next();

                if (entityplayer.openContainer instanceof ContainerChest)
                {
                    IInventory iinventory = ((ContainerChest)entityplayer.openContainer).getLowerChestInventory();

                    if (iinventory == this || iinventory instanceof InventoryLargeChest && ((InventoryLargeChest)iinventory).isPartOfLargeChest(this))
                    {
                        ++this.numPlayersUsing;
                    }
                }
            }
        }

        this.prevLidAngle = this.lidAngle;
        f = this.worldObj.provider instanceof IGalacticraftWorldProvider ? 0.05F : 0.1F;
        double d2;

        if (this.numPlayersUsing > 0 && this.lidAngle == 0.0F)
        {
            double d1 = i + 0.5D;
            d2 = k + 0.5D;
            this.worldObj.playSoundEffect(d1, j + 0.5D, d2, "random.chestopen", 0.5F, this.worldObj.provider instanceof IGalacticraftWorldProvider ? this.worldObj.rand.nextFloat() * 0.1F + 0.6F : this.worldObj.rand.nextFloat() * 0.1F + 0.9F);
        }

        if ((this.numPlayersUsing == 0 || this.locked) && this.lidAngle > 0.0F || this.numPlayersUsing > 0 && this.lidAngle < 1.0F)
        {
            float f1 = this.lidAngle;

            if (this.numPlayersUsing == 0 || this.locked)
            {
                this.lidAngle -= f;
            }
            else
            {
                this.lidAngle += f;
            }

            if (this.lidAngle > 1.0F)
            {
                this.lidAngle = 1.0F;
            }

            float f2 = 0.5F;

            if (this.lidAngle < f2 && f1 >= f2)
            {
                d2 = i + 0.5D;
                double d0 = k + 0.5D;
                this.worldObj.playSoundEffect(d2, j + 0.5D, d0, "random.chestclosed", 0.5F, this.worldObj.provider instanceof IGalacticraftWorldProvider ? this.worldObj.rand.nextFloat() * 0.1F + 0.6F : this.worldObj.rand.nextFloat() * 0.1F + 0.9F);
            }
            if (this.lidAngle < 0.0F)
            {
                this.lidAngle = 0.0F;
            }
        }
        super.update();
    }

    @Override
    public boolean receiveClientEvent(int id, int type)
    {
        if (id == 1)
        {
            this.numPlayersUsing = type;
            return true;
        }
        else
        {
            return super.receiveClientEvent(id, type);
        }
    }

    @Override
    public void openInventory(EntityPlayer player)
    {
        if (!player.isSpectator())
        {
            if (this.numPlayersUsing < 0)
            {
                this.numPlayersUsing = 0;
            }
            ++this.numPlayersUsing;
            this.worldObj.addBlockEvent(this.pos, this.getBlockType(), 1, this.numPlayersUsing);
            this.worldObj.notifyNeighborsOfStateChange(this.pos, this.getBlockType());
            this.worldObj.notifyNeighborsOfStateChange(this.pos.down(), this.getBlockType());
        }
    }

    @Override
    public void closeInventory(EntityPlayer player)
    {
        if (!player.isSpectator() && this.getBlockType() == this.block)
        {
            --this.numPlayersUsing;
            this.worldObj.addBlockEvent(this.pos, this.getBlockType(), 1, this.numPlayersUsing);
            this.worldObj.notifyNeighborsOfStateChange(this.pos, this.getBlockType());
            this.worldObj.notifyNeighborsOfStateChange(this.pos.down(), this.getBlockType());
        }
    }

    @Override
    public boolean isItemValidForSlot(int index, ItemStack stack)
    {
        return true;
    }

    @Override
    public void invalidate()
    {
        super.invalidate();
        this.updateContainingBlockInfo();
    }

    @Override
    public boolean canRenderBreaking()
    {
        return true;
    }

    @Override
    public String getGuiID()
    {
        return "moreplanets:treasure_chest";
    }

    @Override
    public Container createContainer(InventoryPlayer playerInventory, EntityPlayer player)
    {
        return new ContainerChest(playerInventory, this, player);
    }

    @Override
    public int getField(int id)
    {
        return 0;
    }

    @Override
    public void setField(int id, int value) {}

    @Override
    public int getFieldCount()
    {
        return 0;
    }

    @Override
    public void clear()
    {
        for (int i = 0; i < this.chestContents.length; ++i)
        {
            this.chestContents[i] = null;
        }
    }

    @Override
    public int getTierOfKeyRequired()
    {
        return this.tier;
    }

    @Override
    public boolean onValidKeyActivated(EntityPlayer player, ItemStack key, EnumFacing facing)
    {
        if (this.locked)
        {
            this.locked = false;

            if (this.worldObj.isRemote)
            {
                player.playSound("moreplanets:player.unlock_treasure_chest", 1.0F, 1.0F);
            }
            else
            {
                if (!player.capabilities.isCreativeMode && --player.inventory.getCurrentItem().stackSize == 0)
                {
                    player.inventory.setInventorySlotContents(player.inventory.currentItem, null);
                }
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean onActivatedWithoutKey(EntityPlayer player, EnumFacing facing)
    {
        if (this.locked)
        {
            if (player.worldObj.isRemote)
            {
                GalacticraftCore.packetPipeline.sendToServer(new PacketSimple(EnumSimplePacket.S_ON_FAILED_CHEST_UNLOCK, GCCoreUtil.getDimensionID(this.worldObj), new Object[] { this.getTierOfKeyRequired() }));
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean canBreak()
    {
        return false;
    }

    @Override
    public IChatComponent getDisplayName()
    {
        return new ChatComponentText(this.getName());
    }

    @Override
    public double getPacketRange()
    {
        return 20.0D;
    }

    @Override
    public int getPacketCooldown()
    {
        return 3;
    }

    @Override
    public boolean isNetworkedTile()
    {
        return true;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public AxisAlignedBB getRenderBoundingBox()
    {
        return new AxisAlignedBB(this.getPos().add(-1, 0, -1), this.getPos().add(2, 2, 2));
    }

    @Override
    public int[] getSlotsForFace(EnumFacing facing)
    {
        return new int[] {};
    }

    @Override
    public boolean canInsertItem(int index, ItemStack itemStack, EnumFacing facing)
    {
        return false;
    }

    @Override
    public boolean canExtractItem(int index, ItemStack itemStack, EnumFacing facing)
    {
        return false;
    }

    public static TileEntityTreasureChestMP findClosest(Entity entity, int tier)
    {
        double distance = Double.MAX_VALUE;
        TileEntityTreasureChestMP chest = null;

        for (TileEntity tile : entity.worldObj.loadedTileEntityList)
        {
            if (tile instanceof TileEntityTreasureChestMP && ((TileEntityTreasureChestMP) tile).getTierOfKeyRequired() == tier)
            {
                double dist = entity.getDistanceSq(tile.getPos().getX() + 0.5, tile.getPos().getY() + 0.5, tile.getPos().getZ() + 0.5);

                if (dist < distance)
                {
                    distance = dist;
                    chest = (TileEntityTreasureChestMP) tile;
                }
            }
        }
        return chest;
    }
}