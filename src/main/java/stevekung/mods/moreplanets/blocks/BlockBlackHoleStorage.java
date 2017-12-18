package stevekung.mods.moreplanets.blocks;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import stevekung.mods.moreplanets.core.MorePlanetsCore;
import stevekung.mods.moreplanets.entity.EntityBlackHoleStorage;
import stevekung.mods.moreplanets.tileentity.TileEntityBlackHoleStorage;
import stevekung.mods.moreplanets.util.EnumParticleTypesMP;
import stevekung.mods.moreplanets.util.ItemDescription;
import stevekung.mods.moreplanets.util.JsonUtils;
import stevekung.mods.moreplanets.util.blocks.BlockBaseMP;
import stevekung.mods.moreplanets.util.blocks.EnumSortCategoryBlock;
import stevekung.mods.moreplanets.util.blocks.IBlockDescription;
import stevekung.mods.moreplanets.util.helper.ItemDescriptionHelper;

public class BlockBlackHoleStorage extends BlockBaseMP implements ITileEntityProvider, IBlockDescription
{
    public BlockBlackHoleStorage(String name)
    {
        super(Material.iron);
        this.setStepSound(Block.soundTypeMetal);
        this.setHardness(2.0F);
        this.setUnlocalizedName(name);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void randomDisplayTick(World world, BlockPos pos, IBlockState state, Random rand)
    {
        TileEntity tile = world.getTileEntity(pos);

        if (tile instanceof TileEntityBlackHoleStorage)
        {
            TileEntityBlackHoleStorage storage = (TileEntityBlackHoleStorage) tile;

            if (!storage.disableBlackHole)
            {
                for (int i = 0; i < 16; i++)
                {
                    double x = pos.getX() + rand.nextFloat() - 0.5D;
                    double y = pos.getY() + 0.5D;
                    double z = pos.getZ() + rand.nextFloat();
                    double motionX = rand.nextDouble() - 0.5D;
                    double motionZ = rand.nextDouble() - 0.5D;
                    MorePlanetsCore.PROXY.spawnParticle(EnumParticleTypesMP.DARK_PORTAL, x + 0.5D, y, z, motionX, 1.0D, 0.0D);
                    MorePlanetsCore.PROXY.spawnParticle(EnumParticleTypesMP.DARK_PORTAL, x + 0.5D, y, z + 0.0D, 0.0D, 1.0D, motionZ);
                }
            }
        }
    }

    @Override
    public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack itemStack)
    {
        TileEntity tile = world.getTileEntity(pos);
        world.setBlockState(pos, this.getDefaultState());

        if (!world.isRemote)
        {
            if (placer instanceof EntityPlayer)
            {
                EntityPlayer player = (EntityPlayer) placer;
                EntityBlackHoleStorage blackHole = new EntityBlackHoleStorage(world);
                blackHole.setTileEntityPos(pos);
                blackHole.setLocationAndAngles(pos.getX() + 0.5D, pos.getY() + 2, pos.getZ() + 0.5D, 0.0F, 0.0F);
                world.spawnEntityInWorld(blackHole);
                world.playSoundEffect(pos.getX() + 0.5D, pos.getY() + 2, pos.getZ() + 0.5D, "moreplanets:ambient.black_hole.start", 1.0F, 1.0F);

                if (tile instanceof TileEntityBlackHoleStorage)
                {
                    TileEntityBlackHoleStorage storage = (TileEntityBlackHoleStorage) tile;
                    storage.ownerUUID = player.getGameProfile().getId().toString();

                    if (itemStack.hasTagCompound())
                    {
                        NBTTagList list = itemStack.getTagCompound().getTagList("Items", 10);
                        storage.inventory = new ItemStack[storage.getSizeInventory()];
                        storage.disableBlackHole = itemStack.getTagCompound().getBoolean("Disable");
                        storage.useHopper = itemStack.getTagCompound().getBoolean("Hopper");
                        storage.xp = itemStack.getTagCompound().getInteger("XP");
                        storage.collectMode = itemStack.getTagCompound().getString("Mode");

                        for (int i = 0; i <  list.tagCount(); ++i)
                        {
                            NBTTagCompound nbt =  list.getCompoundTagAt(i);
                            int slot = nbt.getByte("Slot");

                            if (slot >= 0 && slot < storage.inventory.length)
                            {
                                storage.inventory[slot] = ItemStack.loadItemStackFromNBT(nbt);
                            }
                        }
                    }
                }
            }
        }
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumFacing side, float hitX, float hitY, float hitZ)
    {
        if (world.isRemote)
        {
            return true;
        }
        else
        {
            TileEntity tile = world.getTileEntity(pos);

            if (tile instanceof TileEntityBlackHoleStorage)
            {
                TileEntityBlackHoleStorage storage = (TileEntityBlackHoleStorage) tile;

                if (player.getGameProfile().getId().toString().equals(storage.ownerUUID))
                {
                    if (player.isSneaking() && storage.xp > 0)
                    {
                        Random rand = world.rand;
                        int drainExp = rand.nextInt(25) == 0 ? 24 + rand.nextInt(16) : rand.nextInt(10) == 0 ? 20 + rand.nextInt(5) : rand.nextInt(5) == 0 ? 10 + rand.nextInt(5) : 3 + rand.nextInt(5);
                        storage.xp -= storage.xp < drainExp ? storage.xp : drainExp;
                        player.addExperience(drainExp);
                        player.worldObj.playSoundAtEntity(player, "random.orb", 0.1F, 0.5F * ((player.worldObj.rand.nextFloat() - player.worldObj.rand.nextFloat()) * 0.7F + 1.8F));
                    }
                    else
                    {
                        player.openGui(MorePlanetsCore.MOD_ID, -1, world, pos.getX(), pos.getY(), pos.getZ());
                    }
                }
                else
                {
                    JsonUtils json = new JsonUtils();
                    player.addChatMessage(json.text(StatCollector.translateToLocal("gui.bh_storage_not_owner.message")).setChatStyle(json.red()));
                }
            }
            return true;
        }
    }

    @Override
    public void breakBlock(World world, BlockPos pos, IBlockState state)
    {
        world.updateComparatorOutputLevel(pos, this);
        world.playSoundEffect(pos.getX() + 0.5D, pos.getY() + 2, pos.getZ() + 0.5D, "moreplanets:ambient.black_hole.explode", 1.0F, 1.0F);
        super.breakBlock(world, pos, state);
    }

    @Override
    public void harvestBlock(World world, EntityPlayer player, BlockPos pos, IBlockState state, TileEntity tile)
    {
        player.addExhaustion(0.025F);
        ItemStack itemStack = new ItemStack(this);

        if (tile instanceof TileEntityBlackHoleStorage)
        {
            NBTTagCompound nbt = new NBTTagCompound();
            TileEntityBlackHoleStorage storage = (TileEntityBlackHoleStorage) tile;
            NBTTagList list = new NBTTagList();

            for (int i = 0; i < storage.inventory.length; ++i)
            {
                if (storage.inventory[i] != null)
                {
                    NBTTagCompound compound = new NBTTagCompound();
                    compound.setByte("Slot", (byte)i);
                    storage.inventory[i].writeToNBT(compound);
                    list.appendTag(compound);
                }
            }
            nbt.setTag("Items", list);
            nbt.setBoolean("Disable", storage.disableBlackHole);
            nbt.setBoolean("Hopper", storage.useHopper);
            nbt.setString("Mode", storage.collectMode);
            nbt.setInteger("XP", storage.xp);
            itemStack.setTagCompound(nbt);
            Block.spawnAsEntity(world, pos, itemStack);
        }
    }

    @Override
    public float getPlayerRelativeBlockHardness(EntityPlayer player, World world, BlockPos pos)
    {
        TileEntity tile = world.getTileEntity(pos);

        if (tile instanceof TileEntityBlackHoleStorage)
        {
            TileEntityBlackHoleStorage storage = (TileEntityBlackHoleStorage) tile;

            if (!player.getGameProfile().getId().toString().equals(storage.ownerUUID))
            {
                return -1.0F;
            }
        }
        return super.getPlayerRelativeBlockHardness(player, world, pos);
    }

    @Override
    public boolean isOpaqueCube()
    {
        return false;
    }

    @Override
    public boolean isFullCube()
    {
        return false;
    }

    @Override
    public int getRenderType()
    {
        return 2;
    }

    @Override
    public boolean hasComparatorInputOverride()
    {
        return true;
    }

    @Override
    public int getComparatorInputOverride(World world, BlockPos pos)
    {
        return Container.calcRedstone(world.getTileEntity(pos));
    }

    @Override
    public TileEntity createNewTileEntity(World world, int meta)
    {
        return new TileEntityBlackHoleStorage();
    }

    @Override
    public EnumSortCategoryBlock getBlockCategory(int meta)
    {
        return EnumSortCategoryBlock.MACHINE_BLOCK;
    }

    @Override
    public ItemDescription getDescription()
    {
        return (itemStack, list) -> list.addAll(ItemDescriptionHelper.getDescription(BlockBlackHoleStorage.this.getUnlocalizedName() + ".description"));
    }

    @Override
    public String getName()
    {
        return "black_hole_storage";
    }
}