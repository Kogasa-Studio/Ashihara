package kogasastudio.ashihara.registry;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import kogasastudio.ashihara.Ashihara;
import kogasastudio.ashihara.utils.ItemHoldAnimHandler;
import kogasastudio.ashihara.utils.PrePostSwingHandler;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.function.Supplier;

public class DataAttachmentTypes
{
    public static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES = DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES, Ashihara.MODID);

    public static final Supplier<AttachmentType<Integer>> GUIDEBOOK_READING_PAGE =  ATTACHMENT_TYPES.register("guidebook_reading_page", () -> AttachmentType.builder(() -> 0).serialize(Codec.INT.fieldOf("value")).build());
    public static final Supplier<AttachmentType<ItemHoldAnimHandler>> ITEM_PLAYING_HOLDING_ANIM =  ATTACHMENT_TYPES.register("item_playing_holding_anim", () -> AttachmentType.builder(() -> ItemHoldAnimHandler.EMPTY).serialize(MapCodec.assumeMapUnsafe(ItemHoldAnimHandler.CODEC)).build());
    public static final Supplier<AttachmentType<PrePostSwingHandler>> PRE_SWING_REMAINING =  ATTACHMENT_TYPES.register("pre_swing_remaining", () -> AttachmentType.builder(() -> PrePostSwingHandler.EMPTY).serialize(MapCodec.assumeMapUnsafe(PrePostSwingHandler.CODEC)).build());
    public static final Supplier<AttachmentType<PrePostSwingHandler>> POST_SWING_REMAINING =  ATTACHMENT_TYPES.register("post_swing_remaining", () -> AttachmentType.builder(() -> PrePostSwingHandler.EMPTY).serialize(MapCodec.assumeMapUnsafe(PrePostSwingHandler.CODEC)).build());

    public static final Supplier<AttachmentType<Integer>> GRID_SNAP_STEP =
        ATTACHMENT_TYPES.register(
            "grid_snap_step",
            () -> AttachmentType.builder(() -> 4) // GridSnapHelper.GRID_1PX, avoids circular dependency
                .serialize(Codec.INT.fieldOf("value"))
                .build()
        );

    public static final Supplier<AttachmentType<kogasastudio.ashihara.network.HagoromoFlightData>> HAGOROMO_FLIGHT =
        ATTACHMENT_TYPES.register(
            "hagoromo_flight",
            () -> AttachmentType.builder(() -> new kogasastudio.ashihara.network.HagoromoFlightData(false, false, 0, 0))
                .serialize(kogasastudio.ashihara.network.HagoromoFlightData.MAP_CODEC)
                .build()
        );

    public static final Supplier<AttachmentType<Boolean>> EATING_MODE =
        ATTACHMENT_TYPES.register(
            "eating_mode",
            () -> AttachmentType.builder(() -> false)
                .serialize(Codec.BOOL.fieldOf("value"))
                .build()
        );
}