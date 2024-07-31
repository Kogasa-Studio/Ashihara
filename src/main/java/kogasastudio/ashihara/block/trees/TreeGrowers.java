package kogasastudio.ashihara.block.trees;

import kogasastudio.ashihara.Ashihara;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.grower.TreeGrower;

import java.util.Optional;

public class TreeGrowers
{
    public static final TreeGrower CHERRY_BLOSSOM = new TreeGrower
    (
        "cherry_blossom",
        Optional.of(ResourceKey.create(Registries.CONFIGURED_FEATURE, ResourceLocation.fromNamespaceAndPath(Ashihara.MODID, "great_cherry_tree"))),
        Optional.of(ResourceKey.create(Registries.CONFIGURED_FEATURE, ResourceLocation.fromNamespaceAndPath(Ashihara.MODID, "great_cherry_tree"))),
        Optional.empty()
    );

    public static final TreeGrower RED_MAPLE = new TreeGrower
    (
    "red_maple",
    Optional.of(ResourceKey.create(Registries.CONFIGURED_FEATURE, ResourceLocation.fromNamespaceAndPath(Ashihara.MODID, "red_maple_tree"))),
    Optional.of(ResourceKey.create(Registries.CONFIGURED_FEATURE, ResourceLocation.fromNamespaceAndPath(Ashihara.MODID, "red_maple_tree"))),
    Optional.empty()
    );
}
