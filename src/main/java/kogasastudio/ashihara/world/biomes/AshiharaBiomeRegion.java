package kogasastudio.ashihara.world.biomes;

import com.mojang.datafixers.util.Pair;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Climate;
import terrablender.api.Region;
import terrablender.api.RegionType;

import java.util.function.Consumer;

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
        addBiome(
                mapper,
                // todo 参数在 https://yaossg.com/biome/1-introduction/1.4-properties-of-biome.html 可以看到是什么意思
                new Climate.ParameterPoint(
                        FULL_RANGE,
                        FULL_RANGE,
                        FULL_RANGE,
                        FULL_RANGE,
                        FULL_RANGE,
                        FULL_RANGE,
                        0
                ),
                BiomeRegistryHandler.JUNIOR_CHERRY_FOREST.getKey()
        );
    }
}
