package stevekung.mods.moreplanets.blocks;

import java.util.List;
import java.util.Random;

import micdoodle8.mods.galacticraft.api.block.IPartialSealableBlock;
import net.minecraft.block.BlockBeacon;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumWorldBlockLayer;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import stevekung.mods.moreplanets.util.blocks.BlockBreakableMP;
import stevekung.mods.moreplanets.util.blocks.EnumSortCategoryBlock;
import stevekung.mods.moreplanets.util.helper.BlockStateHelper;

public class BlockTintedGlass extends BlockBreakableMP implements IPartialSealableBlock
{
    public BlockTintedGlass(String name)
    {
        super(Material.glass);
        this.setDefaultState(this.blockState.getBaseState().withProperty(BlockStateHelper.COLOR, EnumDyeColor.WHITE));
        this.setHardness(0.5F);
        this.setResistance(20.0F);
        this.setStepSound(soundTypeGlass);
        this.setUnlocalizedName(name);
    }

    @Override
    public int damageDropped(IBlockState state)
    {
        return ((EnumDyeColor)state.getValue(BlockStateHelper.COLOR)).getMetadata();
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void getSubBlocks(Item item, CreativeTabs creativeTabs, List list)
    {
        for (int i = 0; i < EnumDyeColor.values().length; ++i)
        {
            list.add(new ItemStack(item, 1, i));
        }
    }

    @Override
    public MapColor getMapColor(IBlockState state)
    {
        return ((EnumDyeColor)state.getValue(BlockStateHelper.COLOR)).getMapColor();
    }

    @Override
    @SideOnly(Side.CLIENT)
    public EnumWorldBlockLayer getBlockLayer()
    {
        return EnumWorldBlockLayer.TRANSLUCENT;
    }

    @Override
    public int quantityDropped(Random rand)
    {
        return 0;
    }

    @Override
    public boolean canSilkHarvest(World world, BlockPos pos, IBlockState state, EntityPlayer player)
    {
        return true;
    }

    @Override
    public boolean isFullCube()
    {
        return false;
    }

    @Override
    public void onBlockAdded(World world, BlockPos pos, IBlockState state)
    {
        if (!world.isRemote)
        {
            BlockBeacon.updateColorAsync(world, pos);
        }
    }

    @Override
    public void breakBlock(World world, BlockPos pos, IBlockState state)
    {
        if (!world.isRemote)
        {
            BlockBeacon.updateColorAsync(world, pos);
        }
    }

    @Override
    public IBlockState getStateFromMeta(int meta)
    {
        return this.getDefaultState().withProperty(BlockStateHelper.COLOR, EnumDyeColor.byMetadata(meta));
    }

    @Override
    public int getMetaFromState(IBlockState state)
    {
        return ((EnumDyeColor)state.getValue(BlockStateHelper.COLOR)).getMetadata();
    }

    @Override
    protected BlockState createBlockState()
    {
        return new BlockState(this, new IProperty[] {BlockStateHelper.COLOR});
    }

    @Override
    public boolean isSealed(World world, BlockPos pos, EnumFacing facing)
    {
        return true;
    }

    @Override
    public EnumSortCategoryBlock getBlockCategory(int meta)
    {
        return EnumSortCategoryBlock.BUILDING_BLOCK;
    }

    @Override
    protected boolean isTranslucentBlock()
    {
        return true;
    }

    @Override
    protected boolean renderSideOnOtherState()
    {
        return true;
    }
}