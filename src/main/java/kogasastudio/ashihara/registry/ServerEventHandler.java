package kogasastudio.ashihara.registry;

import kogasastudio.ashihara.helper.RecipeHelper;
import kogasastudio.ashihara.network.SyncRecipesPayload;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.network.PacketDistributor;

@EventBusSubscriber
public class ServerEventHandler
{
    @SubscribeEvent
    public static void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event)
    {
        var cache = RecipeHelper.getServerCachedRecipes();
        for (var entry : cache.entrySet())
        {
            PacketDistributor.sendToPlayer(
                (ServerPlayer) event.getEntity(),
                new SyncRecipesPayload(entry.getKey(), entry.getValue())
            );
        }
    }
}
