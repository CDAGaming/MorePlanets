package stevekung.mods.moreplanets.util.tileentity;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;

public class TileEntityRenderTickable extends TileEntity implements ITickable
{
    public int renderTicks;

    @Override
    public void update()
    {
        this.renderTicks++;
    }

    @Override
    public NBTTagCompound getUpdateTag()
    {
        return this.writeToNBT(new NBTTagCompound());
    }
}