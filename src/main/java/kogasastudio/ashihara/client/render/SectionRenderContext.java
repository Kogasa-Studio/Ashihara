package kogasastudio.ashihara.client.render;

import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexSorting;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.SectionBufferBuilderPack;
import net.minecraft.client.renderer.chunk.SectionCompiler;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.client.event.AddSectionGeometryEvent;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public record SectionRenderContext
(
    BlockAndTintGetter level,
    BlockPos pos,
    BlockEntity blockEntity,
    PoseStack poseStack,
    @Nullable SectionCompiler compiler,
    @Nullable Map<RenderType, BufferBuilder> bufferBuilders,
    @Nullable SectionBufferBuilderPack builderPack,
    @Nullable List<AddSectionGeometryEvent.AdditionalSectionRenderer> additionalRenderers,
    @Nullable Function<RenderType, VertexConsumer> consumerFunction
)
{
}
