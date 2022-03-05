package kogasastudio.ashihara.client.models.baked;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.model.ItemOverrideList;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.Direction;

import java.util.List;
import java.util.Random;

public class PailModel implements IBakedModel
{
    private final IBakedModel existingModel;

    public PailModel(IBakedModel model)
    {
        this.existingModel = model;
    }

    @Override
    public List<BakedQuad> getQuads(BlockState state, Direction side, Random rand)
    {
        return this.existingModel.getQuads(state, side, rand);
    }

    @Override
    public boolean isAmbientOcclusion() {return this.existingModel.isAmbientOcclusion();}

    @Override
    public boolean isGui3d() {return this.existingModel.isGui3d();}

    @Override
    public boolean isSideLit() {return this.existingModel.isSideLit();}

    @Override
    public boolean isBuiltInRenderer() {return true;}

    @Override
    public TextureAtlasSprite getParticleTexture() {return this.existingModel.getParticleTexture();}

    @Override
    public ItemOverrideList getOverrides() {return this.existingModel.getOverrides();}

    @Override
    public IBakedModel handlePerspective(ItemCameraTransforms.TransformType cameraTransformType, MatrixStack mat)
    {return existingModel.handlePerspective(cameraTransformType, mat);}
}
