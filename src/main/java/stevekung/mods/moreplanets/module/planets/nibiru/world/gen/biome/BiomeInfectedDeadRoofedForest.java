package stevekung.mods.moreplanets.module.planets.nibiru.world.gen.biome;

import java.util.Random;

import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenAbstractTree;
import stevekung.mods.moreplanets.core.config.ConfigManagerMP;
import stevekung.mods.moreplanets.module.planets.nibiru.blocks.NibiruBlocks;
import stevekung.mods.moreplanets.module.planets.nibiru.world.gen.feature.WorldGenInfectedCanopyTree;
import stevekung.mods.moreplanets.module.planets.nibiru.world.gen.feature.WorldGenInfectedTree;
import stevekung.mods.moreplanets.module.planets.nibiru.world.gen.feature.WorldGenInfectedVinesDirt;

public class BiomeInfectedDeadRoofedForest extends BiomeNibiru
{
    public BiomeInfectedDeadRoofedForest()
    {
        super(ConfigManagerMP.idBiomeInfectedDeadRoofedForest);
        this.enableRain = true;
        this.topBlock = NibiruBlocks.INFECTED_GRASS.getDefaultState();
        this.fillerBlock = NibiruBlocks.INFECTED_DIRT.getDefaultState();
        this.stoneBlock = NibiruBlocks.NIBIRU_BLOCK.getDefaultState();
        this.setTemperatureRainfall(0.7F, 0.8F);
        this.getBiomeDecorator().infectedTreesPerChunk = 5;
        this.getBiomeDecorator().infectedTallGrassPerChunk = 4;
        this.getBiomeDecorator().orangeBushPerChunk = 3;
        this.getBiomeDecorator().reedsPerChunk = 10;
        this.theBiomeDecorator.treesPerChunk = -999;
    }

    @Override
    public WorldGenAbstractTree genBigTreeChance(Random rand)
    {
        if (rand.nextInt(8) < 1)
        {
            return new WorldGenInfectedCanopyTree(false);
        }
        else if (rand.nextInt(20) == 0)
        {
            return rand.nextInt(5) == 0 ? new WorldGenInfectedCanopyTree(true) : new WorldGenInfectedTree(true, NibiruBlocks.NIBIRU_LOG, 0, NibiruBlocks.NIBIRU_LEAVES, 0);
        }
        else
        {
            return new WorldGenInfectedTree(false, NibiruBlocks.NIBIRU_LOG, 0, NibiruBlocks.NIBIRU_LEAVES, 0);
        }
    }

    @Override
    public void decorate(World world, Random rand, BlockPos pos)
    {
        for (int i = 0; i < 4; ++i)
        {
            for (int j = 0; j < 4; ++j)
            {
                int k = i * 4 + 1 + 8 + rand.nextInt(3);
                int l = j * 4 + 1 + 8 + rand.nextInt(3);
                BlockPos blockpos = world.getHeight(pos.add(k, 0, l));

                WorldGenAbstractTree worldgenabstracttree = this.genBigTreeChance(rand);

                if (worldgenabstracttree.generate(world, rand, blockpos))
                {
                    worldgenabstracttree.func_180711_a(world, rand, blockpos);
                }
            }
        }
        if (rand.nextInt(25) == 0)
        {
            new WorldGenInfectedVinesDirt().generate(world, rand, pos.add(rand.nextInt(16) + 8, rand.nextInt(256), rand.nextInt(16) + 8));
        }
        super.decorate(world, rand, pos);
    }
}