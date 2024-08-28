package kogasastudio.ashihara.client.render;

import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.SectionBufferBuilderPack;
import net.minecraft.client.renderer.chunk.SectionCompiler;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.fml.ModList;

import java.util.Map;

@SuppressWarnings("unchecked")
public interface WithLevelRenderer<T extends BlockEntity>
{
    default boolean needRender(T object, Vec3 cameraPos)
    {
        return false;
    }

    default int getPackedLight(BlockEntity be)
    {
        if (be == null || be.getLevel() == null) return 0;
        return LevelRenderer.getLightColor(be.getLevel(), be.getBlockPos());
    }

    default MultiBufferSource createMultiBufferSource(SectionRenderContext context, RenderType... types)
    {
        SimpleMultiBufferSource multiBufferSource = new SimpleMultiBufferSource();
        for (RenderType type : types)
        {
            multiBufferSource.putBuffer(getBufferBuilder(context, type), type);
        }
        return multiBufferSource;
    }

    default BufferBuilder getBufferBuilder(SectionRenderContext context, RenderType type)
    {
        return getOrBeginLayer(context.bufferBuilders(), context.builderPack(), type);
    }

    default BufferBuilder getOrBeginLayer(Map<RenderType, BufferBuilder> map, SectionBufferBuilderPack pack, RenderType type)
    {
        BufferBuilder bufferbuilder = map.get(type);
        if (bufferbuilder == null)
        {
            ByteBufferBuilder bytebufferbuilder = pack.buffer(type);
            bufferbuilder = new BufferBuilder(bytebufferbuilder, type.mode(), type.format());
            map.put(type, bufferbuilder);
        }

        return bufferbuilder;
    }

    @OnlyIn(Dist.CLIENT)
    static boolean isBasicRenderType(RenderType renderType)
    {
        return renderType == RenderType.solid()
        || renderType == RenderType.cutout()
        || renderType == RenderType.translucent()
        || renderType == RenderType.cutoutMipped()
        || renderType == RenderType.tripwire();
    }

    @OnlyIn(Dist.CLIENT)
    default void resetToBlock000(BlockEntity be, RenderType renderType, PoseStack poseStack)
    {
        resetToBlock000(be.getBlockPos(), renderType, poseStack);
    }

    @OnlyIn(Dist.CLIENT)
    static void resetToBlock000(BlockPos pos, RenderType renderType, PoseStack poseStack)
    {
        poseStack.setIdentity();
        if (isBasicRenderType(renderType) || ModList.get().isLoaded("sodium"))
        {
            poseStack.translate(pos.getX() & 15, pos.getY() & 15, pos.getZ() & 15);
        } else
        {
            poseStack.translate(pos.getX(), pos.getY(), pos.getZ());
        }
    }

    @OnlyIn(Dist.CLIENT)
    void renderStatic(SectionRenderContext context);
}