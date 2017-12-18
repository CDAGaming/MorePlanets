package stevekung.mods.moreplanets.module.planets.nibiru.blocks;

import java.util.List;

import com.google.common.collect.Lists;

import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.IStringSerializable;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.fluids.BlockFluidBase;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import stevekung.mods.moreplanets.util.VariantsName;
import stevekung.mods.moreplanets.util.blocks.BlockBushMP;
import stevekung.mods.moreplanets.util.blocks.IBlockVariants;

public class BlockNibiruSeaweed extends BlockBushMP implements IBlockVariants
{
    public static PropertyEnum VARIANT = PropertyEnum.create("variant", BlockType.class);

    public BlockNibiruSeaweed(String name)
    {
        super(Material.water);
        this.setUnlocalizedName(name);
        this.setDefaultState(this.getDefaultState().withProperty(VARIANT, BlockType.NIBIRU_SEAWEED));
    }

    @Override
    public void setBlockBoundsBasedOnState(IBlockAccess world, BlockPos pos)
    {
        this.setBlockBounds(0.2F, 0.0F, 0.2F, 0.8F, 0.8F, 0.8F);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void getSubBlocks(Item item, CreativeTabs creativeTabs, List list)
    {
        for (int i = 0; i < BlockType.valuesCached().length; ++i)
        {
            list.add(new ItemStack(item, 1, i));
        }
    }

    @Override
    public void harvestBlock(World world, EntityPlayer player, BlockPos pos, IBlockState state, TileEntity te)
    {
        player.addExhaustion(0.025F);

        if (this.canSilkHarvest(world, pos, world.getBlockState(pos), player) && EnchantmentHelper.getSilkTouchModifier(player))
        {
            List<ItemStack> items = Lists.newArrayList();
            ItemStack itemstack = this.createStackedBlock(state);

            if (itemstack != null)
            {
                items.add(itemstack);
            }

            ForgeEventFactory.fireBlockHarvesting(items, world, pos, world.getBlockState(pos), 0, 1.0f, true, player);

            for (ItemStack is : items)
            {
                Block.spawnAsEntity(world, pos, is);
            }
        }
        else
        {
            if (world.provider.doesWaterVaporize())
            {
                world.setBlockToAir(pos);
                return;
            }

            int i = EnchantmentHelper.getFortuneModifier(player);
            this.harvesters.set(player);
            this.dropBlockAsItem(world, pos, state, i);
            this.harvesters.set(null);
            Material material = world.getBlockState(pos.down()).getBlock().getMaterial();

            if (material.blocksMovement() || material.isLiquid())
            {
                world.setBlockState(pos, NibiruBlocks.INFECTED_WATER_FLUID_BLOCK.getDefaultState());
            }
        }
    }

    @Override
    public boolean canBlockStay(World world, BlockPos pos, IBlockState state)
    {
        IBlockState blockDown = world.getBlockState(pos.down());
        IBlockState blockUp = world.getBlockState(pos.up());
        Block block = blockDown.getBlock();
        return (block == NibiruBlocks.INFECTED_GRAVEL || block == NibiruBlocks.INFECTED_DIRT || block == NibiruBlocks.INFECTED_SAND || block == NibiruBlocks.INFECTED_CLAY) && blockUp.getBlock() == NibiruBlocks.INFECTED_WATER_FLUID_BLOCK && blockUp.getValue(BlockFluidBase.LEVEL) == 0;
    }

    @Override
    public boolean isReplaceable(World world, BlockPos pos)
    {
        return false;
    }

    @Override
    public int getDamageValue(World world, BlockPos pos)
    {
        return this.getMetaFromState(world.getBlockState(pos));
    }

    @Override
    public int damageDropped(IBlockState state)
    {
        return this.getMetaFromState(state);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public EnumOffsetType getOffsetType()
    {
        return EnumOffsetType.XYZ;
    }

    @Override
    public VariantsName getVariantsName()
    {
        return new VariantsName("seaweed");
    }

    @Override
    protected BlockState createBlockState()
    {
        return new BlockState(this, new IProperty[] { VARIANT, BlockLiquid.LEVEL });
    }

    @Override
    public IBlockState getStateFromMeta(int meta)
    {
        return this.getDefaultState().withProperty(VARIANT, BlockType.valuesCached()[meta]).withProperty(BlockLiquid.LEVEL, 15);
    }

    @Override
    public int getMetaFromState(IBlockState state)
    {
        return ((BlockType)state.getValue(VARIANT)).ordinal();
    }

    public static enum BlockType implements IStringSerializable
    {
        NIBIRU_SEAWEED;

        private static BlockType[] values = BlockType.values();

        public static BlockType[] valuesCached()
        {
            return BlockType.values;
        }

        @Override
        public String toString()
        {
            return this.name().toLowerCase();
        }

        @Override
        public String getName()
        {
            return this.name().toLowerCase();
        }
    }
}