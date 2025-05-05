package kogasastudio.ashihara.registry;

import com.mojang.serialization.Codec;
import kogasastudio.ashihara.Ashihara;
import kogasastudio.ashihara.utils.ItemHoldAnimHandler;
import kogasastudio.ashihara.utils.PrePostSwingHandler;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.function.Supplier;

public class DataComponentTypes
{
    public static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES = DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES, Ashihara.MODID);

    public static final Supplier<AttachmentType<Integer>> GUIDEBOOK_READING_PAGE =  ATTACHMENT_TYPES.register("guidebook_reading_page", () -> AttachmentType.builder(() -> 0).serialize(Codec.INT).build());
    public static final Supplier<AttachmentType<ItemHoldAnimHandler>> ITEM_PLAYING_HOLDING_ANIM =  ATTACHMENT_TYPES.register("item_playing_holding_anim", () -> AttachmentType.builder(() -> ItemHoldAnimHandler.EMPTY).serialize(ItemHoldAnimHandler.CODEC).build());
    public static final Supplier<AttachmentType<PrePostSwingHandler>> PRE_SWING_REMAINING =  ATTACHMENT_TYPES.register("pre_swing_remaining", () -> AttachmentType.builder(() -> PrePostSwingHandler.EMPTY).serialize(PrePostSwingHandler.CODEC).build());
    public static final Supplier<AttachmentType<PrePostSwingHandler>> POST_SWING_REMAINING =  ATTACHMENT_TYPES.register("post_swing_remaining", () -> AttachmentType.builder(() -> PrePostSwingHandler.EMPTY).serialize(PrePostSwingHandler.CODEC).build());
}
