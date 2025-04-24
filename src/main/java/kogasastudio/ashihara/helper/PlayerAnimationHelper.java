package kogasastudio.ashihara.helper;

import kogasastudio.ashihara.client.models.geo.PlayerProxyModel;
import kogasastudio.ashihara.utils.mixin.PlayerProxyProvider;
import net.minecraft.world.entity.player.Player;

import java.util.function.Consumer;

public class PlayerAnimationHelper
{
    public static void triggerPlayerAnimation(Player player, Consumer<PlayerProxyModel> consumer)
    {
        ((PlayerProxyProvider) player).ashihara_1_21$getAnimationProxy().startProxy(consumer);
    }
}
