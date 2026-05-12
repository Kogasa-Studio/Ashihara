package kogasastudio.ashihara.utils;

import com.mojang.serialization.Codec;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;

public class ItemCodecs
{
    public static final Codec<Item> ITEM_CODEC = Identifier.CODEC.xmap(BuiltInRegistries.ITEM::getValue, BuiltInRegistries.ITEM::getKey);
}
