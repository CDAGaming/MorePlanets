package stevekung.mods.moreplanets.module.planets.nibiru.blocks;

import java.util.Random;

import net.minecraft.block.SoundType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import stevekung.mods.moreplanets.util.blocks.BlockFallingMP;

public class BlockInfectedGravel extends BlockFallingMP
{
    public BlockInfectedGravel(String name)
    {
        super();
        this.setUnlocalizedName(name);
        this.setHardness(0.6F);
        this.setSoundType(SoundType.GROUND);
    }

    @Override
    public Item getItemDropped(IBlockState state, Random rand, int fortune)
    {
        if (fortune > 3)
        {
            fortune = 3;
        }
        return rand.nextInt(10 - fortune * 3) == 0 ? Items.FLINT : Item.getItemFromBlock(this);
    }

    @Override
    public String getName()
    {
        return "infected_gravel";
    }
}