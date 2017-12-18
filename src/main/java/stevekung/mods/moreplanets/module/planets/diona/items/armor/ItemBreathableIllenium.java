package stevekung.mods.moreplanets.module.planets.diona.items.armor;

import net.minecraft.client.model.ModelBiped;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import stevekung.mods.moreplanets.module.planets.diona.items.DionaItems;
import stevekung.mods.moreplanets.util.helper.ClientRegisterHelper;
import stevekung.mods.moreplanets.util.items.armor.ItemBreathableArmor;

public class ItemBreathableIllenium extends ItemBreathableArmor
{
    public ItemBreathableIllenium(String name, ArmorMaterial material, int type)
    {
        super(material, type);
        this.setUnlocalizedName(name);
    }

    @Override
    public String getArmorTexture(ItemStack itemStack, Entity entity, int slot, String layer)
    {
        if (itemStack.getItem() == this)
        {
            return "moreplanets:textures/model/armor/breathable_illenium.png";
        }
        return null;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public ModelBiped getArmorModel(EntityLivingBase entityLiving, ItemStack itemStack, int armorSlot, ModelBiped defaultModel)
    {
        if (entityLiving instanceof EntityPlayer)
        {
            return ClientRegisterHelper.getTranclucentArmorModel(armorSlot, defaultModel);
        }
        return defaultModel;
    }

    @Override
    public Item getRepairItems()
    {
        return DionaItems.DIONA_ITEM;
    }

    @Override
    public int getRepairItemsMetadata()
    {
        return 2;
    }

    @Override
    public EnumGearType getGearType()
    {
        return EnumGearType.HELMET;
    }

    @Override
    public Item getBreathableArmor()
    {
        return this;
    }

    @Override
    public String getName()
    {
        return "breathable_illenium_helmet";
    }
}