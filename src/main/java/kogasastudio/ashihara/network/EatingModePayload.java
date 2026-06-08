package kogasastudio.ashihara.network;

import io.netty.buffer.ByteBuf;
import kogasastudio.ashihara.Ashihara;
import kogasastudio.ashihara.registry.DataAttachmentTypes;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record EatingModePayload(boolean enabled) implements CustomPacketPayload
{
    public static final Type<EatingModePayload> TYPE =
        new Type<>(Identifier.fromNamespaceAndPath(Ashihara.MODID, "eating_mode"));

    @Override
    public Type<? extends CustomPacketPayload> type() { return TYPE; }

    public static final StreamCodec<ByteBuf, EatingModePayload> STREAM_CODEC = StreamCodec.composite(ByteBufCodecs.BOOL, EatingModePayload::enabled, EatingModePayload::new);

    public static class ServerHandler
    {
        public static void handle(EatingModePayload payload, IPayloadContext context)
        {
            context.player().setData(DataAttachmentTypes.EATING_MODE.get(), payload.enabled());
        }
    }
}