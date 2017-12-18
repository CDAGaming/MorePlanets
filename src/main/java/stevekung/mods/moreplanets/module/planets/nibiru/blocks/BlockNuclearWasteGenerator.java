package stevekung.mods.moreplanets.module.planets.nibiru.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import stevekung.mods.moreplanets.core.MorePlanetsCore;
import stevekung.mods.moreplanets.module.planets.nibiru.tileentity.TileEntityNuclearWasteGenerator;
import stevekung.mods.moreplanets.util.ItemDescription;
import stevekung.mods.moreplanets.util.blocks.BlockTileMP;
import stevekung.mods.moreplanets.util.blocks.EnumSortCategoryBlock;
import stevekung.mods.moreplanets.util.blocks.IBlockDescription;
import stevekung.mods.moreplanets.util.blocks.ISingleBlockRender;
import stevekung.mods.moreplanets.util.helper.ItemDescriptionHelper;

public class BlockNuclearWasteGenerator extends BlockTileMP implements IBlockDescription, ISingleBlockRender
{
    public BlockNuclearWasteGenerator(String name)
    {
        super(Material.iron);
        this.setHardness(5.0F);
        this.setUnlocalizedName(name);
        this.setStepSound(Block.soundTypeMetal);
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumFacing side, float hitX, float hitY, float hitZ)
    {
        if (!world.isRemote)
        {
            player.openGui(MorePlanetsCore.INSTANCE, -1, world, pos.getX(), pos.getY(), pos.getZ());
            return true;
        }
        return true;
    }

    @Override
    public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack itemStack)
    {
        world.setBlockState(pos, this.getDefaultState(), 3);

        if (itemStack.hasTagCompound() && itemStack.getTagCompound().hasKey("EnergyStored"))
        {
            if (world.getTileEntity(pos) instanceof TileEntityNuclearWasteGenerator)
            {
                TileEntityNuclearWasteGenerator electric = (TileEntityNuclearWasteGenerator) world.getTileEntity(pos);
                electric.storage.setEnergyStored(itemStack.getTagCompound().getFloat("EnergyStored"));
            }
        }
    }

    @Override
    public void harvestBlock(World world, EntityPlayer player, BlockPos pos, IBlockState state, TileEntity tile)
    {
        if (tile instanceof TileEntityNuclearWasteGenerator)
        {
            ItemStack machine = new ItemStack(this);
            TileEntityNuclearWasteGenerator electric = (TileEntityNuclearWasteGenerator) tile;

            if (electric.getEnergyStoredGC() > 0)
            {
                machine.setTagCompound(new NBTTagCompound());
                machine.getTagCompound().setFloat("EnergyStored", electric.getEnergyStoredGC());
            }
            Block.spawnAsEntity(world, pos, machine);
        }
    }

    @Override
    public TileEntity createNewTileEntity(World world, int meta)
    {
        return new TileEntityNuclearWasteGenerator();
    }

    @Override
    public EnumSortCategoryBlock getBlockCategory(int meta)
    {
        return EnumSortCategoryBlock.MACHINE_BLOCK;
    }

    @Override
    public ItemDescription getDescription()
    {
        return (itemStack, list) -> list.addAll(ItemDescriptionHelper.getDescription(BlockNuclearWasteGenerator.this.getUnlocalizedName() + ".description"));
    }

    @Override
    public String getName()
    {
        return "nuclear_waste_generator";
    }

    @Override
    public Block getBlock()
    {
        return this;
    }
}