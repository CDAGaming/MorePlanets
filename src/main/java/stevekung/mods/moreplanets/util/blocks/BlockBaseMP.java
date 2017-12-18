package stevekung.mods.moreplanets.util.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import stevekung.mods.moreplanets.core.MorePlanetsCore;

public class BlockBaseMP extends Block implements ISortableBlock, ISingleBlockRender
{
    private EnumSortCategoryBlock category;
    protected String name;

    public BlockBaseMP(Material material)
    {
        super(material);
    }

    public BlockBaseMP(String name, Material material)
    {
        super(material);
        this.name = name;
        this.setUnlocalizedName(name);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public CreativeTabs getCreativeTabToDisplayOn()
    {
        return MorePlanetsCore.BLOCK_TAB;
    }

    @Override
    public EnumSortCategoryBlock getBlockCategory(int meta)
    {
        return this.category == null ? EnumSortCategoryBlock.BUILDING_BLOCK : this.category;
    }

    public BlockBaseMP setSortCategory(EnumSortCategoryBlock category)
    {
        this.category = category;
        return this;
    }

    @Override
    public String getName()
    {
        return this.name;
    }

    @Override
    public Block getBlock()
    {
        return this;
    }
}