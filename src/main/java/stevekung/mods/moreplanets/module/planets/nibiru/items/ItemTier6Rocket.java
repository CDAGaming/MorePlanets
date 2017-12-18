package stevekung.mods.moreplanets.module.planets.nibiru.items;

import java.util.List;

import micdoodle8.mods.galacticraft.api.item.IHoldableItem;
import micdoodle8.mods.galacticraft.core.GCFluids;
import micdoodle8.mods.galacticraft.core.util.GCCoreUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import stevekung.mods.moreplanets.module.planets.nibiru.entity.EntityTier6Rocket;
import stevekung.mods.moreplanets.util.CachedEnumUtil;
import stevekung.mods.moreplanets.util.items.ISortableItem;
import stevekung.mods.moreplanets.util.items.ItemRocketBaseMP;

public class ItemTier6Rocket extends ItemRocketBaseMP implements IHoldableItem, ISortableItem
{
    public ItemTier6Rocket(String name)
    {
        super();
        this.setUnlocalizedName(name);
    }

    @Override
    protected void spawnRocket(ItemStack itemStack, World world, EntityPlayer player, float centerX, float centerY, float centerZ)
    {
        EntityTier6Rocket rocket = new EntityTier6Rocket(world, centerX, centerY, centerZ, CachedEnumUtil.valuesRocketCached()[itemStack.getItemDamage()]);

        rocket.rotationYaw += 45;
        rocket.setPosition(rocket.posX, rocket.posY + rocket.getOnPadYOffset(), rocket.posZ);
        world.spawnEntityInWorld(rocket);

        if (itemStack.hasTagCompound() && itemStack.getTagCompound().hasKey("RocketFuel"))
        {
            rocket.fuelTank.fill(new FluidStack(GCFluids.fluidFuel, itemStack.getTagCompound().getInteger("RocketFuel")), true);
        }

        if (!player.capabilities.isCreativeMode)
        {
            itemStack.stackSize--;

            if (itemStack.stackSize <= 0)
            {
                itemStack = null;
            }
        }
        if (rocket.getType().getPreFueled())
        {
            rocket.fuelTank.fill(new FluidStack(GCFluids.fluidFuel, rocket.getMaxFuel()), true);
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    protected void addDescription(ItemStack itemStack, List<String> list)
    {
        EntityTier6Rocket rocket = new EntityTier6Rocket(Minecraft.getMinecraft().theWorld, 0, 0, 0, CachedEnumUtil.valuesRocketCached()[itemStack.getItemDamage()]);
        list.add(GCCoreUtil.translate("gui.message.fuel.name") + ": " + itemStack.getTagCompound().getInteger("RocketFuel") + " / " + rocket.fuelTank.getCapacity());
    }
}