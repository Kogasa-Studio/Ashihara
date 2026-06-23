package kogasastudio.ashihara.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.QuadInstance;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.block.BlockAndTintGetter;
import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
import net.minecraft.client.renderer.block.dispatch.BlockStateModelPart;
import net.minecraft.client.resources.model.geometry.BakedQuad;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.model.ao.EnhancedBlockModelLighter;

import java.util.ArrayList;
import java.util.List;

/**
 * Renders a standalone BlockStateModel into a {@link MultiBufferSource.BufferSource},
 * suitable for BER and ISTER contexts (per-frame rendering).
 */
public final class StandaloneModelRenderer
{
    private static final RandomSource RANDOM = RandomSource.create(42L);

    private StandaloneModelRenderer() {}

   /** Render the model at identity transform (positioning handled via PoseStack). */
   public static void render(BlockStateModel model, BlockAndTintGetter level, BlockPos pos, BlockState state, PoseStack pose, MultiBufferSource.BufferSource buf, int packedLight)
   {
       var lighter = new EnhancedBlockModelLighter();
       lighter.reset();
       List<BlockStateModelPart> parts = new ArrayList<>();
       model.collectParts(level, pos, state, RANDOM, parts);
       for (var part : parts)
       {
           for (var face : Direction.values())
               for (var quad : part.getQuads(face))
                   putQuad(quad, level, state, pos, lighter, pose, buf, packedLight);
           for (var quad : part.getQuads(null))
               putQuad(quad, level, state, pos, lighter, pose, buf, packedLight);
       }
   }

   private static void putQuad(BakedQuad quad, BlockAndTintGetter level, BlockState state, BlockPos pos, EnhancedBlockModelLighter lighter, PoseStack pose, MultiBufferSource.BufferSource buf, int packedLight)
   {
       var instance = new QuadInstance();
       if (quad.materialInfo().ambientOcclusion())
       {
           lighter.prepareQuadAmbientOcclusion(level, state, pos, quad, instance);
       }
       else
       {
           lighter.prepareQuadFlat(level, state, pos, packedLight, quad, instance);
       }
       VertexConsumer consumer = buf.getBuffer(Sheets.cutoutBlockSheet());
       consumer.putBakedQuad(pose.last(), quad, instance);
   }

   /** Render with explicit packedLight — suitable for ISTER where pos-based AO is invalid. */
   public static void renderItem(BlockStateModel model, BlockAndTintGetter level, BlockPos pos, BlockState state, PoseStack pose, MultiBufferSource.BufferSource buf, int packedLight)
   {
       List<BlockStateModelPart> parts = new ArrayList<>();
       model.collectParts(level, pos, state, RANDOM, parts);
       for (var part : parts)
       {
           for (var face : Direction.values())
               for (var quad : part.getQuads(face))
               {
                   var instance = new QuadInstance();
                   setLight(instance, packedLight);
                   VertexConsumer consumer = buf.getBuffer(Sheets.cutoutBlockSheet());
                   consumer.putBakedQuad(pose.last(), quad, instance);
               }
           for (var quad : part.getQuads(null))
           {
               var instance = new QuadInstance();
               setLight(instance, packedLight);
               VertexConsumer consumer = buf.getBuffer(Sheets.cutoutBlockSheet());
               consumer.putBakedQuad(pose.last(), quad, instance);
           }
       }
   }

   private static void setLight(QuadInstance instance, int packedLight)
   {
       instance.setLightCoords(packedLight);
   }
}
