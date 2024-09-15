package kogasastudio.ashihara.client.models.baked;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Transformation;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.ChunkRenderTypeSet;
import net.neoforged.neoforge.client.model.IQuadTransformer;
import net.neoforged.neoforge.client.model.QuadTransformers;
import net.neoforged.neoforge.client.model.data.ModelData;
import net.neoforged.neoforge.common.util.TriState;
import org.jetbrains.annotations.Nullable;

import java.util.List;

@SuppressWarnings("NullableProblems")
public record TransformedBakedModel(BakedModel bakedModel, IQuadTransformer transformer) implements BakedModel
{
    public TransformedBakedModel(BakedModel bakedModel, PoseStack stack)
    {
        this(bakedModel, QuadTransformers.applying(new Transformation(stack.last().pose())));
    }

    @Override
    public List<BakedQuad> getQuads(@Nullable BlockState pState, @Nullable Direction pDirection, RandomSource pRandom)
    {
        return this.bakedModel().getQuads(pState, pDirection, pRandom).stream().map(transformer()::process).toList();
    }

    @Override
    public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, RandomSource rand, ModelData data, @Nullable RenderType renderType)
    {
        return bakedModel().getQuads(state, side, rand, data, renderType).stream().map(transformer()::process).toList();
    }

    @Override
    public boolean useAmbientOcclusion()
    {
        return bakedModel().useAmbientOcclusion();
    }

    @Override
    public boolean isGui3d()
    {
        return bakedModel().isGui3d();
    }

    @Override
    public boolean usesBlockLight()
    {
        return bakedModel().usesBlockLight();
    }

    @Override
    public boolean isCustomRenderer()
    {
        return bakedModel().isCustomRenderer();
    }

    @Override
    public TextureAtlasSprite getParticleIcon()
    {
        return bakedModel().getParticleIcon();
    }

    @Override
    public ItemOverrides getOverrides()
    {
        return bakedModel().getOverrides();
    }

    @Override
    public BakedModel applyTransform(ItemDisplayContext transformType, PoseStack poseStack, boolean applyLeftHandTransform)
    {
        return new TransformedBakedModel(bakedModel().applyTransform(transformType, poseStack, applyLeftHandTransform), transformer());
    }

    @Override
    public ModelData getModelData(BlockAndTintGetter level, BlockPos pos, BlockState state, ModelData modelData)
    {
        return bakedModel().getModelData(level, pos, state, modelData);
    }

    @Override
    public TextureAtlasSprite getParticleIcon(ModelData data)
    {
        return bakedModel().getParticleIcon(data);
    }

    @Override
    public List<BakedModel> getRenderPasses(ItemStack itemStack, boolean fabulous)
    {
        return bakedModel().getRenderPasses(itemStack, fabulous);
    }

    @Override
    public List<RenderType> getRenderTypes(ItemStack itemStack, boolean fabulous)
    {
        return bakedModel().getRenderTypes(itemStack, fabulous);
    }

    @Override
    public ChunkRenderTypeSet getRenderTypes(BlockState state, RandomSource rand, ModelData data)
    {
        return bakedModel().getRenderTypes(state, rand, data);
    }

    @Override
    public TriState useAmbientOcclusion(BlockState state, ModelData data, RenderType renderType)
    {
        return bakedModel().useAmbientOcclusion(state, data, renderType);
    }
}
