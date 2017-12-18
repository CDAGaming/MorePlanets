package stevekung.mods.moreplanets.blocks;

import java.util.List;
import java.util.Random;

import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import stevekung.mods.moreplanets.module.planets.chalos.tileentity.TileEntityChalosDungeonSpawner;
import stevekung.mods.moreplanets.module.planets.diona.tileentity.TileEntityDionaDungeonSpawner;
import stevekung.mods.moreplanets.util.blocks.BlockBaseMP;

public class BlockSpaceDungeonSpawner extends BlockBaseMP implements ITileEntityProvider
{
    public static PropertyEnum PLANET = PropertyEnum.create("planet", DungeonType.class);

    public BlockSpaceDungeonSpawner(String name)
    {
        super(Material.air);
        this.setBlockUnbreakable();
        this.setResistance(6000001.0F);
        this.translucent = true;
        this.setDefaultState(this.getDefaultState().withProperty(PLANET, DungeonType.DIONA));
        this.setUnlocalizedName(name);
    }

    @Override
    public AxisAlignedBB getCollisionBoundingBox(World world, BlockPos pos, IBlockState state)
    {
        return null;
    }

    @Override
    public MovingObjectPosition collisionRayTrace(World world, BlockPos pos, Vec3 start, Vec3 end)
    {
        return null;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void getSubBlocks(Item item, CreativeTabs creativeTabs, List list)
    {
        for (int i = 0; i < DungeonType.valuesCached().length; ++i)
        {
            list.add(new ItemStack(item, 1, i));
        }
    }

    @Override
    public int getRenderType()
    {
        return -1;
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
    @SideOnly(Side.CLIENT)
    public float getAmbientOcclusionLightValue()
    {
        return 1.0F;
    }

    @Override
    public ItemStack getPickBlock(MovingObjectPosition moving, World world, BlockPos pos, EntityPlayer player)
    {
        return null;
    }

    @Override
    public boolean hasTileEntity(IBlockState state)
    {
        return true;
    }

    @Override
    public TileEntity createNewTileEntity(World world, int meta)
    {
        switch (meta)
        {
        case 0:
            return new TileEntityDionaDungeonSpawner();
        case 1:
            return new TileEntityChalosDungeonSpawner();
        default:
            return null;
        }
    }

    @Override
    public boolean canHarvestBlock(IBlockAccess world, BlockPos pos, EntityPlayer player)
    {
        return false;
    }

    @Override
    public int quantityDropped(IBlockState state, int fortune, Random rand)
    {
        return 0;
    }

    @Override
    protected BlockState createBlockState()
    {
        return new BlockState(this, new IProperty[] { PLANET });
    }

    @Override
    public IBlockState getStateFromMeta(int meta)
    {
        return this.getDefaultState().withProperty(PLANET, DungeonType.valuesCached()[meta]);
    }

    @Override
    public int getMetaFromState(IBlockState state)
    {
        return ((DungeonType)state.getValue(PLANET)).ordinal();
    }

    public static enum DungeonType implements IStringSerializable
    {
        DIONA,
        CHALOS,
        NIBIRU;

        private static DungeonType[] values = DungeonType.values();

        public static DungeonType[] valuesCached()
        {
            return DungeonType.values;
        }

        @Override
        public String toString()
        {
            return this.getName().toLowerCase();
        }

        @Override
        public String getName()
        {
            return this.name().toLowerCase();
        }
    }
}