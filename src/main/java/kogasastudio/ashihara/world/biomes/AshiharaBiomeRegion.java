package kogasastudio.ashihara.world.biomes;

import com.mojang.datafixers.util.Pair;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.biome.Climate;
import terrablender.api.Region;
import terrablender.api.RegionType;

import java.util.function.Consumer;

import static net.minecraft.world.level.biome.Climate.Parameter.point;
import static net.minecraft.world.level.biome.Climate.Parameter.span;

/**
 * @author DustW
 **/
public class AshiharaBiomeRegion extends Region {
    public AshiharaBiomeRegion(ResourceLocation name, int weight) {
        super(name, RegionType.OVERWORLD, weight);
    }

    private final Climate.Parameter FULL_RANGE = Climate.Parameter.span(-1.0F, 1.0F);

    @Override
    public void addBiomes(Registry<Biome> registry, Consumer<Pair<Climate.ParameterPoint, ResourceKey<Biome>>> mapper) {
        /*addBiome
        (
            mapper,
            //参数在 https://yaossg.com/biome/1-introduction/1.4-properties-of-biome.html 可以看到是什么意思
            Climate.parameters
            (
                span(-0.41f, 4.5f),
                span(0.4f, 0.85f),
                span(0.21f, 0.35f),
                span(-0.2225f, 0.05f),
                point(1.0f),
                span(-0.4F, -0.26F),
                0
            ),
            BiomeRegistryHandler.JUNIOR_CHERRY_FOREST.getKey()
        );
        addBiome
        (
            mapper,
            Climate.parameters
            (
                span(-0.21f, 5.5f),
                span(0.3f, 0.85f),
                span(0.29f, 0.35f),
                span(-0.2225f, 0.05f),
                point(1.0f),
                span(-0.35F, -0.18F),
                0
            ),
            BiomeRegistryHandler.RED_MAPLE_FOREST.getKey()
        );*/
        addBiomeSimilar(mapper, Biomes.FLOWER_FOREST, BiomeRegistryHandler.JUNIOR_CHERRY_FOREST.getKey());
        addBiomeSimilar(mapper, Biomes.DARK_FOREST, BiomeRegistryHandler.RED_MAPLE_FOREST.getKey());
    }
}
