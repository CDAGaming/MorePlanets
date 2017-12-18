package stevekung.mods.moreplanets.util.blocks;

import java.util.List;
import java.util.Random;

import com.google.common.collect.Lists;

import net.minecraft.block.Block;
import net.minecraft.block.IGrowable;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import stevekung.mods.moreplanets.module.planets.nibiru.blocks.NibiruBlocks;
import stevekung.mods.moreplanets.module.planets.nibiru.items.NibiruItems;
import stevekung.mods.moreplanets.util.helper.ColorHelper;

public class BlockStemMP extends BlockBushMP implements IGrowable
{
    public static PropertyInteger AGE = PropertyInteger.create("age", 0, 7);
    public static PropertyDirection FACING = PropertyDirection.create("facing", facing -> facing != EnumFacing.DOWN);
    private Block crop;

    public BlockStemMP(String name, Block crop)
    {
        this.setDefaultState(this.blockState.getBaseState().withProperty(AGE, Integer.valueOf(0)).withProperty(FACING, EnumFacing.UP));
        this.crop = crop;
        this.setTickRandomly(true);
        float f = 0.125F;
        this.setBlockBounds(0.5F - f, 0.0F, 0.5F - f, 0.5F + f, 0.25F, 0.5F + f);
        this.setUnlocalizedName(name);
        this.setStepSound(soundTypeWood);
    }

    @Override
    public IBlockState getActualState(IBlockState state, IBlockAccess world, BlockPos pos)
    {
        state = state.withProperty(FACING, EnumFacing.UP);

        for (EnumFacing enumfacing : EnumFacing.Plane.HORIZONTAL)
        {
            if (world.getBlockState(pos.offset(enumfacing)).getBlock() == this.crop)
            {
                state = state.withProperty(FACING, enumfacing);
                break;
            }
        }
        return state;
    }

    @Override
    public boolean canBlockStay(World world, BlockPos pos, IBlockState state)
    {
        return world.getBlockState(pos.down()).getBlock() == NibiruBlocks.INFECTED_FARMLAND;
    }

    @Override
    public void updateTick(World world, BlockPos pos, IBlockState state, Random rand)
    {
        super.updateTick(world, pos, state, rand);

        if (world.getLightFromNeighbors(pos.up()) >= 9)
        {
            float f = this.getGrowthChance(this, world, pos, state);

            if (rand.nextInt((int)(25.0F / f) + 1) == 0)
            {
                int i = state.getValue(AGE).intValue();

                if (i < 7)
                {
                    state = state.withProperty(AGE, Integer.valueOf(i + 1));
                    world.setBlockState(pos, state, 2);
                }
                else
                {
                    for (EnumFacing enumfacing : EnumFacing.Plane.HORIZONTAL)
                    {
                        if (world.getBlockState(pos.offset(enumfacing)).getBlock() == this.crop)
                        {
                            return;
                        }
                    }

                    pos = pos.offset(EnumFacing.Plane.HORIZONTAL.random(rand));
                    Block block = world.getBlockState(pos.down()).getBlock();

                    if (world.isAirBlock(pos) && (block == NibiruBlocks.INFECTED_GRASS || block == NibiruBlocks.INFECTED_DIRT || block == NibiruBlocks.INFECTED_FARMLAND))
                    {
                        world.setBlockState(pos, this.crop.getDefaultState());
                    }
                }
            }
        }
    }

    protected float getGrowthChance(Block block, World world, BlockPos pos, IBlockState state)
    {
        float f = 1.0F;
        BlockPos blockpos = pos.down();

        for (int i = -1; i <= 1; ++i)
        {
            for (int j = -1; j <= 1; ++j)
            {
                float f1 = 0.0F;
                IBlockState iblockstate = world.getBlockState(blockpos.add(i, 0, j));

                if (this.canBlockStay(world, blockpos.add(i, 0, j), state))
                {
                    f1 = 1.0F;

                    if (iblockstate.getBlock().isFertile(world, blockpos.add(i, 0, j)))
                    {
                        f1 = 3.0F;
                    }
                }
                if (i != 0 || j != 0)
                {
                    f1 /= 4.0F;
                }
                f += f1;
            }
        }

        BlockPos blockpos1 = pos.north();
        BlockPos blockpos2 = pos.south();
        BlockPos blockpos3 = pos.west();
        BlockPos blockpos4 = pos.east();
        boolean flag = block == world.getBlockState(blockpos3).getBlock() || block == world.getBlockState(blockpos4).getBlock();
        boolean flag1 = block == world.getBlockState(blockpos1).getBlock() || block == world.getBlockState(blockpos2).getBlock();

        if (flag && flag1)
        {
            f /= 2.0F;
        }
        else
        {
            boolean flag2 = block == world.getBlockState(blockpos3.north()).getBlock() || block == world.getBlockState(blockpos4.north()).getBlock() || block == world.getBlockState(blockpos4.south()).getBlock() || block == world.getBlockState(blockpos3.south()).getBlock();

            if (flag2)
            {
                f /= 2.0F;
            }
        }
        return f;
    }

    public void growStem(World world, BlockPos pos, IBlockState state)
    {
        int i = state.getValue(AGE).intValue() + MathHelper.getRandomIntegerInRange(world.rand, 2, 5);
        world.setBlockState(pos, state.withProperty(AGE, Integer.valueOf(Math.min(7, i))), 2);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public int getRenderColor(IBlockState state)
    {
        if (state.getBlock() != this)
        {
            return ColorHelper.rgbToDecimal(143, 55, 33);
        }
        else
        {
            return ColorHelper.rgbToDecimal(143, 55, 33);
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public int colorMultiplier(IBlockAccess world, BlockPos pos, int renderPass)
    {
        return this.getRenderColor(world.getBlockState(pos));
    }

    @Override
    public void setBlockBoundsForItemRender()
    {
        float f = 0.125F;
        this.setBlockBounds(0.5F - f, 0.0F, 0.5F - f, 0.5F + f, 0.25F, 0.5F + f);
    }

    @Override
    public void setBlockBoundsBasedOnState(IBlockAccess world, BlockPos pos)
    {
        this.maxY = (world.getBlockState(pos).getValue(AGE).intValue() * 2 + 2) / 16.0F;
        float f = 0.125F;
        this.setBlockBounds(0.5F - f, 0.0F, 0.5F - f, 0.5F + f, (float)this.maxY, 0.5F + f);
    }

    @Override
    public List<ItemStack> getDrops(IBlockAccess world, BlockPos pos, IBlockState state, int fortune)
    {
        List<ItemStack> ret = Lists.newArrayList();
        {
            Item item = this.getSeedItem();

            if (item != null)
            {
                int i = state.getValue(AGE).intValue();

                for (int j = 0; j < 3; ++j)
                {
                    if (RANDOM.nextInt(15) <= i)
                    {
                        ret.add(new ItemStack(item));
                    }
                }
            }
        }
        return ret;
    }

    protected Item getSeedItem()
    {
        return this.crop == NibiruBlocks.INFECTED_MELON_BLOCK ? NibiruItems.INFECTED_MELON_SEEDS : null;
    }

    @Override
    public Item getItemDropped(IBlockState state, Random rand, int fortune)
    {
        return null;
    }

    @Override
    public boolean canGrow(World world, BlockPos pos, IBlockState state, boolean isClient)
    {
        return state.getValue(AGE).intValue() != 7;
    }

    @Override
    public boolean canUseBonemeal(World world, Random rand, BlockPos pos, IBlockState state)
    {
        return true;
    }

    @Override
    public ItemStack getPickBlock(MovingObjectPosition target, World world, BlockPos pos, EntityPlayer player)
    {
        return new ItemStack(NibiruItems.INFECTED_MELON_SEEDS);
    }

    @Override
    public void grow(World world, Random rand, BlockPos pos, IBlockState state)
    {
        this.growStem(world, pos, state);
    }

    @Override
    public IBlockState getStateFromMeta(int meta)
    {
        return this.getDefaultState().withProperty(AGE, Integer.valueOf(meta));
    }

    @Override
    public int getMetaFromState(IBlockState state)
    {
        return state.getValue(AGE).intValue();
    }

    @Override
    protected BlockState createBlockState()
    {
        return new BlockState(this, new IProperty[] {AGE, FACING});
    }
}