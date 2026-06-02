package kogasastudio.ashihara.client.render.quad;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.QuadInstance;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.block.BlockAndTintGetter;
import net.minecraft.client.renderer.block.BlockModelLighter;
import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
import net.minecraft.client.renderer.block.dispatch.BlockStateModelPart;
import net.minecraft.client.renderer.chunk.ChunkSectionLayer;
import net.minecraft.client.resources.model.geometry.BakedQuad;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.model.quad.MutableQuad;
import org.joml.Matrix4f;

import java.util.List;
import java.util.function.Function;

public class QuadBaker
{
    private static final RandomSource RANDOM = RandomSource.create(42L);

    /**
     * Render a BlockStateModel into a chunk buffer, baking self-rotation
     * and inBlockPos translation into each quad so that ambient occlusion
     * is computed on the final transformed geometry.
     */
    public static void renderModel(
        BlockStateModel model,
        BlockAndTintGetter level,
        BlockPos pos,
        BlockState state,
        Matrix4f quadTransform,
        PoseStack poseStack,
        Function<ChunkSectionLayer, VertexConsumer> chunkBuffer
    )
    {
        BlockModelLighter lighter = new BlockModelLighter();
        List<BlockStateModelPart> parts = new java.util.ArrayList<>();
        model.collectParts(level, pos, state, RANDOM, parts);

        for (BlockStateModelPart part : parts)
        {
            for (Direction face : Direction.values())
            {
                List<BakedQuad> quads = part.getQuads(face);
                if (quads.isEmpty()) continue;
                for (BakedQuad quad : quads)
                {
                    renderQuad(quad, quadTransform, pos, state, level, lighter, poseStack, chunkBuffer);
                }
            }
            List<BakedQuad> unculled = part.getQuads(null);
            for (BakedQuad quad : unculled)
            {
                renderQuad(quad, quadTransform, pos, state, level, lighter, poseStack, chunkBuffer);
            }
        }
    }

    private static void renderQuad(
        BakedQuad quad, Matrix4f transform, BlockPos pos,
        BlockState state, BlockAndTintGetter level, BlockModelLighter lighter,
        PoseStack poseStack, Function<ChunkSectionLayer, VertexConsumer> chunkBuffer
    )
    {
        MutableQuad mq = new MutableQuad();
        mq.setFrom(quad);
        mq.transform(transform);
        BakedQuad transformed = mq.toBakedQuad();

        QuadInstance instance = new QuadInstance();
        if (transformed.materialInfo().ambientOcclusion() != false)
        {
            lighter.prepareQuadAmbientOcclusion(level, state, pos, transformed, instance);
        }
        else
        {
            int light = LevelRenderer.getLightCoords(level, pos);
            lighter.prepareQuadFlat(level, state, pos, light, transformed, instance);
        }
        VertexConsumer consumer = chunkBuffer.apply(ChunkSectionLayer.CUTOUT);
        consumer.putBakedQuad(poseStack.last(), transformed, instance);
    }
}
