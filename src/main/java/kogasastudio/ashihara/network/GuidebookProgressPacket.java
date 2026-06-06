package kogasastudio.ashihara.network;

import io.netty.buffer.ByteBuf;
import kogasastudio.ashihara.Ashihara;
import kogasastudio.ashihara.client.models.geo.GuideBookModel;
import kogasastudio.ashihara.registry.DataAttachmentTypes;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record GuidebookProgressPacket(int UnlockProgress, int currentPage) implements CustomPacketPayload
{
    public static final Type<GuidebookProgressPacket> TYPE = new Type<>(Identifier.fromNamespaceAndPath(Ashihara.MODID, "guidebook_progress_packet"));

    public static final StreamCodec<ByteBuf, GuidebookProgressPacket> STREAM_CODEC = StreamCodec.composite
    (
        ByteBufCodecs.VAR_INT, GuidebookProgressPacket::UnlockProgress,
        ByteBufCodecs.VAR_INT, GuidebookProgressPacket::currentPage,
        GuidebookProgressPacket::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type()
    {
        return TYPE;
    }

    public static class Handler
    {
        public static void handle(GuidebookProgressPacket packet, final IPayloadContext context)
        {
            context.enqueueWork(() -> context.player().setData(DataAttachmentTypes.GUIDEBOOK_READING_PAGE, Math.clamp(packet.currentPage(), 0, GuideBookModel.getTotalPages())));
        }
    }
}
