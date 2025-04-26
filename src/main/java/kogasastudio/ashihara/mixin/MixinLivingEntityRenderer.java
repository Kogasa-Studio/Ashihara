package kogasastudio.ashihara.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import kogasastudio.ashihara.client.render.PlayerAnimationProxy;
import kogasastudio.ashihara.utils.mixin.PlayerProxyProvider;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import software.bernie.geckolib.animation.AnimationState;

@Mixin(LivingEntityRenderer.class)
public class MixinLivingEntityRenderer
{
    @Inject(method = "render(Lnet/minecraft/world/entity/LivingEntity;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/entity/LivingEntityRenderer;getOverlayCoords(Lnet/minecraft/world/entity/LivingEntity;F)I"))
    public <T extends LivingEntity> void render(T entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int packedLight, CallbackInfo ci)
    {
        if (entity instanceof Player player)
        {
            PlayerAnimationProxy proxy = ((PlayerProxyProvider) player).ashihara_1_21$getAnimationProxy();
            proxy.model.handleAnimations(proxy.model, proxy.model.hashCode(), new AnimationState<>(proxy.model, 0, 0, partialTicks, false), partialTicks);
            proxy.proxy();
        }
    }
}
