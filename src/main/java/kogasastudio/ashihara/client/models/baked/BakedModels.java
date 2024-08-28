package kogasastudio.ashihara.client.models.baked;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.BlockModelRotation;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.client.resources.model.UnbakedModel;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.model.data.ModelData;
import net.neoforged.neoforge.client.model.geometry.IUnbakedGeometry;

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

    public static void render(PoseStack.Pose pose, VertexConsumer buffer, BakedModel model, RenderType type, BlockState state, int light) {
        Minecraft.getInstance().getBlockRenderer().getModelRenderer().renderModel(
                pose, buffer, state, model,
                1.0F, 1.0F, 1.0F,
                light, OverlayTexture.NO_OVERLAY,
                ModelData.EMPTY, type
        );
    }
}