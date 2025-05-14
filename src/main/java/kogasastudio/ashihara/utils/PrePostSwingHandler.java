package kogasastudio.ashihara.utils;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;

public class PrePostSwingHandler
{
    private ItemStack item;
    private InteractionHand hand;
    private boolean use;
    private float ticksRemain;

    public static final PrePostSwingHandler EMPTY = new PrePostSwingHandler(ItemStack.EMPTY, InteractionHand.MAIN_HAND, 0f);
    public static final Codec<PrePostSwingHandler> CODEC = RecordCodecBuilder.create
    (
        instance -> instance.group
        (
            ItemStack.CODEC.fieldOf("item").forGetter(PrePostSwingHandler::getItem),
            Codec.STRING.fieldOf("hand").xmap(InteractionHand::valueOf, InteractionHand::name).forGetter(PrePostSwingHandler::getHand),
            Codec.BOOL.fieldOf("use").forGetter(PrePostSwingHandler::isUse),
            Codec.FLOAT.fieldOf("duration").forGetter(PrePostSwingHandler::getTicksRemain)
        ).apply(instance, PrePostSwingHandler::new)
    );


    public PrePostSwingHandler(ItemStack item, InteractionHand hand, float duration)
    {
        this(item, hand, false, duration);
    }

    public PrePostSwingHandler(ItemStack item, InteractionHand hand, boolean use, float duration)
    {
        this.item = item;
        this.hand = hand;
        this.use = use;
        this.ticksRemain = duration;
    }

    public void tick()
    {
        this.ticksRemain = ticksRemain <= 0 ? 0 : ticksRemain - 1;
    }

    public ItemStack getItem()
    {
        return item;
    }

    public InteractionHand getHand()
    {
        return hand;
    }

    public boolean isUse() {return use;}

    public void setUse(boolean use) {this.use = use;}

    public float getTicksRemain()
    {
        return ticksRemain;
    }
}
