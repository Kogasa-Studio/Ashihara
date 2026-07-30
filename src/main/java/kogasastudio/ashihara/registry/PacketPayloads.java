package kogasastudio.ashihara.registry;

import kogasastudio.ashihara.network.AnimatePlayerPacket;
import kogasastudio.ashihara.network.ContainerSlotClickPacket;
import kogasastudio.ashihara.network.FluidSlotClickPayload;
import kogasastudio.ashihara.network.GuidebookProgressPacket;
import kogasastudio.ashihara.network.OpenGuidebookPacket;
import kogasastudio.ashihara.network.GridSnapPayload;
import kogasastudio.ashihara.network.EatingModePayload;
import kogasastudio.ashihara.network.HagoromoFlightSpeedPacket;
import kogasastudio.ashihara.network.HagoromoFlightSyncPacket;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

@EventBusSubscriber
public class PacketPayloads
{
    @SubscribeEvent
    public static void register(final RegisterPayloadHandlersEvent event)
    {
        final PayloadRegistrar registrar = event.registrar("1");
        registrar.playBidirectional
        (
        GuidebookProgressPacket.TYPE,
        GuidebookProgressPacket.STREAM_CODEC,
        GuidebookProgressPacket.Handler::handle,
        GuidebookProgressPacket.Handler::handle
        );
        registrar.playToClient(OpenGuidebookPacket.TYPE, OpenGuidebookPacket.STREAM_CODEC, OpenGuidebookPacket.ClientHandler::handle);
        registrar.playToClient(AnimatePlayerPacket.TYPE, AnimatePlayerPacket.STREAM_CODEC, AnimatePlayerPacket.ClientHandler::handle);
        // 泛用容器槽位点击包（所有基于 ContainerScreen3D 的容器共用）
        registrar.playToServer(ContainerSlotClickPacket.TYPE, ContainerSlotClickPacket.STREAM_CODEC, ContainerSlotClickPacket.ServerHandler::handle);
        // 流体槽位点击包
        registrar.playToServer(FluidSlotClickPayload.TYPE, FluidSlotClickPayload.STREAM_CODEC, FluidSlotClickPayload.ServerHandler::handle);
        registrar.playToServer(GridSnapPayload.TYPE, GridSnapPayload.STREAM_CODEC, GridSnapPayload.ServerHandler::handle);
        registrar.playToServer(EatingModePayload.TYPE, EatingModePayload.STREAM_CODEC, EatingModePayload.ServerHandler::handle);
        registrar.playToServer(HagoromoFlightSpeedPacket.TYPE, HagoromoFlightSpeedPacket.STREAM_CODEC, HagoromoFlightSpeedPacket.ServerHandler::handle);
        registrar.playToClient(HagoromoFlightSyncPacket.TYPE, HagoromoFlightSyncPacket.STREAM_CODEC, HagoromoFlightSyncPacket.ClientHandler::handle);
    }
}
