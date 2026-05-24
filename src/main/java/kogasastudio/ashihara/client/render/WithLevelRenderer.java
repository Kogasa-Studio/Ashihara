package kogasastudio.ashihara.client.render;

import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.fml.ModList;
import net.neoforged.neoforge.model.data.ModelData;

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
        return LevelRenderer.getLightCoords(be.getLevel(), be.getBlockPos());
    }

    
    default void resetToBlock000(BlockEntity be, PoseStack poseStack)
    {
        resetToBlock000(be.getBlockPos(), poseStack);
    }

    
    static void resetToBlock000(BlockPos pos, PoseStack poseStack)
    {
        poseStack.setIdentity();
        poseStack.translate(pos.getX() & 15, pos.getY() & 15, pos.getZ() & 15);
    }

    
    void renderStatic(SectionRenderContext context, ModelRenderer renderer);

    interface ModelRenderer
    {
        void renderBlockStateModel(BlockStateModel model, PoseStack stack, int overlay, ModelData modelData);
    }
}