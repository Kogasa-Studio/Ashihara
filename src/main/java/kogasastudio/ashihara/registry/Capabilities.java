package kogasastudio.ashihara.registry;

import kogasastudio.ashihara.block.tileentities.MortarTE;
import kogasastudio.ashihara.block.tileentities.TERegistryHandler;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;

@EventBusSubscriber(bus = EventBusSubscriber.Bus.MOD)
public class Capabilities
{
    @SubscribeEvent
    public void registerCaps(RegisterCapabilitiesEvent event)
    {
        event.registerBlockEntity
        (
            net.neoforged.neoforge.capabilities.Capabilities.ItemHandler.BLOCK,
            TERegistryHandler.MORTAR_TE.get(), MortarTE::getInv
        );
        event.registerBlockEntity
        (
            net.neoforged.neoforge.capabilities.Capabilities.FluidHandler.BLOCK,
            TERegistryHandler.MORTAR_TE.get(), (te, direction) -> te
        );
    }
}
