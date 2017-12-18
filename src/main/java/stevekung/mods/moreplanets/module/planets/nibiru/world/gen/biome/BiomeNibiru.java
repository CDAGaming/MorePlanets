package stevekung.mods.moreplanets.module.planets.nibiru.world.gen.biome;

import java.util.Random;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeDecorator;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.gen.feature.WorldGenAbstractTree;
import stevekung.mods.moreplanets.module.planets.nibiru.blocks.NibiruBlocks;
import stevekung.mods.moreplanets.module.planets.nibiru.world.gen.BiomeDecoratorNibiru;
import stevekung.mods.moreplanets.module.planets.nibiru.world.gen.feature.WorldGenInfectedBigTree;
import stevekung.mods.moreplanets.module.planets.nibiru.world.gen.feature.WorldGenInfectedTree;
import stevekung.mods.moreplanets.util.world.gen.biome.BiomeBaseMP;

public class BiomeNibiru extends BiomeBaseMP
{
    protected IBlockState stoneBlock;

    public BiomeNibiru(int id)
    {
        super(id);
        this.getBiomeDecorator().clayPerChunk = 1;
        this.getBiomeDecorator().sandPerChunk = 1;
        this.getBiomeDecorator().sandPerChunk2 = 3;
        this.getBiomeDecorator().pureHurbPerChunk = 4;
        this.theBiomeDecorator.treesPerChunk = -999;
        this.theBiomeDecorator.flowersPerChunk = -999;
        this.theBiomeDecorator.grassPerChunk = -999;
    }

    @Override
    public WorldGenAbstractTree genBigTreeChance(Random rand)
    {
        return rand.nextInt(10) == 0 ? new WorldGenInfectedBigTree(true, NibiruBlocks.NIBIRU_LOG, 0, NibiruBlocks.NIBIRU_LEAVES, 0) : new WorldGenInfectedTree(true, NibiruBlocks.NIBIRU_LOG, 0, NibiruBlocks.NIBIRU_LEAVES, 0);
    }

    @Override
    public BiomeDecorator createBiomeDecorator()
    {
        return new BiomeDecoratorNibiru();
    }

    protected BiomeDecoratorNibiru getBiomeDecorator()
    {
        return (BiomeDecoratorNibiru)this.theBiomeDecorator;
    }

    @Override
    public void genTerrainBlocks(World world, Random rand, ChunkPrimer chunkPrimer, int chunkX, int chunkZ, double noise)
    {
        this.genPlanetTerrain(world, rand, chunkPrimer, chunkX, chunkZ, noise);
    }

    protected void genPlanetTerrain(World world, Random rand, ChunkPrimer chunkPrimerIn, int chunkX, int chunkZ, double noise)
    {
        int i = world.getSeaLevel();
        IBlockState iblockstate = this.topBlock;
        IBlockState iblockstate1 = this.fillerBlock;
        int j = -1;
        int k = (int)(noise / 3.0D + 3.0D + rand.nextDouble() * 0.25D);
        int l = chunkX & 15;
        int i1 = chunkZ & 15;
        BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos();

        for (int j1 = 255; j1 >= 0; --j1)
        {
            if (j1 <= rand.nextInt(5))
            {
                chunkPrimerIn.setBlockState(i1, j1, l, Blocks.bedrock.getDefaultState());
            }
            else
            {
                IBlockState iblockstate2 = chunkPrimerIn.getBlockState(i1, j1, l);

                if (iblockstate2.getBlock().getMaterial() == Material.air)
                {
                    j = -1;
                }
                else if (iblockstate2.getBlock() == NibiruBlocks.NIBIRU_BLOCK)
                {
                    if (this.stoneBlock != null)
                    {
                        chunkPrimerIn.setBlockState(i1, j1, l, this.stoneBlock);
                    }
                    if (j == -1)
                    {
                        if (k <= 0)
                        {
                            iblockstate = null;
                            iblockstate1 = NibiruBlocks.NIBIRU_BLOCK.getDefaultState();
                        }
                        else if (j1 >= i - 4 && j1 <= i + 1)
                        {
                            iblockstate = this.topBlock;
                            iblockstate1 = this.fillerBlock;
                        }

                        if (j1 < i && (iblockstate == null || iblockstate.getBlock().getMaterial() == Material.air))
                        {
                            if (this.getFloatTemperature(blockpos$mutableblockpos.set(chunkX, j1, chunkZ)) < 0.15F)
                            {
                                iblockstate = NibiruBlocks.INFECTED_ICE.getDefaultState();
                            }
                            else
                            {
                                iblockstate = NibiruBlocks.INFECTED_WATER_FLUID_BLOCK.getDefaultState();
                            }
                        }

                        j = k;

                        if (j1 >= i - 1)
                        {
                            chunkPrimerIn.setBlockState(i1, j1, l, iblockstate);
                        }
                        else if (j1 < i - 7 - k)
                        {
                            iblockstate = null;
                            iblockstate1 = NibiruBlocks.NIBIRU_BLOCK.getDefaultState();
                            chunkPrimerIn.setBlockState(i1, j1, l, NibiruBlocks.INFECTED_GRAVEL.getDefaultState());
                        }
                        else
                        {
                            chunkPrimerIn.setBlockState(i1, j1, l, iblockstate1);
                        }
                    }
                    else if (j > 0)
                    {
                        --j;
                        chunkPrimerIn.setBlockState(i1, j1, l, iblockstate1);

                        if (j == 0 && iblockstate1.getBlock() == NibiruBlocks.INFECTED_SAND)
                        {
                            j = rand.nextInt(4) + Math.max(0, j1 - 63);
                            iblockstate1 = NibiruBlocks.NIBIRU_SANDSTONE.getDefaultState();
                        }
                    }
                }
            }
        }
    }
}