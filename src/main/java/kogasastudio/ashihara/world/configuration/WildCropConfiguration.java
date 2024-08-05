package kogasastudio.ashihara.world.configuration;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

public record WildCropConfiguration(int tries, int xzSpread, int ySpread, Holder<PlacedFeature> primaryFeature, Holder<PlacedFeature> secondaryFeature) implements FeatureConfiguration
{
    public static final Codec<WildCropConfiguration> CODEC = RecordCodecBuilder.create
    (
        c -> c.group
        (
            ExtraCodecs.POSITIVE_INT.fieldOf("tries").forGetter(WildCropConfiguration::tries),
            ExtraCodecs.NON_NEGATIVE_INT.fieldOf("xz_spread").forGetter(WildCropConfiguration::xzSpread),
            ExtraCodecs.NON_NEGATIVE_INT.fieldOf("y_spread").forGetter(WildCropConfiguration::ySpread),
            PlacedFeature.CODEC.fieldOf("primary_feature").forGetter(WildCropConfiguration::primaryFeature),
            PlacedFeature.CODEC.fieldOf("secondary_feature").forGetter(WildCropConfiguration::secondaryFeature)
        ).apply(c, WildCropConfiguration::new)
    );

    public int tries() {return tries;}
    public int xzSpread() {return xzSpread;}
    public int ySpread() {return ySpread;}
    public Holder<PlacedFeature> primaryFeature() {return primaryFeature;}
    public Holder<PlacedFeature> secondaryFeature() {return secondaryFeature;}
}
