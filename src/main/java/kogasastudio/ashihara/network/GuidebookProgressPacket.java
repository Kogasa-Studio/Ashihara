package kogasastudio.ashihara.network;

import io.netty.buffer.ByteBuf;
import kogasastudio.ashihara.Ashihara;
import kogasastudio.ashihara.registry.DataComponentTypes;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record GuidebookProgressPacket(int UnlockProgress, int currentPage) implements CustomPacketPayload
{
    public static final Type<GuidebookProgressPacket> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(Ashihara.MODID, "guidebook_progress_packet"));

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

    public static class ServerHandler
    {
        public static void handle(GuidebookProgressPacket packet, final IPayloadContext context)
        {
            context.enqueueWork(() -> context.player().setData(DataComponentTypes.GUIDEBOOK_READING_PAGE, packet.currentPage()));
        }
    }
}
