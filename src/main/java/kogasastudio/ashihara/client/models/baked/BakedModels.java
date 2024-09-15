package kogasastudio.ashihara.client.models.baked;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import kogasastudio.ashihara.Ashihara;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.BlockModelRotation;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.client.resources.model.UnbakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.model.data.ModelData;
import net.neoforged.neoforge.client.model.geometry.IUnbakedGeometry;
import net.neoforged.neoforge.client.model.lighting.LightPipelineAwareModelBlockRenderer;
import net.neoforged.neoforge.client.model.lighting.QuadLighter;

/**
 * @author TT432
 */
public class BakedModels {
    /**
     * 需要在 ModelManager 重载结束后可用
     */
    public static BakedModel bake(ModelResourceLocation modelResourceLocation, IUnbakedGeometry<?> model) {
        BlockModel tempModel = BlockModel.fromString("{}");
        tempModel.customData.setCustomGeometry(model);
        return bake(modelResourceLocation, tempModel);
    }

    /**
     * 需要在 ModelManager 重载结束后可用
     */
    public static BakedModel bake(ModelResourceLocation modelResourceLocation, UnbakedModel model) {
        return Minecraft.getInstance().getModelManager().getModelBakery().new ModelBakerImpl(
                (modelLoc, loc) -> Minecraft.getInstance().getTextureAtlas(loc.atlasLocation()).apply(loc.texture()),
                modelResourceLocation
        ).bakeUncached(model, BlockModelRotation.X0_Y0);
    }

    public static void render(BakedModel model, VertexConsumer consumer, QuadLighter lighter, PoseStack stack, BlockAndTintGetter level, BlockState state, BlockPos pos, RenderType type)
    {
        LightPipelineAwareModelBlockRenderer.render
        (
            consumer, lighter, level, new TransformedBakedModel(model, stack), state, pos, new PoseStack(), false, Ashihara.getRandom(), 42L, OverlayTexture.NO_OVERLAY, ModelData.EMPTY, type
        );
    }
}