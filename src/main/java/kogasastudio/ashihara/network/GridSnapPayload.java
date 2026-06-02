package kogasastudio.ashihara.network;

import io.netty.buffer.ByteBuf;
import kogasastudio.ashihara.Ashihara;
import kogasastudio.ashihara.registry.DataComponentTypes;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record GridSnapPayload(int gridStep) implements CustomPacketPayload
{
    public static final Type<GridSnapPayload> TYPE =
        new Type<>(Identifier.fromNamespaceAndPath(Ashihara.MODID, "grid_snap"));

    @Override
    public Type<? extends CustomPacketPayload> type()
    {
        return TYPE;
    }

    public static final StreamCodec<ByteBuf, GridSnapPayload> STREAM_CODEC =
        StreamCodec.composite(ByteBufCodecs.INT, GridSnapPayload::gridStep, GridSnapPayload::new);

    public static class ServerHandler
    {
        public static void handle(GridSnapPayload payload, IPayloadContext context)
        {
            context.player().setData(DataComponentTypes.GRID_SNAP_STEP.get(), payload.gridStep());
        }
    }
}
