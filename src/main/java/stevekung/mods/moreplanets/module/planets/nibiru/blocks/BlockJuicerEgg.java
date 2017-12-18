package stevekung.mods.moreplanets.module.planets.nibiru.blocks;

import java.util.List;
import java.util.Random;

import micdoodle8.mods.galacticraft.planets.venus.entities.EntityJuicer;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import stevekung.mods.moreplanets.module.planets.nibiru.tileentity.TileEntityJuicerEgg;
import stevekung.mods.moreplanets.util.blocks.BlockBaseMP;
import stevekung.mods.moreplanets.util.blocks.EnumSortCategoryBlock;
import stevekung.mods.moreplanets.util.helper.BlockSoundHelper;

public class BlockJuicerEgg extends BlockBaseMP implements ITileEntityProvider
{
    public BlockJuicerEgg(String name)
    {
        super(Material.ground);
        this.setResistance(0.0F);
        this.setHardness(0.5F);
        this.setUnlocalizedName(name);
        this.setStepSound(BlockSoundHelper.ALIEN_EGG);
        this.slipperiness = 0.8F;
    }

    @Override
    public int getRenderType()
    {
        return 3;
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
    public boolean canSilkHarvest(World world, BlockPos pos, IBlockState state, EntityPlayer player)
    {
        return true;
    }

    @Override
    public void onBlockDestroyedByPlayer(World world, BlockPos pos, IBlockState state)
    {
        if (!world.isRemote)
        {
            if (world.rand.nextInt(5) == 0)
            {
                EntityJuicer juicer = new EntityJuicer(world);
                juicer.setLocationAndAngles(pos.getX() + 0.5D, pos.getY() + 1.0D, pos.getZ() + 0.5D, 0.0F, 0.0F);
                world.spawnEntityInWorld(juicer);
            }
            if (world.rand.nextInt(10) == 0)
            {
                double radiusPlayer = 5.0D;
                List<EntityPlayer> playerList = world.getEntitiesWithinAABB(EntityPlayer.class, new AxisAlignedBB(pos.getX() - radiusPlayer, pos.getY() - radiusPlayer, pos.getZ() - radiusPlayer, pos.getX() + radiusPlayer, pos.getY() + radiusPlayer, pos.getZ() + radiusPlayer));

                if (!playerList.isEmpty())
                {
                    for (EntityPlayer player : playerList)
                    {
                        EntityJuicer juicer = new EntityJuicer(world);
                        juicer.setLocationAndAngles(pos.getX() + 0.5D, pos.getY() + 1.0D, pos.getZ() + 0.5D, 0.0F, 0.0F);
                        world.spawnEntityInWorld(juicer);
                        juicer.mountEntity(player);
                    }
                }
            }
        }
    }

    @Override
    public void onFallenUpon(World world, BlockPos pos, Entity entity, float fallDistance)
    {
        if (entity.isSneaking())
        {
            super.onFallenUpon(world, pos, entity, fallDistance);
        }
        else
        {
            entity.fall(fallDistance, 0.0F);
        }
    }

    @Override
    public void onLanded(World world, Entity entity)
    {
        if (entity.isSneaking())
        {
            super.onLanded(world, entity);
        }
        else if (entity.motionY < 0.0D)
        {
            entity.motionY = -entity.motionY;
        }
    }

    @Override
    public void onEntityCollidedWithBlock(World world, BlockPos pos, Entity entity)
    {
        if (Math.abs(entity.motionY) < 0.1D && !entity.isSneaking())
        {
            double d0 = 0.4D + Math.abs(entity.motionY) * 0.2D;
            entity.motionX *= d0;
            entity.motionZ *= d0;
        }
    }

    @Override
    public int quantityDropped(Random rand)
    {
        return 0;
    }

    @Override
    public TileEntity createNewTileEntity(World world, int meta)
    {
        return new TileEntityJuicerEgg();
    }

    @Override
    public EnumSortCategoryBlock getBlockCategory(int meta)
    {
        return EnumSortCategoryBlock.DECORATION_NON_BLOCK;
    }

    @Override
    public String getName()
    {
        return "juicer_egg";
    }
}