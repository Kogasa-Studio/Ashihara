package kogasastudio.ashihara.network;

import io.netty.buffer.ByteBuf;
import kogasastudio.ashihara.Ashihara;
import kogasastudio.ashihara.registry.Items;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.level.GameType;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record HagoromoFlightSpeedPacket(double delta) implements CustomPacketPayload
{
    public static final Type<HagoromoFlightSpeedPacket> TYPE =
        new Type<>(Identifier.fromNamespaceAndPath(Ashihara.MODID, "hagoromo_fly_speed"));

    public static final StreamCodec<ByteBuf, HagoromoFlightSpeedPacket> STREAM_CODEC =
        StreamCodec.composite(ByteBufCodecs.DOUBLE, HagoromoFlightSpeedPacket::delta, HagoromoFlightSpeedPacket::new);

    @Override
    public Type<? extends CustomPacketPayload> type() { return TYPE; }

    public static class ServerHandler
    {
        public static void handle(HagoromoFlightSpeedPacket packet, IPayloadContext context)
        {
            context.enqueueWork(() ->
            {
                if (!(context.player() instanceof ServerPlayer player)) return;
                if (!player.getItemBySlot(EquipmentSlot.CHEST).is(Items.HAGOROMO)) return;
                float current = player.getAbilities().getFlyingSpeed();
                float delta = (float) packet.delta() * 0.02f;
                float newSpeed = Mth.clamp(current + delta, 0.005f, 0.5f);
                player.getAbilities().setFlyingSpeed(newSpeed);
                player.onUpdateAbilities();
                player.sendOverlayMessage(Component.translatable("tooltip.ashihara.hagoromo_flight_speed", String.format("%.2f", newSpeed)));
            });
        }
    }
}