package kogasastudio.ashihara.client.render.ter;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import kogasastudio.ashihara.block.tileentities.CharlotteTE;
import kogasastudio.ashihara.client.models.geo.UIPanelModel;
import kogasastudio.ashihara.client.render.SectionRenderContext;
import kogasastudio.ashihara.client.render.WithLevelRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.phys.Vec3;
import software.bernie.geckolib.animatable.GeoBlockEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;

public class CharlotteBER implements BlockEntityRenderer<CharlotteTE>, WithLevelRenderer<CharlotteTE>, GeoBlockEntity
{
    public CharlotteBER(BlockEntityRendererProvider.Context dispatcherIn) {}

    @Override
    public void renderStatic(SectionRenderContext context, ModelRenderer renderer)
    {
    }

    RenderType renderType = RenderType.entityTranslucent(UIPanelModel.DEFAULT_TEXTURE);

    @Override
    public void render(CharlotteTE blockEntity, float partialTick, PoseStack poseStack, MultiBufferSource buffer, int packedLight, int packedOverlay)
    {
        poseStack.pushPose();
        poseStack.translate(0, 0.8 - 0.16/16f, 0);
        blockEntity.model.RENDERER.render(poseStack, blockEntity.model, buffer, renderType, buffer.getBuffer(renderType), packedLight, packedOverlay);
        poseStack.popPose();
    }

    @Override
    public boolean shouldRender(CharlotteTE blockEntity, Vec3 cameraPos)
    {
        return blockEntity.checkRender();
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers)
    {
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache()
    {
        return null;
    }
}
