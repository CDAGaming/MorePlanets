package stevekung.mods.moreplanets.module.planets.chalos.recipe;

import java.util.HashMap;
import java.util.List;

import com.google.common.collect.Lists;

import micdoodle8.mods.galacticraft.api.recipe.INasaWorkbenchRecipe;
import micdoodle8.mods.galacticraft.core.recipe.NasaWorkbenchRecipe;
import net.minecraft.item.ItemStack;
import stevekung.mods.moreplanets.inventory.InventorySchematicRocket;

public class Tier5RocketRecipes
{
    private static List<INasaWorkbenchRecipe> ROCKET_RECIPE = Lists.newArrayList();

    public static ItemStack findMatchingRocketRecipe(InventorySchematicRocket inv)
    {
        for (INasaWorkbenchRecipe recipe : Tier5RocketRecipes.getRocketRecipes())
        {
            if (recipe.matches(inv))
            {
                return recipe.getRecipeOutput();
            }
        }
        return null;
    }

    public static void addRocketRecipe(ItemStack result, HashMap<Integer, ItemStack> input)
    {
        Tier5RocketRecipes.addRocketRecipe(new NasaWorkbenchRecipe(result, input));
    }

    public static void addRocketRecipe(INasaWorkbenchRecipe recipe)
    {
        Tier5RocketRecipes.ROCKET_RECIPE.add(recipe);
    }

    public static List<INasaWorkbenchRecipe> getRocketRecipes()
    {
        return Tier5RocketRecipes.ROCKET_RECIPE;
    }
}