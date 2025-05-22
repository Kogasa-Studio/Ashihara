package kogasastudio.ashihara.registry;

import kogasastudio.ashihara.block.blockentity.MortarBE;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;

@EventBusSubscriber(bus = EventBusSubscriber.Bus.MOD)
public class Capabilities
{
    @SubscribeEvent
    public static void registerCaps(RegisterCapabilitiesEvent event)
    {
        event.registerBlockEntity
        (
        net.neoforged.neoforge.capabilities.Capabilities.ItemHandler.BLOCK,
        BlockEntities.MORTAR_BE.get(), MortarBE::getInv
        );
        event.registerBlockEntity
        (
        net.neoforged.neoforge.capabilities.Capabilities.FluidHandler.BLOCK,
        BlockEntities.MORTAR_BE.get(), MortarBE::getFluid
        );
    }
}
