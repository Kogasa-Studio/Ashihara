package kogasastudio.ashihara.network;

import io.netty.buffer.ByteBuf;
import kogasastudio.ashihara.Ashihara;
import kogasastudio.ashihara.helper.PlayerAnimationHelper;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.UUID;

public record AnimatePlayerPacket(String id, UUID player_uuid) implements CustomPacketPayload
{
    public static final Type<AnimatePlayerPacket> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(Ashihara.MODID, "animate_player_packet"));

    @Override
    public Type<? extends CustomPacketPayload> type()
    {
        return TYPE;
    }

    public static final StreamCodec<ByteBuf, AnimatePlayerPacket> STREAM_CODEC = StreamCodec.composite
    (
        ByteBufCodecs.STRING_UTF8, AnimatePlayerPacket::id,
        ByteBufCodecs.STRING_UTF8.map(UUID::fromString, UUID::toString), AnimatePlayerPacket::player_uuid,
        AnimatePlayerPacket::new
    );

    public static class ClientHandler
    {
        public static void handle(AnimatePlayerPacket packet, final IPayloadContext context)
        {
            Level level = context.player().level();
            if (!level.isClientSide()) return;
            Player playerToAnimate = level.getPlayerByUUID(packet.player_uuid());
            if (playerToAnimate == null) return;
            PlayerAnimationHelper.triggerPlayerAnimation(playerToAnimate, packet.id());
        }
    }
}
