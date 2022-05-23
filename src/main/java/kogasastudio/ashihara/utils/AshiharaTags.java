package kogasastudio.ashihara.utils;

import kogasastudio.ashihara.Ashihara;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

public class AshiharaTags {
    public static final TagKey<Item> AXE = tagForge("axe");
    public static final TagKey<Item> KNIFE = tagForge("knife");

    public static final TagKey<Item> MASHABLE = tag("mashable");
    public static final TagKey<Item> CEREALS = tag("cereals");
    public static final TagKey<Item> CEREAL_PROCESSED = tag("cereal_processed");
    public static final TagKey<Item> SAUCE_MATERIALS = tag("sauce_materials");

    public static final TagKey<Block> ADVANCED_FENCES = tagBlock("advanced_fences");
    public static final TagKey<Block> FENCE_EXPANSIONS = tagBlock("fence_expansions");

    private static TagKey<Item> tag(String name) {
        return ItemTags.create(new ResourceLocation(Ashihara.MODID, name));
    }

    private static TagKey<Item> tagForge(String name) {
        return ItemTags.create(new ResourceLocation("forge", name));
    }

    private static TagKey<Block> tagBlock(String name) {
        return BlockTags.create(new ResourceLocation(Ashihara.MODID, name));
    }

    private static TagKey<Block> tagForgeBlock(String name) {
        return BlockTags.create(new ResourceLocation("forge", name));
    }
}
