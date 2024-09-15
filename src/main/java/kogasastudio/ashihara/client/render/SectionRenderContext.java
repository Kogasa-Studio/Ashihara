package kogasastudio.ashihara.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.client.model.lighting.QuadLighter;

import java.util.function.Function;

public record SectionRenderContext
(
    BlockAndTintGetter level,
    BlockPos pos,
    BlockEntity blockEntity,
    PoseStack poseStack,
    Function<RenderType, VertexConsumer> consumerFunction,
    QuadLighter lighter
)
{
}
