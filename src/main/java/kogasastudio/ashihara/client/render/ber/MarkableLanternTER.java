package kogasastudio.ashihara.client.render.ber;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Transformation;
import kogasastudio.ashihara.block.blockentity.MarkableLanternBE;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import static kogasastudio.ashihara.helper.RenderHelper.buildMatrix;

public class MarkableLanternTER implements BlockEntityRenderer<MarkableLanternBE, BlockEntityRenderState>
{
    private static RandomSource RANDOM = RandomSource.create(432L);

    public MarkableLanternTER(BlockEntityRendererProvider.Context dispatcher)
    {
    }

    /*@Override
    public void render(MarkableLanternBE tileEntityIn, float partialTicks, PoseStack poseStackIn, MultiBufferSource bufferIn, int combinedLightIn, int combinedOverlayIn)
    {
        RenderType icon = RenderType.entityTranslucent(tileEntityIn.getIcon());
        //获取IVertexBuilder
        VertexConsumer builder = bufferIn.getBuffer(icon);
        //通过小纹理的源文件获取该小纹理在Atlas上的位置

        //主渲染
        poseStackIn.pushPose();
        poseStackIn.translate(0.5, 0.5, 0.5);
        poseStackIn.mulPose(Axis.YP.rotationDegrees(getRotationByFacing(tileEntityIn.getBlockState().getValue(FACING))));
        Matrix4f wtf = poseStackIn.last().pose();/*icon.getU0(), icon.getV0()*//*
        buildMatrix(wtf, builder, 0.25f, 0.25f, -0.2505f,0,0 , combinedOverlayIn, combinedLightIn);
        buildMatrix(wtf, builder, -0.25f, 0.25f, -0.2505f, 1,0, combinedOverlayIn, combinedLightIn);
        buildMatrix(wtf, builder, -0.25f, -0.25f, -0.2505f, 1,1, combinedOverlayIn, combinedLightIn);
        buildMatrix(wtf, builder, 0.25f, -0.25f, -0.2505f,0,1, combinedOverlayIn, combinedLightIn);
        poseStackIn.popPose();

        //渲染背面图标
        poseStackIn.pushPose();
        poseStackIn.translate(0.5, 0.5, 0.5);
        poseStackIn.mulPose(Axis.YP.rotationDegrees(getRotationByFacing(tileEntityIn.getBlockState().getValue(FACING)) + 180));
        Matrix4f wth = poseStackIn.last().pose();
        buildMatrix(wth, builder, 0.25f, 0.25f, -0.2505f,0,0 , combinedOverlayIn, combinedLightIn);
        buildMatrix(wth, builder, -0.25f, 0.25f, -0.2505f, 1,0, combinedOverlayIn, combinedLightIn);
        buildMatrix(wth, builder, -0.25f, -0.25f, -0.2505f, 1,1, combinedOverlayIn, combinedLightIn);
        buildMatrix(wth, builder, 0.25f, -0.25f, -0.2505f,0,1, combinedOverlayIn, combinedLightIn);
        poseStackIn.popPose();
        poseStackIn.pushPose();
        poseStackIn.translate(0.5D, 0.5D, 0.5D);
        poseStackIn.scale(0.2F, 0.2F, 0.2F);
        renderRays(poseStackIn, bufferIn.getBuffer(RenderType.dragonRays()));
        renderRays(poseStackIn, bufferIn.getBuffer(RenderType.dragonRaysDepth()));
        poseStackIn.popPose();
    }*/

    private static void renderRays(PoseStack.Pose pose, VertexConsumer buffer)
    {
        Level level = Minecraft.getInstance().level;
        float timeConstant = level == null ? 0 : (float) (level.getGameTime()) / 200f; //Controls how fast the rays move.
        float rotationControl = Math.min(timeConstant > 0.8F ? (timeConstant - 0.8F) / 0.2F : 0.0F, 1.0F);
        int startColor = 0xffffff;
        int endColor = 0x3c9077;
        RandomSource randomsource = RandomSource.create(432L); //Very important, cannot replace with a static field. it's related to the continuity of the rays movement.
        Vector3f vector3f = new Vector3f();
        Vector3f vector3f1 = new Vector3f();
        Vector3f vector3f2 = new Vector3f();
        Vector3f vector3f3 = new Vector3f();
        Quaternionf quaternionf = new Quaternionf();
        int raysCount = 20;

        for (int l = 0; l < raysCount; l++)
        {
            quaternionf.rotationXYZ(
            randomsource.nextFloat() * (float) (Math.PI * 2),
            randomsource.nextFloat() * (float) (Math.PI * 2),
            randomsource.nextFloat() * (float) (Math.PI * 2)
                                   )
            .rotateXYZ(
            randomsource.nextFloat() * (float) (Math.PI * 2),
            randomsource.nextFloat() * (float) (Math.PI * 2),
            randomsource.nextFloat() * (float) (Math.PI * 2) + timeConstant * (float) (Math.PI / 2)
                      );
            pose.mulPose(new Transformation(null, quaternionf, null, null));
            float f1 = randomsource.nextFloat() * 20.0F + 5.0F + rotationControl * 10.0F;
            float f2 = randomsource.nextFloat() * 2.0F + 1.0F + rotationControl * 2.0F;
            vector3f1.set(-(Math.sqrt(3f) / 2f) * f2, f1, -0.5F * f2);
            vector3f2.set((Math.sqrt(3f) / 2f) * f2, f1, -0.5F * f2);
            vector3f3.set(0.0F, f1, f2);
            buffer.addVertex(pose, vector3f).setColor(startColor);
            buffer.addVertex(pose, vector3f1).setColor(endColor);
            buffer.addVertex(pose, vector3f2).setColor(endColor);
            buffer.addVertex(pose, vector3f).setColor(startColor);
            buffer.addVertex(pose, vector3f2).setColor(endColor);
            buffer.addVertex(pose, vector3f3).setColor(endColor);
            buffer.addVertex(pose, vector3f).setColor(startColor);
            buffer.addVertex(pose, vector3f3).setColor(endColor);
            buffer.addVertex(pose, vector3f1).setColor(endColor);
        }
    }

    @Override
    public boolean shouldRenderOffScreen()
    {
        return true;
    }

    @Override
    public BlockEntityRenderState createRenderState()
    {
        return new BlockEntityRenderState();
    }

    @Override
    public void submit(BlockEntityRenderState state, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, CameraRenderState camera)
    {
        submitNodeCollector.submitCustomGeometry(poseStack, RenderTypes.dragonRays(), MarkableLanternTER::renderRays);
    }

    @Override
    public int getViewDistance()
    {
        return BlockEntityRenderer.super.getViewDistance();
    }
}
