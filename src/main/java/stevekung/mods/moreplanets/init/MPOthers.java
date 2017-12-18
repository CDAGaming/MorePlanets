package stevekung.mods.moreplanets.init;

import net.minecraft.block.Block;
import net.minecraft.block.BlockDispenser;
import net.minecraft.block.state.IBlockState;
import net.minecraft.dispenser.BehaviorDefaultDispenseItem;
import net.minecraft.dispenser.IBlockSource;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntityDispenser;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fluids.BlockFluidBase;
import stevekung.mods.moreplanets.module.planets.chalos.blocks.ChalosBlocks;
import stevekung.mods.moreplanets.module.planets.chalos.items.ChalosItems;
import stevekung.mods.moreplanets.module.planets.diona.blocks.DionaBlocks;
import stevekung.mods.moreplanets.module.planets.diona.entity.EntityInfectedCrystallizeBomb;
import stevekung.mods.moreplanets.module.planets.diona.entity.projectile.EntityInfectedCrystallizeArrow;
import stevekung.mods.moreplanets.module.planets.diona.items.DionaItems;
import stevekung.mods.moreplanets.module.planets.fronos.blocks.FronosBlocks;
import stevekung.mods.moreplanets.module.planets.nibiru.blocks.NibiruBlocks;
import stevekung.mods.moreplanets.module.planets.nibiru.entity.projectile.EntityInfectedArrow;
import stevekung.mods.moreplanets.module.planets.nibiru.entity.projectile.EntityInfectedEgg;
import stevekung.mods.moreplanets.module.planets.nibiru.entity.projectile.EntityInfectedSnowball;
import stevekung.mods.moreplanets.module.planets.nibiru.items.NibiruItems;
import stevekung.mods.moreplanets.util.BehaviorProjectileDispenseBucketMP;
import stevekung.mods.moreplanets.util.BehaviorProjectileDispenseMP;
import stevekung.mods.moreplanets.util.helper.CommonRegisterHelper;

public class MPOthers
{
    public static void init()
    {
        MPOthers.registerDispenserObject();
        MPOthers.registerEndermanCarriableBlock();
    }

    private static void registerDispenserObject()
    {
        CommonRegisterHelper.registerProjectileDispense(DionaItems.INFECTED_CRYSTALLIZE_BOMB, new BehaviorProjectileDispenseMP(EntityInfectedCrystallizeBomb.class));
        CommonRegisterHelper.registerProjectileDispense(NibiruItems.INFECTED_SNOWBALL, new BehaviorProjectileDispenseMP(EntityInfectedSnowball.class));
        CommonRegisterHelper.registerProjectileDispense(NibiruItems.INFECTED_EGG, new BehaviorProjectileDispenseMP(EntityInfectedEgg.class));
        CommonRegisterHelper.registerProjectileDispense(DionaItems.INFECTED_CRYSTALLIZE_ARROW, new BehaviorProjectileDispenseMP(EntityInfectedCrystallizeArrow.class, true));
        CommonRegisterHelper.registerProjectileDispense(NibiruItems.INFECTED_ARROW, new BehaviorProjectileDispenseMP(EntityInfectedArrow.class, true));
        CommonRegisterHelper.registerProjectileDispense(DionaItems.CRYSTALLIZE_WATER_FLUID_BUCKET, new BehaviorProjectileDispenseBucketMP());
        CommonRegisterHelper.registerProjectileDispense(DionaItems.CRYSTALLIZE_LAVA_FLUID_BUCKET, new BehaviorProjectileDispenseBucketMP());
        CommonRegisterHelper.registerProjectileDispense(ChalosItems.CHEESE_OF_MILK_FLUID_BUCKET, new BehaviorProjectileDispenseBucketMP());
        CommonRegisterHelper.registerProjectileDispense(ChalosItems.CHEESE_OF_MILK_GAS_BUCKET, new BehaviorProjectileDispenseBucketMP());
        CommonRegisterHelper.registerProjectileDispense(NibiruItems.INFECTED_WATER_FLUID_BUCKET, new BehaviorProjectileDispenseBucketMP());
        CommonRegisterHelper.registerProjectileDispense(NibiruItems.HELIUM_GAS_BUCKET, new BehaviorProjectileDispenseBucketMP());

        CommonRegisterHelper.registerProjectileDispense(Items.bucket, new BehaviorDefaultDispenseItem()
        {
            private BehaviorDefaultDispenseItem item = new BehaviorDefaultDispenseItem();

            @Override
            public ItemStack dispenseStack(IBlockSource source, ItemStack itemStack)
            {
                World world = source.getWorld();
                BlockPos pos = source.getBlockPos().offset(BlockDispenser.getFacing(source.getBlockMetadata()));
                IBlockState state = world.getBlockState(pos);
                Block block = state.getBlock();
                boolean isDefault = state.getValue(BlockFluidBase.LEVEL).intValue() == 0;
                boolean isDefaultGas = state.getValue(BlockFluidBase.LEVEL).intValue() == 7;
                Item item;

                if (block == DionaBlocks.CRYSTALLIZE_WATER_FLUID_BLOCK && isDefault)
                {
                    item = DionaItems.CRYSTALLIZE_WATER_FLUID_BUCKET;
                }
                else if (block == DionaBlocks.CRYSTALLIZE_LAVA_FLUID_BLOCK && isDefault)
                {
                    item = DionaItems.CRYSTALLIZE_LAVA_FLUID_BUCKET;
                }
                else if (block == ChalosBlocks.CHEESE_OF_MILK_FLUID_BLOCK && isDefault)
                {
                    item = ChalosItems.CHEESE_OF_MILK_FLUID_BUCKET;
                }
                else if (block == ChalosBlocks.CHEESE_OF_MILK_GAS_BLOCK && isDefaultGas)
                {
                    item = ChalosItems.CHEESE_OF_MILK_GAS_BUCKET;
                }
                else if (block == NibiruBlocks.INFECTED_WATER_FLUID_BLOCK && isDefault)
                {
                    item = NibiruItems.INFECTED_WATER_FLUID_BUCKET;
                }
                else if (block == NibiruBlocks.HELIUM_GAS_BLOCK && isDefaultGas)
                {
                    item = NibiruItems.HELIUM_GAS_BUCKET;
                }
                else
                {
                    return super.dispenseStack(source, itemStack);
                }

                world.setBlockToAir(pos);

                if (--itemStack.stackSize == 0)
                {
                    itemStack.setItem(item);
                    itemStack.stackSize = 1;
                }
                else if (((TileEntityDispenser)source.getBlockTileEntity()).addItemStack(new ItemStack(item)) < 0)
                {
                    this.item.dispense(source, new ItemStack(item));
                }
                return itemStack;
            }
        });
    }

    private static void registerEndermanCarriableBlock()
    {
        CommonRegisterHelper.registerCarriable(DionaBlocks.INFECTED_CRYSTALLIZE_SLIME_BLOCK);
        CommonRegisterHelper.registerCarriable(DionaBlocks.INFECTED_CRYSTALLIZE_PART);
        CommonRegisterHelper.registerCarriable(DionaBlocks.ZELIUS_EGG);
        CommonRegisterHelper.registerCarriable(ChalosBlocks.CHEESE_GRASS);
        CommonRegisterHelper.registerCarriable(ChalosBlocks.CHEESE_DIRT);
        CommonRegisterHelper.registerCarriable(ChalosBlocks.CHEESE_SLIME_BLOCK);
        CommonRegisterHelper.registerCarriable(ChalosBlocks.CHEESE_OF_MILK_CAKE);
        CommonRegisterHelper.registerCarriable(NibiruBlocks.INFECTED_GRASS);
        CommonRegisterHelper.registerCarriable(NibiruBlocks.INFECTED_DIRT);
        CommonRegisterHelper.registerCarriable(NibiruBlocks.INFECTED_CLAY);
        CommonRegisterHelper.registerCarriable(NibiruBlocks.INFECTED_GRAVEL);
        CommonRegisterHelper.registerCarriable(NibiruBlocks.INFECTED_SNOW);
        CommonRegisterHelper.registerCarriable(NibiruBlocks.INFECTED_SNOW_LAYER);
        CommonRegisterHelper.registerCarriable(NibiruBlocks.GREEN_VEIN_GRASS);
        CommonRegisterHelper.registerCarriable(NibiruBlocks.INFECTED_MELON_BLOCK);
        CommonRegisterHelper.registerCarriable(NibiruBlocks.INFECTED_SAND);
        CommonRegisterHelper.registerCarriable(NibiruBlocks.INFECTED_CACTUS);
        CommonRegisterHelper.registerCarriable(NibiruBlocks.NIBIRU_FLOWER);
        CommonRegisterHelper.registerCarriable(NibiruBlocks.INFECTED_VINES_DIRT);
        CommonRegisterHelper.registerCarriable(NibiruBlocks.PURIFY_GRAVEL);
        CommonRegisterHelper.registerCarriable(FronosBlocks.FRONOS_GRASS);
        CommonRegisterHelper.registerCarriable(FronosBlocks.FRONOS_DIRT);
        CommonRegisterHelper.registerCarriable(FronosBlocks.CANDY_CANE_1);
        CommonRegisterHelper.registerCarriable(FronosBlocks.CANDY_CANE_2);
    }
}