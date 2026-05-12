package kogasastudio.ashihara.client.render.ter;

import com.mojang.blaze3d.vertex.PoseStack;
import kogasastudio.ashihara.block.blockentity.PailBE;
import kogasastudio.ashihara.helper.RenderHelper;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.state.level.CameraRenderState;

public class PailTER implements BlockEntityRenderer<PailBE, BlockEntityRenderState>
{
    public PailTER(BlockEntityRendererProvider.Context rendererDispatcherIn)
    {
    }

    @Override
    public BlockEntityRenderState createRenderState()
    {
        return new BlockEntityRenderState();
    }

    @Override
    public void submit(BlockEntityRenderState state, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, CameraRenderState camera)
    {
    }
}
