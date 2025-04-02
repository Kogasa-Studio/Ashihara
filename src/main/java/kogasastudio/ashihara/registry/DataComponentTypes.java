package kogasastudio.ashihara.registry;

import com.mojang.serialization.Codec;
import kogasastudio.ashihara.Ashihara;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.function.Supplier;

public class DataComponentTypes
{
    public static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES = DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES, Ashihara.MODID);

    public static final Supplier<AttachmentType<Integer>> GUIDEBOOK_READING_PAGE =  ATTACHMENT_TYPES.register("guidebook_reading_page", () -> AttachmentType.builder(() -> 0).serialize(Codec.INT).build());
}
