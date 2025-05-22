package kogasastudio.ashihara.client.render.ter;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import kogasastudio.ashihara.block.blockentity.IRenderInWorldToolTip;
import kogasastudio.ashihara.client.models.geo.UIPanelModel;
import kogasastudio.ashihara.client.render.geo.worldui.PanelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.level.block.entity.BlockEntity;

public interface InWorldToolTipBER<B extends BlockEntity & IRenderInWorldToolTip>
{
    default PanelRenderer.AdditionalRenderer getInfoRenderer(B be)
    {
        return ((poseStack, animatable, bufferSource, renderType, buffer, packedLight, partialTick) ->
        this.renderInfo(be, poseStack, animatable, bufferSource, renderType, buffer, packedLight, partialTick));
    }

    void renderInfo(B be, PoseStack poseStack, UIPanelModel animatable, MultiBufferSource bufferSource, RenderType renderType, VertexConsumer buffer, int packedLight, float partialTick);
}
