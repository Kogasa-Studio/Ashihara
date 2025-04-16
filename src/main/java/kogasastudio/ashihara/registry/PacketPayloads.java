package kogasastudio.ashihara.registry;

import kogasastudio.ashihara.network.GuidebookProgressPacket;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

@EventBusSubscriber(bus = EventBusSubscriber.Bus.MOD)
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
            GuidebookProgressPacket.ServerHandler::handle
        );
    }
}
