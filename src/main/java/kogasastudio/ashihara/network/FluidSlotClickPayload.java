package kogasastudio.ashihara.network;

import io.netty.buffer.ByteBuf;
import kogasastudio.ashihara.Ashihara;
import net.minecraft.core.BlockPos;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.neoforge.transfer.ResourceHandlerUtil;
import net.neoforged.neoforge.transfer.access.ItemAccess;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import net.neoforged.neoforge.transfer.fluid.FluidUtil;
import net.neoforged.neoforge.transfer.resource.ResourceStack;

/**
 * 流体槽位点击包（C2S）。
 * 当玩家在 GUI 中左键点击流体槽时，尝试用光标抓取的物品与 BE 的流体处理器交互。
 */
public record FluidSlotClickPayload(BlockPos pos)
        implements CustomPacketPayload
{
    public static final Type<FluidSlotClickPayload> TYPE = new Type<>(
        Identifier.fromNamespaceAndPath(Ashihara.MODID, "fluid_slot_click"));

    @Override
    public Type<? extends CustomPacketPayload> type()
    {
        return TYPE;
    }

    public static final StreamCodec<ByteBuf, FluidSlotClickPayload> STREAM_CODEC =
            StreamCodec.composite(
                    BlockPos.STREAM_CODEC, FluidSlotClickPayload::pos,
                    FluidSlotClickPayload::new
            );

    public static class ServerHandler
    {
        public static void handle(FluidSlotClickPayload packet, IPayloadContext context)
        {
            context.enqueueWork(() ->
            {
                Player player = context.player();
                Level level = player.level();
                BlockPos pos = packet.pos();
                BlockEntity be = level.getBlockEntity(pos);
                if (be == null || be.isRemoved()) return;

                var beHandler = level.getCapability(Capabilities.Fluid.BLOCK, pos, null);
                if (beHandler == null) return;

                var cursorAccess = ItemAccess.forPlayerCursor(player, player.containerMenu).oneByOne();
                var cursorFluidHandler = cursorAccess.getCapability(Capabilities.Fluid.ITEM);
                if (cursorFluidHandler == null) return;

                // Try fill cursor from BE; on failure drain cursor into BE
                ResourceStack<FluidResource> r = ResourceHandlerUtil.moveFirst(beHandler, cursorFluidHandler, fr -> true, Integer.MAX_VALUE, null);
                if (r != null) FluidUtil.triggerSoundAndGameEvent(r.resource(), level, pos.getCenter(), player, true);
                else
                {
                    ResourceStack<FluidResource> f = ResourceHandlerUtil.moveFirst(cursorFluidHandler, beHandler, fr -> true, Integer.MAX_VALUE, null);
                    if (f != null) FluidUtil.triggerSoundAndGameEvent(f.resource(), level, pos.getCenter(), player, false);
                }
            });
        }
    }
}
