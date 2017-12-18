package stevekung.mods.moreplanets.module.planets.diona.items;

import net.minecraft.item.Item;
import net.minecraft.item.Item.ToolMaterial;
import net.minecraft.item.ItemArmor.ArmorMaterial;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.util.EnumHelper;
import stevekung.mods.moreplanets.module.planets.diona.blocks.DionaBlocks;
import stevekung.mods.moreplanets.module.planets.diona.items.armor.ItemArmorIllenium;
import stevekung.mods.moreplanets.module.planets.diona.items.armor.ItemBreathableIllenium;
import stevekung.mods.moreplanets.util.EnumHarvestLevel;
import stevekung.mods.moreplanets.util.helper.CommonRegisterHelper;
import stevekung.mods.moreplanets.util.items.EnumSortCategoryItem;
import stevekung.mods.moreplanets.util.items.ItemBaseMP;
import stevekung.mods.moreplanets.util.items.ItemBucketMP;
import stevekung.mods.moreplanets.util.items.ItemDungeonKeyMP;
import stevekung.mods.moreplanets.util.items.tools.*;

public class DionaItems
{
    // Base
    public static Item DIONA_ITEM;
    public static Item DIONA_DUNGEON_KEY;
    public static Item INFECTED_CRYSTALLIZE_BOMB;
    public static Item TIER_4_ROCKET;
    public static Item TIER_4_ROCKET_PART;
    public static Item TIER_5_ROCKET_SCHEMATIC;
    public static Item INFECTED_CRYSTALLIZE_SLIMEBALL;
    public static Item INFECTED_CRYSTALLIZE_ARROW;
    public static Item CRYSTALLIZE_WATER_FLUID_BUCKET;
    public static Item CRYSTALLIZE_LAVA_FLUID_BUCKET;
    public static Item DARK_ENERGY_PEARL;

    // Tools
    public static Item ILLENIUM_SWORD;
    public static Item ILLENIUM_SHOVEL;
    public static Item ILLENIUM_PICKAXE;
    public static Item ILLENIUM_AXE;
    public static Item ILLENIUM_HOE;

    // Armor
    public static Item ILLENIUM_HELMET;
    public static Item ILLENIUM_CHESTPLATE;
    public static Item ILLENIUM_LEGGINGS;
    public static Item ILLENIUM_BOOTS;
    public static Item BREATHABLE_ILLENIUM_HELMET;

    // Material
    public static ToolMaterial ILLENIUM_TOOL = EnumHelper.addToolMaterial("ILLENIUM", 3, 1432, 8.5F, 4.5F, 10);
    public static ArmorMaterial ILLENIUM_ARMOR = EnumHelper.addArmorMaterial("ILLENIUM", "ILLENIUM", 40, new int[] { 5, 10, 8, 5 }, 12);

    public static void init()
    {
        /**************************************************************/
        /**********************INITIAL BASE STUFF**********************/
        /**************************************************************/

        DionaItems.DIONA_ITEM = new ItemDiona("diona_item");
        DionaItems.TIER_4_ROCKET_PART = new ItemTier4RocketPart("tier_4_rocket_part");
        DionaItems.TIER_5_ROCKET_SCHEMATIC = new ItemTier5RocketSchematic("tier_5_rocket_schematic");
        DionaItems.TIER_4_ROCKET = new ItemTier4Rocket("tier_4_rocket");
        DionaItems.DIONA_DUNGEON_KEY = new ItemDungeonKeyMP("diona_dungeon_key", 4);
        DionaItems.INFECTED_CRYSTALLIZE_BOMB = new ItemInfectedCrystallizeBomb("infected_crystallize_bomb");
        DionaItems.INFECTED_CRYSTALLIZE_SLIMEBALL = new ItemBaseMP("infected_crystallize_slimeball");
        DionaItems.INFECTED_CRYSTALLIZE_ARROW = new ItemBaseMP("infected_crystallize_arrow").setSortCategory(EnumSortCategoryItem.ARROW);
        DionaItems.CRYSTALLIZE_WATER_FLUID_BUCKET = new ItemBucketMP("crystallize_water_bucket", DionaBlocks.CRYSTALLIZE_WATER_FLUID_BLOCK);
        DionaItems.CRYSTALLIZE_LAVA_FLUID_BUCKET = new ItemBucketMP("crystallize_lava_bucket", DionaBlocks.CRYSTALLIZE_LAVA_FLUID_BLOCK);
        DionaItems.DARK_ENERGY_PEARL = new ItemBaseMP("dark_energy_pearl").setSortCategory(EnumSortCategoryItem.GENERAL);

        /**************************************************************/
        /**********************INITIAL TOOL STUFF**********************/
        /**************************************************************/

        DionaItems.ILLENIUM_SWORD = new ItemSwordMP("illenium_sword", DionaItems.ILLENIUM_TOOL, DionaItems.DIONA_ITEM, 2);
        DionaItems.ILLENIUM_SHOVEL = new ItemShovelMP("illenium_shovel", DionaItems.ILLENIUM_TOOL, DionaItems.DIONA_ITEM, 2);
        DionaItems.ILLENIUM_PICKAXE = new ItemPickaxeMP("illenium_pickaxe", DionaItems.ILLENIUM_TOOL, DionaItems.DIONA_ITEM, 2);
        DionaItems.ILLENIUM_AXE = new ItemAxeMP("illenium_axe", DionaItems.ILLENIUM_TOOL, DionaItems.DIONA_ITEM, 2);
        DionaItems.ILLENIUM_HOE = new ItemHoeMP("illenium_hoe", DionaItems.ILLENIUM_TOOL, DionaItems.DIONA_ITEM, 2);

        /**************************************************************/
        /*********************INITIAL ARMOR STUFF**********************/
        /**************************************************************/

        DionaItems.ILLENIUM_HELMET = new ItemArmorIllenium("illenium_helmet", DionaItems.ILLENIUM_ARMOR, 0);
        DionaItems.ILLENIUM_CHESTPLATE = new ItemArmorIllenium("illenium_chestplate", DionaItems.ILLENIUM_ARMOR, 1);
        DionaItems.ILLENIUM_LEGGINGS = new ItemArmorIllenium("illenium_leggings", DionaItems.ILLENIUM_ARMOR, 2);
        DionaItems.ILLENIUM_BOOTS = new ItemArmorIllenium("illenium_boots", DionaItems.ILLENIUM_ARMOR, 3);
        DionaItems.BREATHABLE_ILLENIUM_HELMET = new ItemBreathableIllenium("breathable_illenium_helmet", DionaItems.ILLENIUM_ARMOR, 0);

        /**************************************************************/
        /**********************REGISTER STUFF**************************/
        /**************************************************************/

        CommonRegisterHelper.registerItem(DionaItems.DIONA_ITEM);
        CommonRegisterHelper.registerItem(DionaItems.INFECTED_CRYSTALLIZE_BOMB);
        CommonRegisterHelper.registerItem(DionaItems.TIER_4_ROCKET_PART);
        CommonRegisterHelper.registerItem(DionaItems.TIER_5_ROCKET_SCHEMATIC);
        CommonRegisterHelper.registerItem(DionaItems.TIER_4_ROCKET);
        CommonRegisterHelper.registerItem(DionaItems.DIONA_DUNGEON_KEY);
        CommonRegisterHelper.registerItem(DionaItems.INFECTED_CRYSTALLIZE_SLIMEBALL);
        CommonRegisterHelper.registerItem(DionaItems.INFECTED_CRYSTALLIZE_ARROW);
        CommonRegisterHelper.registerItem(DionaItems.CRYSTALLIZE_WATER_FLUID_BUCKET);
        CommonRegisterHelper.registerItem(DionaItems.CRYSTALLIZE_LAVA_FLUID_BUCKET);
        CommonRegisterHelper.registerItem(DionaItems.DARK_ENERGY_PEARL);

        CommonRegisterHelper.registerItem(DionaItems.ILLENIUM_SWORD);
        CommonRegisterHelper.registerItem(DionaItems.ILLENIUM_SHOVEL);
        CommonRegisterHelper.registerItem(DionaItems.ILLENIUM_PICKAXE);
        CommonRegisterHelper.registerItem(DionaItems.ILLENIUM_AXE);
        CommonRegisterHelper.registerItem(DionaItems.ILLENIUM_HOE);

        CommonRegisterHelper.registerItem(DionaItems.ILLENIUM_HELMET);
        CommonRegisterHelper.registerItem(DionaItems.ILLENIUM_CHESTPLATE);
        CommonRegisterHelper.registerItem(DionaItems.ILLENIUM_LEGGINGS);
        CommonRegisterHelper.registerItem(DionaItems.ILLENIUM_BOOTS);
        CommonRegisterHelper.registerItem(DionaItems.BREATHABLE_ILLENIUM_HELMET);

        /**************************************************************/
        /********************HARVEST LEVEL STUFF***********************/
        /**************************************************************/

        CommonRegisterHelper.setToolHarvestLevel(DionaItems.ILLENIUM_SHOVEL, EnumHarvestLevel.SHOVEL, 3);
        CommonRegisterHelper.setToolHarvestLevel(DionaItems.ILLENIUM_PICKAXE, EnumHarvestLevel.PICKAXE, 3);
        CommonRegisterHelper.setToolHarvestLevel(DionaItems.ILLENIUM_AXE, EnumHarvestLevel.AXE, 3);

        /**************************************************************/
        /********************ORE DICTIONARY STUFF**********************/
        /**************************************************************/

        CommonRegisterHelper.registerOreDictionary("slimeball", DionaItems.INFECTED_CRYSTALLIZE_SLIMEBALL);

        /**************************************************************/
        /********************FLUID CONTAINER STUFF*********************/
        /**************************************************************/

        CommonRegisterHelper.registerFluidContainer(DionaBlocks.CRYSTALLIZE_WATER_FLUID, new ItemStack(DionaItems.CRYSTALLIZE_WATER_FLUID_BUCKET));
        CommonRegisterHelper.registerFluidContainer(DionaBlocks.CRYSTALLIZE_LAVA_FLUID, new ItemStack(DionaItems.CRYSTALLIZE_LAVA_FLUID_BUCKET));
    }
}