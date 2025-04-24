package kogasastudio.ashihara.mixin;

import kogasastudio.ashihara.client.render.PlayerAnimationProxy;
import kogasastudio.ashihara.utils.mixin.PlayerProxyProvider;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(Player.class)
public class MixinPlayer implements PlayerProxyProvider
{
    @Unique
    private PlayerAnimationProxy ashihara_1_21$proxy = new PlayerAnimationProxy((Player) (Object) this);

    @Override
    public PlayerAnimationProxy ashihara_1_21$getAnimationProxy()
    {
        return ashihara_1_21$proxy;
    }
}
