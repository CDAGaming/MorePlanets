package stevekung.mods.moreplanets.module.planets.chalos.blocks;

import java.util.Random;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.EnumWorldBlockLayer;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import stevekung.mods.moreplanets.core.MorePlanetsCore;
import stevekung.mods.moreplanets.util.EnumParticleTypesMP;
import stevekung.mods.moreplanets.util.blocks.BlockFluidBaseMP;
import stevekung.mods.moreplanets.util.blocks.IFishableLiquidBlock;

public class BlockFluidCheeseOfMilk extends BlockFluidBaseMP implements IFishableLiquidBlock
{
    public BlockFluidCheeseOfMilk(String name)
    {
        super(ChalosBlocks.CHEESE_OF_MILK_FLUID);
        this.setRenderLayer(EnumWorldBlockLayer.TRANSLUCENT);
        this.setLightOpacity(3);
        this.setUnlocalizedName(name);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void randomDisplayTick(World world, BlockPos pos, IBlockState state, Random rand)
    {
        int meta = this.getMetaFromState(state);

        if (rand.nextInt(64) == 0)
        {
            if (meta > 0 && meta < 8)
            {
                world.playSound(pos.getX() + 0.5F, pos.getY() + 0.5F, pos.getZ() + 0.5F, "liquid.water", rand.nextFloat() * 0.25F + 0.75F, rand.nextFloat() * 1.0F + 0.5F, false);
            }
            else if (rand.nextInt(10) == 0)
            {
                world.spawnParticle(EnumParticleTypes.SUSPENDED, pos.getX() + (double)rand.nextFloat(), pos.getY() + (double)rand.nextFloat(), pos.getZ() + (double)rand.nextFloat(), 0.0D, 0.0D, 0.0D);
            }
        }
        if (rand.nextInt(10) == 0 && World.doesBlockHaveSolidTopSurface(world, pos.down()) && !world.getBlockState(pos.down(2)).getBlock().getMaterial().blocksMovement())
        {
            double d5 = pos.getX() + rand.nextFloat();
            double d6 = pos.getY() - 1.05D;
            double d7 = pos.getZ() + rand.nextFloat();
            MorePlanetsCore.PROXY.spawnParticle(EnumParticleTypesMP.CHEESE_OF_MILK_DRIP, d5, d6, d7);
        }
    }

    @Override
    public void onEntityCollidedWithBlock(World world, BlockPos pos, IBlockState state, Entity entity)
    {
        if (entity instanceof EntityLivingBase)
        {
            ((EntityLivingBase)entity).addPotionEffect(new PotionEffect(Potion.saturation.id, 100));
        }
    }

    @Override
    protected boolean isInfinite()
    {
        return true;
    }

    @Override
    public String getName()
    {
        return "cheese_of_milk_fluid";
    }
}