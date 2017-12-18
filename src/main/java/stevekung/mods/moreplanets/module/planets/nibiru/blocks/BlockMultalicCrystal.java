package stevekung.mods.moreplanets.module.planets.nibiru.blocks;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import stevekung.mods.moreplanets.module.planets.nibiru.items.NibiruItems;
import stevekung.mods.moreplanets.module.planets.nibiru.tileentity.TileEntityMultalicCrystal;
import stevekung.mods.moreplanets.util.blocks.BlockContainerMP;
import stevekung.mods.moreplanets.util.blocks.EnumSortCategoryBlock;
import stevekung.mods.moreplanets.util.blocks.ISingleBlockRender;
import stevekung.mods.moreplanets.util.helper.ColorHelper;

public class BlockMultalicCrystal extends BlockContainerMP implements ISingleBlockRender
{
    public BlockMultalicCrystal(String name)
    {
        super(Material.rock);
        this.setLightLevel(0.2F);
        this.setResistance(1.0F);
        this.setHardness(0.4F);
        this.setStepSound(soundTypeGlass);
        this.setUnlocalizedName(name);
        this.setLightOpacity(255);
    }

    @Override
    public void setBlockBoundsBasedOnState(IBlockAccess world, BlockPos pos)
    {
        TileEntityMultalicCrystal crystal = (TileEntityMultalicCrystal)world.getTileEntity(pos);

        if (crystal != null)
        {
            int facing = crystal.facing;
            float f = 0.0625F;

            switch (facing)
            {
            case 0:
                this.setBlockBounds(0.0F + f, 0.0F + f, 0.0F + f, 1.0F - f, 1.0F, 1.0F - f);
                break;
            case 1:
                this.setBlockBounds(0.0F + f, 0.0F, 0.0F + f, 1.0F - f, 1.0F - f, 1.0F - f);
                break;
            case 2:
                this.setBlockBounds(0.0F + f, 0.0F + f, 0.0F + f, 1.0F - f, 1.0F - f, 1.0F);
                break;
            case 3:
                this.setBlockBounds(0.0F + f, 0.0F + f, 0.0F, 1.0F - f, 1.0F - f, 1.0F - f);
                break;
            case 4:
                this.setBlockBounds(0.0F + f, 0.0F + f, 0.0F + f, 1.0F, 1.0F - f, 1.0F - f);
                break;
            case 5:
                this.setBlockBounds(0.0F, 0.0F + f, 0.0F + f, 1.0F - f, 1.0F - f, 1.0F - f);
                break;
            }
        }
        else
        {
            this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
        }
    }

    @Override
    public boolean canSilkHarvest(World world, BlockPos pos, IBlockState state, EntityPlayer player)
    {
        return true;
    }

    @Override
    public void dropBlockAsItemWithChance(World world, BlockPos pos, IBlockState state, float chance, int fortune)
    {
        super.dropBlockAsItemWithChance(world, pos, state, chance, fortune);

        if (this.getItemDropped(state, world.rand, fortune) != Item.getItemFromBlock(this))
        {
            this.dropXpOnBlockBreak(world, pos, MathHelper.getRandomIntegerInRange(world.rand, 3, 5));
        }
    }

    @Override
    public ItemStack getPickBlock(MovingObjectPosition target, World world, BlockPos pos, EntityPlayer player)
    {
        return new ItemStack(this, 1, 0);
    }

    @Override
    public int quantityDropped(IBlockState state, int fortune, Random rand)
    {
        if (fortune > 0 && Item.getItemFromBlock(this) != this.getItemDropped(state, rand, fortune))
        {
            int i = rand.nextInt(fortune + 1) - 1;

            if (i < 0)
            {
                i = 0;
            }
            return this.quantityDropped(rand) * (i + 1);
        }
        return this.quantityDropped(rand);
    }

    @Override
    public int quantityDropped(Random rand)
    {
        return 1 + rand.nextInt(2);
    }

    @Override
    public Item getItemDropped(IBlockState state, Random rand, int fortune)
    {
        return NibiruItems.NIBIRU_ITEM;
    }

    @Override
    public int damageDropped(IBlockState state)
    {
        return 1;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public int colorMultiplier(IBlockAccess world, BlockPos pos, int render)
    {
        return ColorHelper.rgbToDecimal(50, 101, 236);
    }

    @Override
    public boolean isOpaqueCube()
    {
        return false;
    }

    @Override
    public boolean isFullCube()
    {
        return false;
    }

    @Override
    public TileEntity createNewTileEntity(World world, int meta)
    {
        return new TileEntityMultalicCrystal();
    }

    @Override
    public void onNeighborBlockChange(World world, BlockPos pos, IBlockState state, Block block)
    {
        if (this.checkIfAttachedToBlock(world, pos))
        {
            TileEntityMultalicCrystal crystal = (TileEntityMultalicCrystal)world.getTileEntity(pos);
            int facing = crystal.facing;
            boolean flag = false;

            if (!world.isSideSolid(pos.west(), EnumFacing.EAST) && facing == 5)
            {
                flag = true;
            }
            if (!world.isSideSolid(pos.east(), EnumFacing.WEST) && facing == 4)
            {
                flag = true;
            }
            if (!world.isSideSolid(pos.north(), EnumFacing.SOUTH) && facing == 3)
            {
                flag = true;
            }
            if (!world.isSideSolid(pos.south(), EnumFacing.NORTH) && facing == 2)
            {
                flag = true;
            }
            if (!world.isSideSolid(pos.down(), EnumFacing.UP) && facing == 1)
            {
                flag = true;
            }
            if (!world.isSideSolid(pos.up(), EnumFacing.DOWN) && facing == 0)
            {
                flag = true;
            }
            if (flag)
            {
                world.destroyBlock(pos, false);
            }
            return;
        }
    }

    private boolean checkIfAttachedToBlock(World world, BlockPos pos)
    {
        if (!this.canPlaceBlockAt(world, pos))
        {
            world.destroyBlock(pos, false);
            return false;
        }
        return true;
    }

    @Override
    public boolean canPlaceBlockOnSide(World world, BlockPos pos, EnumFacing side)
    {
        if (side == EnumFacing.DOWN && world.isSideSolid(pos.up(), EnumFacing.DOWN))
        {
            return true;
        }
        if (side == EnumFacing.UP && world.isSideSolid(pos.down(), EnumFacing.UP))
        {
            return true;
        }
        if (side == EnumFacing.NORTH && world.isSideSolid(pos.south(), EnumFacing.NORTH))
        {
            return true;
        }
        if (side == EnumFacing.SOUTH && world.isSideSolid(pos.north(), EnumFacing.SOUTH))
        {
            return true;
        }
        if (side == EnumFacing.WEST && world.isSideSolid(pos.east(), EnumFacing.WEST))
        {
            return true;
        }
        return side == EnumFacing.EAST && world.isSideSolid(pos.west(), EnumFacing.EAST);
    }

    @Override
    public boolean canPlaceBlockAt(World world, BlockPos pos)
    {
        if (world.isSideSolid(pos.west(), EnumFacing.EAST))
        {
            return true;
        }
        if (world.isSideSolid(pos.east(), EnumFacing.WEST))
        {
            return true;
        }
        if (world.isSideSolid(pos.north(), EnumFacing.SOUTH))
        {
            return true;
        }
        if (world.isSideSolid(pos.south(), EnumFacing.NORTH))
        {
            return true;
        }
        if (world.isSideSolid(pos.down(), EnumFacing.UP))
        {
            return true;
        }
        return world.isSideSolid(pos.up(), EnumFacing.DOWN);
    }

    @Override
    public EnumSortCategoryBlock getBlockCategory(int meta)
    {
        return EnumSortCategoryBlock.DECORATION_NON_BLOCK;
    }

    @Override
    public String getName()
    {
        return "multalic_crystal";
    }

    @Override
    public Block getBlock()
    {
        return this;
    }
}