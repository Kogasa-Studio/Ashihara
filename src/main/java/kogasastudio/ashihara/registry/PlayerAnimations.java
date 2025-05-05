package kogasastudio.ashihara.registry;

import kogasastudio.ashihara.client.models.geo.PlayerProxyModel;
import net.minecraft.world.entity.player.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

public class PlayerAnimations
{
    private static final Map<String, BiConsumer<Player, PlayerProxyModel>> PLAYER_ANIMATIONS = new HashMap<>();

    public static final String TEST_TEKOKI_ANIM = register("test_tekoki", (player, model) -> model.triggerAnim(player, model.hashCode(), PlayerProxyModel.TEST, PlayerProxyModel.TEST));
    public static final String OTSUCHI_HOLD_ANIM = register("otsuchi_hold", (player, model) -> model.triggerAnim(player, model.hashCode(), PlayerProxyModel.OTSUCHI_HOLD, PlayerProxyModel.OTSUCHI_HOLD));
    public static final String OTSUCHI_SMASH_ANIM = register("otsuchi_smash", (player, model) -> model.triggerAnim(player, model.hashCode(), PlayerProxyModel.OTSUCHI_SMASH, PlayerProxyModel.OTSUCHI_SMASH));

    public static String register(String id, BiConsumer<Player, PlayerProxyModel> anim)
    {
        PLAYER_ANIMATIONS.put(id, anim);
        return id;
    }

    public static BiConsumer<Player, PlayerProxyModel> get(String id)
    {
        return PLAYER_ANIMATIONS.get(id);
    }
}
