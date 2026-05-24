package kogasastudio.ashihara.registry;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.OnDatapackSyncEvent;

@EventBusSubscriber
public class ServerEventHandler
{
    @SubscribeEvent
    public static void onDatapackSync(OnDatapackSyncEvent event)
    {
        event.sendRecipes(
            RecipeTypes.MORTAR.get(),
            RecipeTypes.POT.get(),
            RecipeTypes.CUTTING_BOARD.get()
        );
    }
}
