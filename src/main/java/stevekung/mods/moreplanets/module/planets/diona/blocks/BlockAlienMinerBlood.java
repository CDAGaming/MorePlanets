package stevekung.mods.moreplanets.module.planets.diona.blocks;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import stevekung.mods.moreplanets.util.blocks.BlockBaseMP;
import stevekung.mods.moreplanets.util.blocks.EnumSortCategoryBlock;

public class BlockAlienMinerBlood extends BlockBaseMP
{
    public BlockAlienMinerBlood(String name)
    {
        super(Material.rock);
        this.setTickRandomly(true);
        this.setHardness(1.25F);
        this.setUnlocalizedName(name);
    }

    @Override
    public EnumSortCategoryBlock getBlockCategory(int meta)
    {
        return EnumSortCategoryBlock.DECORATION_BLOCK;
    }

    @Override
    public void updateTick(World world, BlockPos pos, IBlockState state, Random rand)
    {
        if (rand.nextInt(200) == 0)
        {
            world.setBlockState(pos, DionaBlocks.DIONA_BLOCK.getDefaultState());
        }
    }

    @Override
    public Item getItemDropped(IBlockState state, Random rand, int fortune)
    {
        return Item.getItemFromBlock(DionaBlocks.DIONA_BLOCK);
    }

    @Override
    public boolean canSilkHarvest(World world, BlockPos pos, IBlockState state, EntityPlayer player)
    {
        return false;
    }

    @Override
    public String getName()
    {
        return "alien_miner_blood";
    }

    @Override
    public Block getBlock()
    {
        return this;
    }
}