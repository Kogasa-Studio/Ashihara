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

public class CharlotteBER implements BlockEntityRenderer<CharlotteTE>, WithLevelRenderer<CharlotteTE>
{
    public CharlotteBER(BlockEntityRendererProvider.Context dispatcherIn) {}

    @Override
    public void renderStatic(SectionRenderContext context, ModelRenderer renderer)
    {
    }

    RenderType renderType = RenderType.entityTranslucent(UIPanelModel.TEXTURE);

    @Override
    public void render(CharlotteTE blockEntity, float partialTick, PoseStack poseStack, MultiBufferSource buffer, int packedLight, int packedOverlay)
    {
        poseStack.pushPose();
        poseStack.translate(0.5, 0.5, 0.5);
        poseStack.mulPose(Axis.YP.rotationDegrees(-Minecraft.getInstance().cameraEntity.yRotO));
        poseStack.translate(-0.5, -0.5, -0.5);
        blockEntity.model.RENDERER.render(poseStack, blockEntity.model, buffer, renderType, buffer.getBuffer(renderType), packedLight, packedOverlay);
        poseStack.popPose();
    }

    @Override
    public boolean shouldRender(CharlotteTE blockEntity, Vec3 cameraPos)
    {
        return blockEntity.doRender();
    }
}
