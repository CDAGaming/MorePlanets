package stevekung.mods.moreplanets.util.items;

import java.util.List;

import micdoodle8.mods.galacticraft.api.entity.IRocketType.EnumRocketType;
import micdoodle8.mods.galacticraft.api.item.IHoldableItem;
import micdoodle8.mods.galacticraft.core.GCBlocks;
import micdoodle8.mods.galacticraft.core.tile.TileEntityLandingPad;
import micdoodle8.mods.galacticraft.core.util.EnumColor;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public abstract class ItemRocketBaseMP extends ItemBaseMP implements IHoldableItem, ISortableItem
{
    public ItemRocketBaseMP()
    {
        super();
        this.setHasSubtypes(true);
        this.setMaxStackSize(1);
    }

    @Override
    public boolean onItemUse(ItemStack itemStack, EntityPlayer player, World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ)
    {
        boolean padFound = false;
        TileEntity tile = null;

        if (world.isRemote)
        {
            return false;
        }
        else
        {
            float centerX = -1;
            float centerY = -1;
            float centerZ = -1;

            for (int i = -1; i < 2; i++)
            {
                for (int j = -1; j < 2; j++)
                {
                    BlockPos pos1 = pos.add(i, 0, j);
                    IBlockState state = world.getBlockState(pos1);
                    Block block = state.getBlock();
                    int meta = block.getMetaFromState(state);

                    if (block == GCBlocks.landingPadFull && meta == 0)
                    {
                        padFound = true;
                        tile = world.getTileEntity(pos1);
                        centerX = pos.getX() + i + 0.5F;
                        centerY = pos.getY() + 0.4F;
                        centerZ = pos.getZ() + j + 0.5F;
                        break;
                    }
                }
                if (padFound)
                {
                    break;
                }
            }

            if (padFound)
            {
                if (tile instanceof TileEntityLandingPad)
                {
                    if (((TileEntityLandingPad)tile).getDockedEntity() != null)
                    {
                        return false;
                    }
                }
                else
                {
                    return false;
                }
                this.spawnRocket(itemStack, world, player, centerX, centerY, centerZ);
            }
            else
            {
                return false;
            }
        }
        return true;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void getSubItems(Item item, CreativeTabs creativeTabs, List list)
    {
        for (int i = 0; i < EnumRocketType.values().length; i++)
        {
            list.add(new ItemStack(item, 1, i));
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack itemStack, EntityPlayer player, List list, boolean advanced)
    {
        EnumRocketType type;

        if (itemStack.getItemDamage() < 10)
        {
            type = EnumRocketType.values()[itemStack.getItemDamage()];
        }
        else
        {
            type = EnumRocketType.values()[itemStack.getItemDamage() - 10];
        }

        if (!type.getTooltip().isEmpty())
        {
            list.add(type.getTooltip());
        }
        if (type.getPreFueled())
        {
            list.add(EnumColor.RED + "\u00a7o" + StatCollector.translateToLocal("gui.creative_only.desc"));
        }
        if (itemStack.hasTagCompound() && itemStack.getTagCompound().hasKey("RocketFuel"))
        {
            this.addDescription(itemStack, list);
        }
    }

    @Override
    public boolean shouldHoldLeftHandUp(EntityPlayer player)
    {
        return true;
    }

    @Override
    public boolean shouldHoldRightHandUp(EntityPlayer player)
    {
        return true;
    }

    @Override
    public boolean shouldCrouch(EntityPlayer player)
    {
        return true;
    }

    @Override
    public EnumSortCategoryItem getItemCategory(int meta)
    {
        return EnumSortCategoryItem.ROCKET;
    }

    protected abstract void spawnRocket(ItemStack itemStack, World world, EntityPlayer player, float centerX, float centerY, float centerZ);

    @SideOnly(Side.CLIENT)
    protected abstract void addDescription(ItemStack itemStack, List list);
}