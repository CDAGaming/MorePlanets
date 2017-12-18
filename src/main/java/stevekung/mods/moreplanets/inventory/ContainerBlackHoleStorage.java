package stevekung.mods.moreplanets.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class ContainerBlackHoleStorage extends Container
{
    private EntityPlayer player;
    private IInventory inventory;

    public ContainerBlackHoleStorage(IInventory playerInventory, IInventory chestInventory)
    {
        this.inventory = chestInventory;
        this.player = ((InventoryPlayer) playerInventory).player;
        chestInventory.openInventory(this.player);
        this.addSlotForContainer(playerInventory, chestInventory, 256, 256);
    }

    @Override
    public boolean canInteractWith(EntityPlayer player)
    {
        return this.inventory.isUseableByPlayer(player);
    }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer player, int index)
    {
        ItemStack itemstack = null;
        Slot slot = this.inventorySlots.get(index);

        if (slot != null && slot.getHasStack())
        {
            ItemStack itemstack1 = slot.getStack();
            itemstack = itemstack1.copy();

            if (index < 108)
            {
                if (!this.mergeItemStack(itemstack1, 108, this.inventorySlots.size(), true))
                {
                    return null;
                }
            }
            else if (!this.mergeItemStack(itemstack1, 0, 108, false))
            {
                return null;
            }
            if (itemstack1.stackSize == 0)
            {
                slot.putStack(null);
            }
            else
            {
                slot.onSlotChanged();
            }
        }
        return itemstack;
    }

    @Override
    public void onContainerClosed(EntityPlayer player)
    {
        super.onContainerClosed(player);
        this.inventory.closeInventory(player);
    }

    protected void addSlotForContainer(IInventory playerInventory, IInventory chestInventory, int xSize, int ySize)
    {
        int rowCount = 108 / 12;
        int leftCol = (xSize - 243) / 2 + 1;

        for (int chestRow = 0; chestRow < rowCount; chestRow++)
        {
            for (int chestCol = 0; chestCol < 12; chestCol++)
            {
                this.addSlotToContainer(new SlotBlackHoleStorage(chestInventory, chestCol + chestRow * 12, 21 + chestCol * 18, 8 + chestRow * 18));
            }
        }
        for (int playerInvRow = 0; playerInvRow < 3; playerInvRow++)
        {
            for (int playerInvCol = 0; playerInvCol < 9; playerInvCol++)
            {
                this.addSlotToContainer(new Slot(playerInventory, playerInvCol + playerInvRow * 9 + 9, leftCol + playerInvCol * 18, ySize - (4 - playerInvRow) * 18 - 10));
            }
        }
        for (int hotbarSlot = 0; hotbarSlot < 9; hotbarSlot++)
        {
            this.addSlotToContainer(new Slot(playerInventory, hotbarSlot, leftCol + hotbarSlot * 18, ySize - 24));
        }
    }
}