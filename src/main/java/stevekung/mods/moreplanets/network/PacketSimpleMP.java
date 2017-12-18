package stevekung.mods.moreplanets.network;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import io.netty.buffer.ByteBuf;
import micdoodle8.mods.galacticraft.core.network.NetworkUtil;
import micdoodle8.mods.galacticraft.core.network.PacketBase;
import micdoodle8.mods.galacticraft.core.util.WorldUtil;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import stevekung.mods.moreplanets.core.event.WorldTickEventHandler;
import stevekung.mods.moreplanets.core.handler.TeleportHandler;
import stevekung.mods.moreplanets.tileentity.TileEntityBlackHoleStorage;
import stevekung.mods.moreplanets.tileentity.TileEntityDarkEnergyReceiver;
import stevekung.mods.moreplanets.util.MPLog;
import stevekung.mods.moreplanets.util.helper.WorldDimensionHelper;

public class PacketSimpleMP extends PacketBase
{
    private EnumSimplePacketMP type;
    private List<Object> data;

    public PacketSimpleMP()
    {
        super();
    }

    public PacketSimpleMP(EnumSimplePacketMP packetType, int dimID, Object... data)
    {
        this(packetType, dimID, Arrays.asList(data));
    }

    public PacketSimpleMP(EnumSimplePacketMP packetType, int dimID, List<Object> data)
    {
        super(dimID);

        if (packetType.getDecodeClasses().length != data.size())
        {
            MPLog.warning("More Planets Simple Packet found data length different than packet type: %s", packetType.name());
        }
        this.type = packetType;
        this.data = data;
    }

    @Override
    public void encodeInto(ByteBuf buffer)
    {
        super.encodeInto(buffer);
        buffer.writeInt(this.type.ordinal());

        try
        {
            NetworkUtil.encodeData(buffer, this.data);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void decodeInto(ByteBuf buffer)
    {
        super.decodeInto(buffer);
        this.type = EnumSimplePacketMP.valuesCached()[buffer.readInt()];

        if (this.type.getDecodeClasses().length > 0)
        {
            this.data = NetworkUtil.decodeData(this.type.getDecodeClasses(), buffer);
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void handleClientSide(EntityPlayer player)
    {
        /*if (player instanceof EntityPlayerSP)
        {
        }*/

        switch (this.type)
        {

        default:
            break;
        }
    }

    @Override
    public void handleServerSide(EntityPlayer player)
    {
        World world = player.worldObj;
        TileEntity tile;
        BlockPos pos;

        switch (this.type)
        {
        case S_FIRE_EXTINGUISH:
            pos = (BlockPos) this.data.get(0);
            world.playSoundEffect(pos.getX(), pos.getY(), pos.getZ(), "random.fizz", 0.5F, 2.6F + (world.rand.nextFloat() - world.rand.nextFloat()) * 0.8F);
            world.setBlockToAir(pos);
            break;
        case S_RECEIVER_BLOCK_GUIDE:
            tile = world.getTileEntity((BlockPos) this.data.get(0));

            if (tile instanceof TileEntityDarkEnergyReceiver)
            {
                TileEntityDarkEnergyReceiver receiver = (TileEntityDarkEnergyReceiver) tile;
                receiver.renderBlock = !receiver.renderBlock;
            }
            break;
        case S_RESPAWN_PLAYER_NETHER:
            if (world instanceof WorldServer && player instanceof EntityPlayerMP)
            {
                WorldServer worldOld = (WorldServer) world;
                EntityPlayerMP playerMP = (EntityPlayerMP) player;
                WorldServer worldNew = WorldDimensionHelper.getStartWorld(worldOld);
                BlockPos spawnPos = worldNew.getTopSolidOrLiquidBlock(worldNew.getSpawnPoint());
                TeleportHandler.setWarpDimension(playerMP, worldNew, spawnPos.getX(), spawnPos.getY(), spawnPos.getZ(), WorldUtil.getProviderForNameServer(WorldTickEventHandler.startedDimensionData.planetToBack).getDimensionId(), true);
                player.respawnPlayer();
                player.closeScreen();
            }
            break;
        case S_DISABLE_BLACK_HOLE:
            tile = world.getTileEntity((BlockPos) this.data.get(0));

            if (tile instanceof TileEntityBlackHoleStorage)
            {
                TileEntityBlackHoleStorage storage = (TileEntityBlackHoleStorage) tile;
                storage.disableBlackHole = !storage.disableBlackHole;
            }
            break;
        case S_USE_HOPPER:
            tile = world.getTileEntity((BlockPos) this.data.get(0));

            if (tile instanceof TileEntityBlackHoleStorage)
            {
                TileEntityBlackHoleStorage storage = (TileEntityBlackHoleStorage) tile;
                storage.useHopper = !storage.useHopper;
            }
            break;
        case S_CHANGE_COLLECT_MODE:
            tile = world.getTileEntity((BlockPos) this.data.get(0));

            if (tile instanceof TileEntityBlackHoleStorage)
            {
                TileEntityBlackHoleStorage storage = (TileEntityBlackHoleStorage) tile;

                if (storage.collectMode.equals("item"))
                {
                    storage.collectMode = "xp";
                }
                else
                {
                    storage.collectMode = "item";
                }
            }
            break;
        default:
            break;
        }
    }

    public static enum EnumSimplePacketMP
    {
        // SERVER
        S_FIRE_EXTINGUISH(Side.SERVER, BlockPos.class),
        S_RECEIVER_BLOCK_GUIDE(Side.SERVER, BlockPos.class),
        S_RESPAWN_PLAYER_NETHER(Side.SERVER),
        S_DISABLE_BLACK_HOLE(Side.SERVER, BlockPos.class),
        S_USE_HOPPER(Side.SERVER, BlockPos.class),
        S_CHANGE_COLLECT_MODE(Side.SERVER, BlockPos.class),

        ;
        // CLIENT
        //C_TELEPAD_SEND(Side.CLIENT);

        private Side targetSide;
        private Class[] decodeAs;
        private static EnumSimplePacketMP[] values = EnumSimplePacketMP.values();

        private EnumSimplePacketMP(Side targetSide, Class... decodeAs)
        {
            this.targetSide = targetSide;
            this.decodeAs = decodeAs;
        }

        public Side getTargetSide()
        {
            return this.targetSide;
        }

        public Class[] getDecodeClasses()
        {
            return this.decodeAs;
        }

        public static EnumSimplePacketMP[] valuesCached()
        {
            return EnumSimplePacketMP.values;
        }
    }
}