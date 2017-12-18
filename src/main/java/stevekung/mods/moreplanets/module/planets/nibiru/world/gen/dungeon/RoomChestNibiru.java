package stevekung.mods.moreplanets.module.planets.nibiru.world.gen.dungeon;

import java.util.Random;

import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.WeightedRandomChestContent;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import net.minecraftforge.common.ChestGenHooks;
import stevekung.mods.moreplanets.util.helper.BlockStateHelper;
import stevekung.mods.moreplanets.util.helper.ItemLootHelper;
import stevekung.mods.moreplanets.util.tileentity.TileEntityAncientChestMP;
import stevekung.mods.moreplanets.util.world.gen.dungeon.DungeonConfigurationMP;

public class RoomChestNibiru extends RoomEmptyNibiru
{
    public RoomChestNibiru() {}

    public RoomChestNibiru(DungeonConfigurationMP configuration, Random rand, int blockPosX, int blockPosZ, int sizeX, int sizeY, int sizeZ, EnumFacing entranceDir)
    {
        super(configuration, rand, blockPosX, blockPosZ, sizeX, sizeY, sizeZ, entranceDir);
    }

    public RoomChestNibiru(DungeonConfigurationMP configuration, Random rand, int blockPosX, int blockPosZ, EnumFacing entranceDir)
    {
        super(configuration, rand, blockPosX, blockPosZ, rand.nextInt(4) + 6, configuration.getRoomHeight(), rand.nextInt(4) + 6, entranceDir);
    }

    @Override
    public boolean addComponentParts(World world, Random rand, StructureBoundingBox boundingBox)
    {
        if (super.addComponentParts(world, rand, boundingBox))
        {
            int chestX = this.sizeX / 2;
            int chestY = 1;
            int chestZ = this.sizeZ / 2;
            this.setBlockState(world, this.configuration.getAncientChestBlock().withProperty(BlockStateHelper.FACING, this.getDirection().getOpposite()), chestX, chestY, chestZ, boundingBox);
            BlockPos blockpos = new BlockPos(this.getXWithOffset(chestX, chestZ), this.getYWithOffset(chestY), this.getZWithOffset(chestX, chestZ));
            TileEntityAncientChestMP chest = (TileEntityAncientChestMP)world.getTileEntity(blockpos);

            if (chest != null)
            {
                for (int i = 0; i < chest.getSizeInventory(); ++i)
                {
                    chest.setInventorySlotContents(i, null);
                }
                ChestGenHooks info = ChestGenHooks.getInfo(ItemLootHelper.COMMON_SPACE_DUNGEON);
                WeightedRandomChestContent.generateChestContents(rand, info.getItems(rand), chest, info.getCount(rand));
            }
            return true;
        }
        return false;
    }
}