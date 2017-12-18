package stevekung.mods.moreplanets.init;

import static net.minecraftforge.common.BiomeDictionary.Type.*;

import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biome.BiomeProperties;
import stevekung.mods.moreplanets.core.config.ConfigManagerMP;
import stevekung.mods.moreplanets.module.planets.chalos.world.gen.biome.BiomeChalosHills;
import stevekung.mods.moreplanets.module.planets.chalos.world.gen.biome.BiomeChalosPlains;
import stevekung.mods.moreplanets.module.planets.chalos.world.gen.biome.BiomeSlimelyWasteland;
import stevekung.mods.moreplanets.module.planets.nibiru.world.gen.biome.*;
import stevekung.mods.moreplanets.util.helper.CommonRegisterHelper;
import stevekung.mods.moreplanets.util.world.gen.biome.BiomeBaseMP;

public class MPBiomes
{
    public static Biome DIONA = new BiomeBaseMP(new BiomeProperties("Diona").setRainfall(0.0F));
    public static Biome CHALOS_PLAINS = new BiomeChalosPlains(new BiomeProperties("Chalos Plains").setTemperature(0.8F).setRainfall(0.4F).setBaseHeight(0.125F).setHeightVariation(0.05F));
    public static Biome CHALOS_HILLS = new BiomeChalosHills(new BiomeProperties("Chalos Hills").setTemperature(0.2F).setRainfall(0.3F).setBaseHeight(1.0F).setHeightVariation(0.5F));
    public static Biome SLIMELY_WASTELAND = new BiomeSlimelyWasteland(new BiomeProperties("Slimely Wasteland").setBaseHeight(0.2F).setHeightVariation(0.2F));
    public static Biome INFECTED_PLAINS = new BiomeInfectedPlains(new BiomeProperties("Infected Plains").setTemperature(0.8F).setRainfall(0.4F).setBaseHeight(0.125F).setHeightVariation(0.05F));
    public static Biome INFECTED_DEAD_SAVANNA = new BiomeInfectedDeadSavanna(new BiomeProperties("Infected Dead Savanna").setRainDisabled().setRainfall(0.0F).setTemperature(1.2F).setBaseHeight(0.125F).setHeightVariation(0.05F));
    public static Biome INFECTED_DESERT = new BiomeInfectedDesert(new BiomeProperties("Infected Desert").setRainDisabled().setRainfall(0.0F).setTemperature(2.0F).setBaseHeight(0.125F).setHeightVariation(0.05F));
    public static Biome INFECTED_RIVER = new BiomeInfectedRiver(new BiomeProperties("Infected River").setBaseHeight(-0.5F).setHeightVariation(0.0F));
    public static Biome INFECTED_OCEAN = new BiomeInfectedOcean(new BiomeProperties("Infected Ocean").setBaseHeight(-1.0F).setHeightVariation(0.1F));
    public static Biome INFECTED_FOREST = new BiomeInfectedForest(new BiomeProperties("Infected Forest").setTemperature(0.7F).setRainfall(0.8F));
    public static Biome INFECTED_DEEP_OCEAN = new BiomeInfectedDeepOcean(new BiomeProperties("Infected Deep Ocean").setBaseHeight(-1.8F).setHeightVariation(0.1F));
    public static Biome INFECTED_DEAD_ROOFED_FOREST = new BiomeInfectedDeadRoofedForest(new BiomeProperties("Infected Dead Roofed Forest").setTemperature(0.7F).setRainfall(0.8F));
    public static Biome INFECTED_EXTREME_HILLS = new BiomeInfectedExtremeHills(new BiomeProperties("Infected Extreme Hills").setTemperature(0.2F).setRainfall(0.3F).setBaseHeight(1.0F).setHeightVariation(0.5F));
    public static Biome INFECTED_SWAMPLAND = new BiomeInfectedSwampland(new BiomeProperties("Infected Swampland").setTemperature(0.8F).setRainfall(0.9F).setBaseHeight(-0.2F).setHeightVariation(0.1F));
    public static Biome INFECTED_DEAD_TAIGA = new BiomeInfectedDeadTaiga(new BiomeProperties("Infected Dead Taiga").setBaseHeight(0.2F).setHeightVariation(0.2F).setTemperature(0.25F).setRainfall(0.8F));
    public static Biome INFECTED_JUNGLE = new BiomeInfectedJungle(new BiomeProperties("Infected Jungle").setTemperature(0.95F).setRainfall(0.9F));
    public static Biome INFECTED_ICE_PLAINS = new BiomeInfectedIcePlains(new BiomeProperties("Infected Ice Plains").setSnowEnabled().setTemperature(0.0F).setRainfall(0.5F).setBaseHeight(0.125F).setHeightVariation(0.05F));
    public static Biome GREEN_VEIN = new BiomeGreenVein(new BiomeProperties("Green Vein").setTemperature(0.9F).setRainfall(1.0F).setBaseHeight(0.125F).setHeightVariation(0.05F));

    public static void init()
    {
        CommonRegisterHelper.registerBiome(ConfigManagerMP.idBiomeDiona, "diona", MPBiomes.DIONA, COLD, DEAD, DRY);
        CommonRegisterHelper.registerBiome(ConfigManagerMP.idBiomeChalosPlains, "chalos_plains", MPBiomes.CHALOS_PLAINS, PLAINS);
        CommonRegisterHelper.registerBiome(ConfigManagerMP.idBiomeChalosHills, "chalos_hills", MPBiomes.CHALOS_HILLS, MOUNTAIN, HILLS);
        CommonRegisterHelper.registerBiome(ConfigManagerMP.idBiomeSlimelyWasteland, "slimely_wasteland", MPBiomes.SLIMELY_WASTELAND, WASTELAND, DRY);
        CommonRegisterHelper.registerBiome(ConfigManagerMP.idBiomeInfectedPlains, "infected_plains", MPBiomes.INFECTED_PLAINS, PLAINS, DEAD);
        CommonRegisterHelper.registerBiome(ConfigManagerMP.idBiomeInfectedDeadSavanna, "infected_dead_savanna", MPBiomes.INFECTED_DEAD_SAVANNA, HOT, SAVANNA, PLAINS, SPARSE, DEAD);
        CommonRegisterHelper.registerBiome(ConfigManagerMP.idBiomeInfectedDesert, "infected_desert", MPBiomes.INFECTED_DESERT, HOT, DRY, SANDY, DEAD);
        CommonRegisterHelper.registerBiome(ConfigManagerMP.idBiomeInfectedRiver, "infected_river", MPBiomes.INFECTED_RIVER, RIVER, DEAD);
        CommonRegisterHelper.registerBiome(ConfigManagerMP.idBiomeInfectedOcean, "infected_ocean", MPBiomes.INFECTED_OCEAN, OCEAN, DEAD);
        CommonRegisterHelper.registerBiome(ConfigManagerMP.idBiomeInfectedForest, "infected_forest", MPBiomes.INFECTED_FOREST, FOREST, DEAD);
        CommonRegisterHelper.registerBiome(ConfigManagerMP.idBiomeInfectedDeepOcean, "infected_deep_ocean", MPBiomes.INFECTED_DEEP_OCEAN, OCEAN, DEAD);
        CommonRegisterHelper.registerBiome(ConfigManagerMP.idBiomeInfectedDeadRoofedForest, "infected_dead_roofed_forest", MPBiomes.INFECTED_DEAD_ROOFED_FOREST, SPOOKY, DENSE, FOREST, DEAD);
        CommonRegisterHelper.registerBiome(ConfigManagerMP.idBiomeInfectedExtremeHills, "infected_extreme_hills", MPBiomes.INFECTED_EXTREME_HILLS, MOUNTAIN, HILLS, DEAD);
        CommonRegisterHelper.registerBiome(ConfigManagerMP.idBiomeInfectedSwampland, "infected_swampland", MPBiomes.INFECTED_SWAMPLAND, WET, SWAMP, DEAD);
        CommonRegisterHelper.registerBiome(ConfigManagerMP.idBiomeInfectedDeadTaiga, "infected_dead_taiga", MPBiomes.INFECTED_DEAD_TAIGA, COLD, CONIFEROUS, DEAD);
        CommonRegisterHelper.registerBiome(ConfigManagerMP.idBiomeInfectedJungle, "infected_jungle", MPBiomes.INFECTED_JUNGLE, HOT, WET, DENSE, JUNGLE, DEAD);
        CommonRegisterHelper.registerBiome(ConfigManagerMP.idBiomeInfectedIcePlains, "infected_ice_plains", MPBiomes.INFECTED_ICE_PLAINS, COLD, SNOWY, WASTELAND, DEAD);
        CommonRegisterHelper.registerBiome(ConfigManagerMP.idBiomeGreenVein, "green_vein", MPBiomes.GREEN_VEIN, FOREST, RARE, MAGICAL);
    }
}