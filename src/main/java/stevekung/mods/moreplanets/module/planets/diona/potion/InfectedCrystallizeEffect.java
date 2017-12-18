package stevekung.mods.moreplanets.module.planets.diona.potion;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import stevekung.mods.moreplanets.init.MPPotions;
import stevekung.mods.moreplanets.util.DamageSourceMP;
import stevekung.mods.moreplanets.util.PotionMP;
import stevekung.mods.moreplanets.util.helper.ColorHelper;

public class InfectedCrystallizeEffect extends PotionMP
{
    public InfectedCrystallizeEffect()
    {
        super("infected_crystallize", true, ColorHelper.rgbToDecimal(136, 97, 209));
    }

    @Override
    @SideOnly(Side.CLIENT)
    public int getStatusIconIndex()
    {
        Minecraft.getMinecraft().getTextureManager().bindTexture(new ResourceLocation("moreplanets:textures/potion/PotionFX.png"));
        return 0;
    }

    @Override
    public boolean isReady(int duration, int amplifier)
    {
        if (this.id == MPPotions.INFECTED_CRYSTALLIZE.id)
        {
            int k = 20 >> amplifier;
            return k > 0 ? duration % k == 0 : true;
        }
        return false;
    }

    @Override
    public void performEffect(EntityLivingBase living, int food)
    {
        if (this.id == MPPotions.INFECTED_CRYSTALLIZE.id)
        {
            living.attackEntityFrom(DamageSourceMP.INFECTED_CRYSTALLIZE, 1.0F);
        }
    }
}