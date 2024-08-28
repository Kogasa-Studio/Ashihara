package kogasastudio.ashihara.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.vertex.*;
import kogasastudio.ashihara.client.render.SectionRenderContext;
import kogasastudio.ashihara.client.render.WithLevelRenderer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.SectionBufferBuilderPack;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.client.renderer.chunk.RenderChunkRegion;
import net.minecraft.client.renderer.chunk.SectionCompiler;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.client.event.AddSectionGeometryEvent;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.Map;

@Mixin(SectionCompiler.class)
public class MixinSectionCompiler
{
    @Inject
    (
        method = "compile(Lnet/minecraft/core/SectionPos;Lnet/minecraft/client/renderer/chunk/RenderChunkRegion;Lcom/mojang/blaze3d/vertex/VertexSorting;Lnet/minecraft/client/renderer/SectionBufferBuilderPack;Ljava/util/List;)Lnet/minecraft/client/renderer/chunk/SectionCompiler$Results;",
        at = @At
        (
            value = "INVOKE",
            target = "Lnet/minecraft/client/renderer/chunk/SectionCompiler;handleBlockEntity(Lnet/minecraft/client/renderer/chunk/SectionCompiler$Results;Lnet/minecraft/world/level/block/entity/BlockEntity;)V",
            shift = At.Shift.AFTER
        )
    )
    private void compileAshiharaSpecialSectionRenderers
    (
        SectionPos pos,
        RenderChunkRegion level,
        VertexSorting sorting,
        SectionBufferBuilderPack pack,
        List<AddSectionGeometryEvent.AdditionalSectionRenderer> additionalRenderers,
        CallbackInfoReturnable<SectionCompiler.Results> cir,
        @Local BlockEntity blockEntity,
        @Local PoseStack poseStack,
        @Local Map<RenderType, BufferBuilder> bufferBuilders,
        @Local(ordinal = 2) BlockPos blockPos,
        @Local RandomSource random
    )
    {
        if (blockEntity instanceof WithLevelRenderer<?>)
        {
            ((WithLevelRenderer<?>) blockEntity).renderStatic(new SectionRenderContext(level, blockPos, blockEntity, poseStack, (SectionCompiler) (Object) this, bufferBuilders, pack, additionalRenderers, null));
        }
    }

    @Redirect(method = "getOrBeginLayer", at = @At(value = "NEW", target = "(Lcom/mojang/blaze3d/vertex/ByteBufferBuilder;Lcom/mojang/blaze3d/vertex/VertexFormat$Mode;Lcom/mojang/blaze3d/vertex/VertexFormat;)Lcom/mojang/blaze3d/vertex/BufferBuilder;"))
    private BufferBuilder resetFormat(ByteBufferBuilder flag1, VertexFormat.Mode mode, VertexFormat format, @Local BufferBuilder builder, @Local ByteBufferBuilder byteBufferBuilder, @Local(argsOnly = true) RenderType type)
    {
        return new BufferBuilder(byteBufferBuilder, type.mode(), type.format());
    }
}
