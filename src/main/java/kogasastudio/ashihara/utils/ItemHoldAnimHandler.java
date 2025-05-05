package kogasastudio.ashihara.utils;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;

public class ItemHoldAnimHandler
{
    private Item item;
    private InteractionHand hand;

    public static final ItemHoldAnimHandler EMPTY = new ItemHoldAnimHandler(Items.AIR, InteractionHand.MAIN_HAND);
    public static final Codec<ItemHoldAnimHandler> CODEC = RecordCodecBuilder.create
    (
        instance ->
        instance.group
        (
            ItemCodecs.ITEM_CODEC.fieldOf("item").forGetter(ItemHoldAnimHandler::getItem),
            Codec.STRING.fieldOf("hand").xmap(InteractionHand::valueOf, InteractionHand::name).forGetter(ItemHoldAnimHandler::getHand)
        ).apply(instance, ItemHoldAnimHandler::new)
    );

    public ItemHoldAnimHandler(Item item, InteractionHand hand)
    {
        this.item = item;
        this.hand = hand;
    }

    public InteractionHand getHand()
    {
        return hand;
    }

    public Item getItem()
    {
        return item;
    }
}
