package stevekung.mods.moreplanets.module.planets.nibiru.world.gen.biome;

import java.util.Random;

import net.minecraft.block.material.Material;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.gen.feature.WorldGenAbstractTree;
import stevekung.mods.moreplanets.core.config.ConfigManagerMP;
import stevekung.mods.moreplanets.module.planets.nibiru.blocks.NibiruBlocks;
import stevekung.mods.moreplanets.module.planets.nibiru.world.gen.feature.WorldGenInfectedSwampTree;
import stevekung.mods.moreplanets.module.planets.nibiru.world.gen.feature.WorldGenInfectedVinesDirt;

public class BiomeInfectedSwampland extends BiomeNibiru
{
    public BiomeInfectedSwampland()
    {
        super(ConfigManagerMP.idBiomeInfectedSwampland);
        this.enableRain = true;
        this.topBlock = NibiruBlocks.INFECTED_GRASS.getDefaultState();
        this.fillerBlock = NibiruBlocks.INFECTED_DIRT.getDefaultState();
        this.stoneBlock = NibiruBlocks.NIBIRU_BLOCK.getDefaultState();
        this.getBiomeDecorator().infectedTreesPerChunk = 2;
        this.getBiomeDecorator().infectedTallGrassPerChunk = 12;
        this.getBiomeDecorator().waterlilyPerChunk = 4;
        this.getBiomeDecorator().whiteTailPerChunk = 4;
        this.getBiomeDecorator().clayPerChunk = 1;
        this.getBiomeDecorator().reedsPerChunk = 16;
        this.getBiomeDecorator().sandPerChunk2 = 0;
        this.getBiomeDecorator().sandPerChunk = 0;
        this.setTemperatureRainfall(0.8F, 0.9F);
        this.setHeight(new Height(-0.2F, 0.1F));
    }

    @Override
    public void decorate(World world, Random rand, BlockPos pos)
    {
        if (rand.nextInt(25) == 0)
        {
            new WorldGenInfectedVinesDirt().generate(world, rand, pos.add(rand.nextInt(16) + 8, rand.nextInt(256), rand.nextInt(16) + 8));
        }
        super.decorate(world, rand, pos);
    }

    @Override
    public WorldGenAbstractTree genBigTreeChance(Random rand)
    {
        return rand.nextInt(20) == 0 ? new WorldGenInfectedSwampTree(false) : new WorldGenInfectedSwampTree(true);
    }

    @Override
    public void genTerrainBlocks(World world, Random rand, ChunkPrimer chunkPrimer, int chunkX, int chunkZ, double stoneNoise)
    {
        double d0 = BiomeGenBase.GRASS_COLOR_NOISE.func_151601_a(chunkX * 0.25D, chunkZ * 0.25D);

        if (d0 > 0.0D)
        {
            int i = chunkX & 15;
            int j = chunkZ & 15;

            for (int k = 255; k >= 0; --k)
            {
                if (chunkPrimer.getBlockState(j, k, i).getBlock().getMaterial() != Material.air)
                {
                    if (k == 62 && chunkPrimer.getBlockState(j, k, i).getBlock() != NibiruBlocks.INFECTED_WATER_FLUID_BLOCK)
                    {
                        chunkPrimer.setBlockState(j, k, i, NibiruBlocks.INFECTED_WATER_FLUID_BLOCK.getDefaultState());

                        if (d0 < 0.12D)
                        {
                            chunkPrimer.setBlockState(j, k + 1, i, NibiruBlocks.SPORELILY.getDefaultState());
                        }
                    }
                    break;
                }
            }
        }
        this.genPlanetTerrain(world, rand, chunkPrimer, chunkX, chunkZ, stoneNoise);
    }
}