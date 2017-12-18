package stevekung.mods.moreplanets.module.planets.nibiru.world.gen.structure;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;

import com.google.common.collect.Lists;

import net.minecraft.entity.monster.EntityWitch;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.biome.BiomeGenBase.SpawnListEntry;
import net.minecraft.world.gen.structure.MapGenStructure;
import net.minecraft.world.gen.structure.MapGenStructureIO;
import net.minecraft.world.gen.structure.StructureComponent;
import net.minecraft.world.gen.structure.StructureStart;
import stevekung.mods.moreplanets.init.MPBiomes;
import stevekung.mods.moreplanets.util.MPLog;

public class MapGenNibiruJungleTemple extends MapGenStructure
{
    private List<SpawnListEntry> scatteredFeatureSpawnList;
    private int maxDistanceBetweenScatteredFeatures;
    private int minDistanceBetweenScatteredFeatures;

    static
    {
        MapGenStructureIO.registerStructure(Start.class, "NibiruJungleTemple");
        MapGenStructureIO.registerStructureComponent(StructureNibiruJungleTemplePieces.JungleTemple.class, "NibiruJungleTemple");
    }

    public MapGenNibiruJungleTemple()
    {
        this.scatteredFeatureSpawnList = Lists.newArrayList();
        this.maxDistanceBetweenScatteredFeatures = 32;
        this.minDistanceBetweenScatteredFeatures = 8;
        this.scatteredFeatureSpawnList.add(new SpawnListEntry(EntityWitch.class, 1, 1, 1));
    }

    public MapGenNibiruJungleTemple(Map<String, String> map)
    {
        this();

        for (Entry<String, String> entry : map.entrySet())
        {
            if (entry.getKey().equals("distance"))
            {
                this.maxDistanceBetweenScatteredFeatures = MathHelper.parseIntWithDefaultAndMax(entry.getValue(), this.maxDistanceBetweenScatteredFeatures, this.minDistanceBetweenScatteredFeatures + 1);
            }
        }
    }

    @Override
    public String getStructureName()
    {
        return "NibiruJungleTemple";
    }

    @Override
    protected boolean canSpawnStructureAtCoords(int chunkX, int chunkZ)
    {
        int i = chunkX;
        int j = chunkZ;

        if (chunkX < 0)
        {
            chunkX -= this.maxDistanceBetweenScatteredFeatures - 1;
        }
        if (chunkZ < 0)
        {
            chunkZ -= this.maxDistanceBetweenScatteredFeatures - 1;
        }

        int k = chunkX / this.maxDistanceBetweenScatteredFeatures;
        int l = chunkZ / this.maxDistanceBetweenScatteredFeatures;
        Random random = this.worldObj.setRandomSeed(k, l, 14357617);
        k = k * this.maxDistanceBetweenScatteredFeatures;
        l = l * this.maxDistanceBetweenScatteredFeatures;
        k = k + random.nextInt(this.maxDistanceBetweenScatteredFeatures - this.minDistanceBetweenScatteredFeatures);
        l = l + random.nextInt(this.maxDistanceBetweenScatteredFeatures - this.minDistanceBetweenScatteredFeatures);

        if (i == k && j == l)
        {
            BiomeGenBase biomegenbase = this.worldObj.getWorldChunkManager().getBiomeGenerator(new BlockPos(i * 16 + 8, 0, j * 16 + 8));

            if (biomegenbase == null)
            {
                return false;
            }
            if (biomegenbase == MPBiomes.INFECTED_JUNGLE)
            {
                return true;
            }
        }
        return false;
    }

    @Override
    protected StructureStart getStructureStart(int chunkX, int chunkZ)
    {
        return new Start(this.worldObj, this.rand, chunkX, chunkZ);
    }

    public boolean canMobSpawn(BlockPos pos)
    {
        StructureStart structurestart = this.func_175797_c(pos);

        if (structurestart != null && structurestart instanceof Start && !structurestart.getComponents().isEmpty())
        {
            StructureComponent structurecomponent = structurestart.getComponents().getFirst();
            return structurecomponent instanceof StructureNibiruJungleTemplePieces.JungleTemple;
        }
        else
        {
            return false;
        }
    }

    public List<SpawnListEntry> getSpawnList()
    {
        return this.scatteredFeatureSpawnList;
    }

    public static class Start extends StructureStart
    {
        public Start() {}

        public Start(World world, Random rand, int chunkX, int chunkZ)
        {
            super(chunkX, chunkZ);
            MPLog.debug("Generate jungle temple at %s %s", chunkX * 16, chunkZ * 16);
            BiomeGenBase biomegenbase = world.getBiomeGenForCoords(new BlockPos(chunkX * 16 + 8, 0, chunkZ * 16 + 8));

            if (biomegenbase == MPBiomes.INFECTED_JUNGLE)
            {
                StructureNibiruJungleTemplePieces.JungleTemple component = new StructureNibiruJungleTemplePieces.JungleTemple(rand, chunkX * 16, chunkZ * 16);
                this.components.add(component);
            }
            this.updateBoundingBox();
        }
    }
}