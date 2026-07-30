package kogasastudio.ashihara.network;

import io.netty.buffer.ByteBuf;
import kogasastudio.ashihara.Ashihara;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record HagoromoFlightSyncPacket(boolean hasFlight, boolean hagoromoActive, int airborneTicks, int cooldownTicks) implements CustomPacketPayload
{
    public static final Type<HagoromoFlightSyncPacket> TYPE =
        new Type<>(Identifier.fromNamespaceAndPath(Ashihara.MODID, "hagoromo_flight_sync"));

    public static final StreamCodec<ByteBuf, HagoromoFlightSyncPacket> STREAM_CODEC = StreamCodec.composite(
        ByteBufCodecs.BOOL, HagoromoFlightSyncPacket::hasFlight,
        ByteBufCodecs.BOOL, HagoromoFlightSyncPacket::hagoromoActive,
        ByteBufCodecs.INT, HagoromoFlightSyncPacket::airborneTicks,
        ByteBufCodecs.INT, HagoromoFlightSyncPacket::cooldownTicks,
        HagoromoFlightSyncPacket::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() { return TYPE; }

    public static class ClientHandler
    {
        public static void handle(HagoromoFlightSyncPacket packet, IPayloadContext context)
        {
            context.enqueueWork(() ->
            {
                var player = context.player();
                if (player == null) return;
                var data = new HagoromoFlightData(packet.hasFlight(), packet.hagoromoActive(), packet.airborneTicks(), packet.cooldownTicks());
                player.setData(kogasastudio.ashihara.registry.DataAttachmentTypes.HAGOROMO_FLIGHT.get(), data);
            });
        }
    }
}