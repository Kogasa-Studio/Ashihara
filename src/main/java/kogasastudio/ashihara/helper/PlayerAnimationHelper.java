package kogasastudio.ashihara.helper;

import kogasastudio.ashihara.Ashihara;
import kogasastudio.ashihara.client.models.geo.PlayerProxyModel;
import kogasastudio.ashihara.network.AnimatePlayerPacket;
import kogasastudio.ashihara.registry.PlayerAnimations;
import kogasastudio.ashihara.utils.mixin.PlayerProxyProvider;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.player.RemotePlayer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.function.BiConsumer;

public class PlayerAnimationHelper
{
    public static void triggerPlayerAnimation(Player player, String id)
    {
        if (!(player instanceof LocalPlayer) && !(player instanceof RemotePlayer)) return;
        BiConsumer<Player, PlayerProxyModel> consumer = PlayerAnimations.get(id);
        if (consumer == null)
        {
            Ashihara.LOGGER_MAIN.error("Could not find any player animation with id: {}", id);
            return;
        }
        ((PlayerProxyProvider) player).ashihara_1_21$getAnimationProxy().startProxy(consumer);
    }

    public static void pushPlayerAnimation(Player player, String id)
    {
        if (!(player instanceof ServerPlayer serverPlayer)) return;
        PacketDistributor.sendToPlayer(serverPlayer, new AnimatePlayerPacket(id, player.getUUID()));
    }
}
