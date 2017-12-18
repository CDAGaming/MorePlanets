package stevekung.mods.moreplanets.util.helper;

import java.util.Comparator;
import java.util.List;
import java.util.Map;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Ordering;

import micdoodle8.mods.galacticraft.core.util.StackSorted;
import net.minecraft.block.Block;
import net.minecraft.block.BlockDispenser;
import net.minecraft.dispenser.IBehaviorDispenseItem;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving.SpawnPlacementType;
import net.minecraft.entity.EntitySpawnPlacementRegistry;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidContainerRegistry.FluidContainerData;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.IFuelHandler;
import net.minecraftforge.fml.common.network.IGuiHandler;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.oredict.OreDictionary;
import stevekung.mods.moreplanets.core.MorePlanetsCore;
import stevekung.mods.moreplanets.util.EnumHarvestLevel;
import stevekung.mods.moreplanets.util.blocks.EnumSortCategoryBlock;
import stevekung.mods.moreplanets.util.blocks.ISingleBlockRender;
import stevekung.mods.moreplanets.util.blocks.ISortableBlock;
import stevekung.mods.moreplanets.util.entity.EnumEntityTrackerType;
import stevekung.mods.moreplanets.util.items.EnumSortCategoryItem;
import stevekung.mods.moreplanets.util.items.ISingleItemRender;
import stevekung.mods.moreplanets.util.items.ISortableItem;

public class CommonRegisterHelper
{
    private static int ID = 0;
    public static Map<EnumSortCategoryBlock, List<StackSorted>> SORT_MAP_BLOCKS = Maps.newHashMap();
    public static Map<EnumSortCategoryItem, List<StackSorted>> SORT_MAP_ITEMS = Maps.newHashMap();
    public static Map<Block, String> SINGLE_BLOCK_RENDER_LIST = Maps.newHashMap();
    public static Map<Item, String> SINGLE_ITEM_RENDER_LIST = Maps.newHashMap();

    public static void registerBlock(Block block)
    {
        CommonRegisterHelper.registerBlock(block, ItemBlock.class);
    }

    public static void registerBlock(Block block, Class<? extends ItemBlock> itemBlock)
    {
        GameRegistry.registerBlock(block, itemBlock, block.getUnlocalizedName().substring(5));

        if (block instanceof ISingleBlockRender)
        {
            ISingleBlockRender blockRender = (ISingleBlockRender) block;

            if (blockRender.getBlock() != null && blockRender.getName() != null)
            {
                CommonRegisterHelper.SINGLE_BLOCK_RENDER_LIST.put(blockRender.getBlock(), blockRender.getName());
            }
        }
        if (CommonRegisterHelper.isEffectiveClient() && itemBlock != null)
        {
            CommonRegisterHelper.registerSorted(block);
        }
    }

    public static void registerFluid(Fluid fluid)
    {
        FluidRegistry.registerFluid(fluid);
    }

    public static void registerItem(Item item)
    {
        GameRegistry.registerItem(item, item.getUnlocalizedName().substring(5));

        if (item instanceof ISingleItemRender)
        {
            ISingleItemRender itemRender = (ISingleItemRender) item;

            if (itemRender.getItem() != null && itemRender.getName() != null)
            {
                CommonRegisterHelper.SINGLE_ITEM_RENDER_LIST.put(itemRender.getItem(), itemRender.getName());
            }
        }
        if (CommonRegisterHelper.isClient())
        {
            CommonRegisterHelper.registerSorted(item);
        }
    }

    public static void registerTileEntity(Class<? extends TileEntity> tile, String name)
    {
        GameRegistry.registerTileEntity(tile, name);
    }

    public static void registerFluidContainer(Fluid fluid, ItemStack filledContainer)
    {
        CommonRegisterHelper.registerFluidContainer(fluid, filledContainer, new ItemStack(Items.bucket));
    }

    public static void registerFluidContainer(Fluid fluid, ItemStack filledContainer, ItemStack emptyContainer)
    {
        FluidContainerRegistry.registerFluidContainer(new FluidContainerData(new FluidStack(fluid, FluidContainerRegistry.BUCKET_VOLUME), filledContainer, emptyContainer));
    }

    public static void registerGUIHandler(Object obj, IGuiHandler handler)
    {
        NetworkRegistry.INSTANCE.registerGuiHandler(obj, handler);
    }

    public static void registerFuelHandler(IFuelHandler handler)
    {
        GameRegistry.registerFuelHandler(handler);
    }

    public static void setFireBurn(Block block, int encouragement, int flammibility)
    {
        Blocks.fire.setFireInfo(block, encouragement, flammibility);
    }

    public static void registerEntity(Class<? extends Entity> entity, String name, int backgroundColor, int foregroundColor)
    {
        CommonRegisterHelper.registerEntity(entity, name, backgroundColor, foregroundColor, EnumEntityTrackerType.NORMAL);
    }

    public static void registerEntity(Class<? extends Entity> entity, String name, int backgroundColor, int foregroundColor, EnumEntityTrackerType type)
    {
        CommonRegisterHelper.registerEntity(entity, name, backgroundColor, foregroundColor, type.getTrackingRange(), type.getUpdateFrequency());
    }

    public static void registerEntity(Class<? extends Entity> entity, String name, int backgroundColor, int foregroundColor, int trackingRange, int updateFrequency)
    {
        EntityRegistry.registerModEntity(entity, name, CommonRegisterHelper.ID++, MorePlanetsCore.MOD_ID, trackingRange, updateFrequency, true, backgroundColor, foregroundColor);
    }

    public static void registerNonMobEntity(Class<? extends Entity> entity, String name)
    {
        CommonRegisterHelper.registerNonMobEntity(entity, name, EnumEntityTrackerType.NORMAL);
    }

    public static void registerNonMobEntity(Class<? extends Entity> entity, String name, EnumEntityTrackerType type)
    {
        CommonRegisterHelper.registerNonMobEntity(entity, name, type.getTrackingRange(), type.getUpdateFrequency());
    }

    public static void registerNonMobEntity(Class<? extends Entity> entity, String name, int trackingRange, int updateFrequency)
    {
        EntityRegistry.registerModEntity(entity, name, CommonRegisterHelper.ID++, MorePlanetsCore.MOD_ID, trackingRange, updateFrequency, true);
    }

    public static void registerEntityPlacement(Class<? extends Entity> entity, SpawnPlacementType type)
    {
        EntitySpawnPlacementRegistry.setPlacementType(entity, type);
    }

    public static void registerCarriable(Block block)
    {
        EntityEnderman.setCarriable(block, true);
    }

    public static void setBlockHarvestLevel(Block block, EnumHarvestLevel harvestLevel, int level)
    {
        block.setHarvestLevel(harvestLevel.toString(), level);
    }

    public static void setBlockHarvestLevel(Block block, EnumHarvestLevel harvestLevel, int level, int meta)
    {
        block.setHarvestLevel(harvestLevel.toString(), level, block.getStateFromMeta(meta));
    }

    public static void setToolHarvestLevel(Item item, EnumHarvestLevel harvestLevel, int level)
    {
        item.setHarvestLevel(harvestLevel.toString(), level);
    }

    public static void registerOreDictionary(String name, Block block)
    {
        OreDictionary.registerOre(name, block);
    }

    public static void registerOreDictionary(String name, Item item)
    {
        OreDictionary.registerOre(name, item);
    }

    public static void registerOreDictionary(String name, ItemStack itemStack)
    {
        OreDictionary.registerOre(name, itemStack);
    }

    public static void registerForgeEvent(Object event)
    {
        MinecraftForge.EVENT_BUS.register(event);
    }

    public static void registerProjectileDispense(Item item, IBehaviorDispenseItem projectile)
    {
        BlockDispenser.dispenseBehaviorRegistry.putObject(item, projectile);
    }

    public static boolean isClient()
    {
        return FMLCommonHandler.instance().getSide() == Side.CLIENT;
    }

    public static boolean isEffectiveClient()
    {
        return FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT;
    }

    private static void registerSorted(Block block)
    {
        if (block instanceof ISortableBlock)
        {
            ISortableBlock sortableBlock = (ISortableBlock) block;
            List<ItemStack> blocks = Lists.newArrayList();
            block.getSubBlocks(Item.getItemFromBlock(block), null, blocks);

            for (ItemStack itemStack : blocks)
            {
                EnumSortCategoryBlock categoryBlock = sortableBlock.getBlockCategory(itemStack.getItemDamage());

                if (!CommonRegisterHelper.SORT_MAP_BLOCKS.containsKey(categoryBlock))
                {
                    CommonRegisterHelper.SORT_MAP_BLOCKS.put(categoryBlock, Lists.newArrayList());
                }
                CommonRegisterHelper.SORT_MAP_BLOCKS.get(categoryBlock).add(new StackSorted(itemStack.getItem(), itemStack.getItemDamage()));
            }
        }
        else if (block.getCreativeTabToDisplayOn() == MorePlanetsCore.BLOCK_TAB)
        {
            throw new RuntimeException(block.getClass() + " must inherit " + ISortableBlock.class.getSimpleName() + "!");
        }
    }

    public static void postRegisteredSortBlock()
    {
        List<StackSorted> itemOrderListBlocks = Lists.newArrayList();

        for (EnumSortCategoryBlock type : EnumSortCategoryBlock.valuesCached())
        {
            List<StackSorted> stackSorteds = CommonRegisterHelper.SORT_MAP_BLOCKS.get(type);
            itemOrderListBlocks.addAll(stackSorteds);
        }
        Comparator<ItemStack> tabSorterBlocks = Ordering.explicit(itemOrderListBlocks).onResultOf(input -> new StackSorted(input.getItem(), input.getItemDamage()));
        MorePlanetsCore.BLOCK_TAB.setTabSorter(tabSorterBlocks);
    }

    private static void registerSorted(Item item)
    {
        if (item instanceof ISortableItem)
        {
            ISortableItem sortableItem = (ISortableItem) item;
            List<ItemStack> items = Lists.newArrayList();
            item.getSubItems(item, null, items);

            for (ItemStack itemStack : items)
            {
                EnumSortCategoryItem categoryItem = sortableItem.getItemCategory(itemStack.getItemDamage());

                if (!CommonRegisterHelper.SORT_MAP_ITEMS.containsKey(categoryItem))
                {
                    CommonRegisterHelper.SORT_MAP_ITEMS.put(categoryItem, Lists.newArrayList());
                }
                CommonRegisterHelper.SORT_MAP_ITEMS.get(categoryItem).add(new StackSorted(itemStack.getItem(), itemStack.getItemDamage()));
            }
        }
        else if (item.getCreativeTab() == MorePlanetsCore.ITEM_TAB)
        {
            throw new RuntimeException(item.getClass() + " must inherit " + ISortableItem.class.getSimpleName() + "!");
        }
    }

    public static void postRegisteredSortItem()
    {
        List<StackSorted> itemOrderListItems = Lists.newArrayList();

        for (EnumSortCategoryItem type : EnumSortCategoryItem.valuesCached())
        {
            itemOrderListItems.addAll(CommonRegisterHelper.SORT_MAP_ITEMS.get(type));
        }
        Comparator<ItemStack> tabSorterItems = Ordering.explicit(itemOrderListItems).onResultOf(input -> new StackSorted(input.getItem(), input.getItemDamage()));
        MorePlanetsCore.ITEM_TAB.setTabSorter(tabSorterItems);
    }
}