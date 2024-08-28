package kogasastudio.ashihara.mixin.sodium;

import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import kogasastudio.ashihara.client.render.SectionRenderContext;
import kogasastudio.ashihara.client.render.WithLevelRenderer;
import net.caffeinemc.mods.sodium.client.render.chunk.compile.ChunkBuildBuffers;
import net.caffeinemc.mods.sodium.client.render.chunk.compile.ChunkBuildContext;
import net.caffeinemc.mods.sodium.client.render.chunk.compile.ChunkBuildOutput;
import net.caffeinemc.mods.sodium.client.render.chunk.compile.tasks.ChunkBuilderMeshingTask;
import net.caffeinemc.mods.sodium.client.render.chunk.terrain.material.DefaultMaterials;
import net.caffeinemc.mods.sodium.client.render.chunk.translucent_sorting.TranslucentGeometryCollector;
import net.caffeinemc.mods.sodium.client.util.task.CancellationToken;
import net.caffeinemc.mods.sodium.client.world.LevelSlice;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.function.Function;

@Pseudo
@Mixin(ChunkBuilderMeshingTask.class)
public class MixinChunkBuilderMeshingTask
{
    @Inject
    (
    method = "execute(Lnet/caffeinemc/mods/sodium/client/render/chunk/compile/ChunkBuildContext;Lnet/caffeinemc/mods/sodium/client/util/task/CancellationToken;)Lnet/caffeinemc/mods/sodium/client/render/chunk/compile/ChunkBuildOutput;",
    at = @At
        (
            value = "INVOKE",
            target = "Lnet/minecraft/client/renderer/blockentity/BlockEntityRenderDispatcher;getRenderer(Lnet/minecraft/world/level/block/entity/BlockEntity;)Lnet/minecraft/client/renderer/blockentity/BlockEntityRenderer;"
        )
    )
    private void onSodiumRenderWLR
    (
    ChunkBuildContext buildContext,
    CancellationToken cancellationToken,
    CallbackInfoReturnable<ChunkBuildOutput> cir,
    @Local LevelSlice slice,
    @Local ChunkBuildBuffers buffers,
    @Local TranslucentGeometryCollector collector,
    @Local BlockEntity blockEntity
    )
    {
        if (blockEntity instanceof WithLevelRenderer<?>)
        {
            Function<RenderType, VertexConsumer> consumer = type -> buffers.get(DefaultMaterials.forRenderLayer(type)).asFallbackVertexConsumer(DefaultMaterials.forRenderLayer(type), collector);
            ((WithLevelRenderer<?>) blockEntity).renderStatic(new SectionRenderContext(slice, blockEntity.getBlockPos(), blockEntity, new PoseStack(), null, null, null, null, consumer));
        }
    }
}
