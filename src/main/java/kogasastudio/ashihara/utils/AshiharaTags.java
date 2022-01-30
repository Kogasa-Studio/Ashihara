package kogasastudio.ashihara.utils;

import kogasastudio.ashihara.Ashihara;
import net.minecraft.item.Item;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.Tag;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.Tags;
import net.minecraftforge.registries.DeferredRegister;

public class AshiharaTags
{
    public static final Tags.IOptionalNamedTag<Item> MASHABLE = tag("mashable");
    public static final Tags.IOptionalNamedTag<Item> CEREALS = tag("cereals");
    public static final Tags.IOptionalNamedTag<Item> CEREAL_PROCESSED = tag("cereal_processed");
    public static final Tags.IOptionalNamedTag<Item> SAUCE_MATERIALS = tag("sauce_materials");

    private static Tags.IOptionalNamedTag<Item> tag(String name)
    {
        return ItemTags.createOptional(new ResourceLocation(Ashihara.MODID, name));
    }
}
