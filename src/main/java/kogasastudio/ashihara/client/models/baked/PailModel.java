package kogasastudio.ashihara.client.models.baked;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;

public class PailModel implements BakedModel
{
    private final BakedModel existingModel;

    public PailModel(BakedModel model)
    {
        this.existingModel = model;
    }

    @Override
    public List<BakedQuad> getQuads(BlockState state, Direction side, RandomSource rand)
    {
        return this.existingModel.getQuads(state, side, rand);
    }

    @Override
    public boolean useAmbientOcclusion()
    {
        return this.existingModel.useAmbientOcclusion();
    }

    @Override
    public boolean isGui3d()
    {
        return this.existingModel.isGui3d();
    }

    @Override
    public boolean usesBlockLight()
    {
        return this.existingModel.usesBlockLight();
    }

    @Override
    public boolean isCustomRenderer()
    {
        return true;
    }

    @Override
    public TextureAtlasSprite getParticleIcon()
    {
        return this.existingModel.getParticleIcon();
    }

    @Override
    public ItemOverrides getOverrides()
    {
        return this.existingModel.getOverrides();
    }

    @Override
    public BakedModel applyTransform(ItemDisplayContext transformType, PoseStack poseStack, boolean applyLeftHandTransform)
    {
        return existingModel.applyTransform(transformType, poseStack, applyLeftHandTransform);
    }
}
