package kogasastudio.ashihara.network;

import io.netty.buffer.ByteBuf;
import kogasastudio.ashihara.Ashihara;
import kogasastudio.ashihara.utils.ClientUtil;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record OpenGuidebookPacket(String id) implements CustomPacketPayload
{
    public static final Type<OpenGuidebookPacket> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(Ashihara.MODID, "open_guidebook_packet"));

    @Override
    public Type<? extends CustomPacketPayload> type()
    {
        return TYPE;
    }

    public static final StreamCodec<ByteBuf, OpenGuidebookPacket> STREAM_CODEC = StreamCodec.composite(ByteBufCodecs.STRING_UTF8, OpenGuidebookPacket::id, OpenGuidebookPacket::new);

    public static class ClientHandler
    {
        public static void handle(OpenGuidebookPacket packet, final IPayloadContext context)
        {
            ClientUtil.setGuideBookScreen(context.player());
        }
    }
}
