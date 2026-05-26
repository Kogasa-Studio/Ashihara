package kogasastudio.ashihara.mixin;

import kogasastudio.ashihara.client.render.PlayerAnimationProxy;
import kogasastudio.ashihara.utils.mixin.PlayerProxyProvider;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.player.PlayerModel;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerModel.class)
public class MixinPlayerModel
{
    @Inject(
        method = "setupAnim(Lnet/minecraft/client/renderer/entity/state/AvatarRenderState;)V",
        at = @At("RETURN")
    )
    private void afterSetupAnim(AvatarRenderState state, CallbackInfo ci)
    {
        for (Player player : Minecraft.getInstance().level.players())
        {
            PlayerAnimationProxy proxy = ((PlayerProxyProvider) player).ashihara_1_21$getAnimationProxy();
            if (proxy.isActivated())
            {
                proxy.tick(state.partialTick);
                proxy.applyToModel((PlayerModel) (Object) this);
                return;
            }
        }
    }
}
