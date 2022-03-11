package kogasastudio.ashihara.utils;

import kogasastudio.ashihara.Ashihara;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.Tags;

public class AshiharaTags
{
    public static final Tags.IOptionalNamedTag<Item> MASHABLE = tag("mashable");
    public static final Tags.IOptionalNamedTag<Item> CEREALS = tag("cereals");
    public static final Tags.IOptionalNamedTag<Item> CEREAL_PROCESSED = tag("cereal_processed");
    public static final Tags.IOptionalNamedTag<Item> SAUCE_MATERIALS = tag("sauce_materials");

    public static final Tags.IOptionalNamedTag<Block> ADVANCED_FENCES = tagBlock("advanced_fences");
    public static final Tags.IOptionalNamedTag<Block> FENCE_EXPANSIONS = tagBlock("fence_expansions");

    private static Tags.IOptionalNamedTag<Item> tag(String name)
    {
        return ItemTags.createOptional(new ResourceLocation(Ashihara.MODID, name));
    }

    private static Tags.IOptionalNamedTag<Block> tagBlock(String name)
    {
        return BlockTags.createOptional(new ResourceLocation(Ashihara.MODID, name));
    }
}
