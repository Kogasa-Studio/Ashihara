package kogasastudio.ashihara.network;

import io.netty.buffer.ByteBuf;
import kogasastudio.ashihara.Ashihara;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ClickType;
import net.neoforged.neoforge.network.handling.IPayloadContext;

/**
 * 泛用容器槽位点击包（C2S）。
 * 所有基于 ContainerScreen3D 的容器都共用此包，无需各自实现网络层。
 *
 * 服务端收到后：验证 containerId → 调用 menu.clicked() → 由 ContainerSynchronizer 自动向客户端回推槽位更新。
 */
public record ContainerSlotClickPacket(int containerId, int slotId, int button, int clickTypeOrdinal)
        implements CustomPacketPayload
{
    public static final Type<ContainerSlotClickPacket> TYPE = new Type<>(
            ResourceLocation.fromNamespaceAndPath(Ashihara.MODID, "container_slot_click"));

    @Override
    public Type<? extends CustomPacketPayload> type()
    {
        return TYPE;
    }

    public static final StreamCodec<ByteBuf, ContainerSlotClickPacket> STREAM_CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.VAR_INT, ContainerSlotClickPacket::containerId,
                    ByteBufCodecs.VAR_INT, ContainerSlotClickPacket::slotId,
                    ByteBufCodecs.VAR_INT, ContainerSlotClickPacket::button,
                    ByteBufCodecs.VAR_INT, ContainerSlotClickPacket::clickTypeOrdinal,
                    ContainerSlotClickPacket::new
            );

    public static class ServerHandler
    {
        public static void handle(ContainerSlotClickPacket packet, IPayloadContext context)
        {
            context.enqueueWork(() ->
            {
                Player player = context.player();
                AbstractContainerMenu menu = player.containerMenu;
                if (menu.containerId != packet.containerId()) return;
                if (!menu.stillValid(player)) return;

                ClickType[] types = ClickType.values();
                int ord = packet.clickTypeOrdinal();
                if (ord < 0 || ord >= types.length) return;

                menu.clicked(packet.slotId(), packet.button(), types[ord], player);
            });
        }
    }
}

