package kogasastudio.ashihara.client.models.geo;

import com.mojang.blaze3d.vertex.PoseStack;
import kogasastudio.ashihara.Ashihara;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.renderer.GeoObjectRenderer;
import software.bernie.geckolib.util.GeckoLibUtil;

public class SimpleInternalControlGeoModel extends InternalControlGeoModel<SimpleInternalControlGeoModel>
{
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    private final ResourceLocation MODEL;
    private final ResourceLocation TEXTURES;
    private final ResourceLocation ANIMATIONS;
    private final RenderType RENDER_TYPE;

    public final GeoObjectRenderer<SimpleInternalControlGeoModel> RENDERER = new GeoObjectRenderer<>(this);

    public SimpleInternalControlGeoModel(String modelPrefix, String texturePrefix, String animationsPrefix)
    {
        this.MODEL = ResourceLocation.fromNamespaceAndPath(Ashihara.MODID, modelPrefix);
        this.TEXTURES = ResourceLocation.fromNamespaceAndPath(Ashihara.MODID, texturePrefix);
        this.ANIMATIONS = ResourceLocation.fromNamespaceAndPath(Ashihara.MODID, animationsPrefix);
        this.RENDER_TYPE = RenderType.entityTranslucent(TEXTURES);
    }

    public void render(PoseStack stack, MultiBufferSource buffers, int light, int overlay)
    {
        this.RENDERER.render(stack, this, buffers, RENDER_TYPE, buffers.getBuffer(RENDER_TYPE), light, overlay);
    }

    public SimpleInternalControlGeoModel(String modelPrefix, String texturePrefix)
    {
        this(modelPrefix, texturePrefix, "");
    }
    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache()
    {
        return cache;
    }

    @Override
    public double getTick(Object object)
    {
        return 0;
    }

    @Override
    public ResourceLocation getModelResource(SimpleInternalControlGeoModel animatable)
    {
        return MODEL;
    }

    @Override
    public ResourceLocation getTextureResource(SimpleInternalControlGeoModel animatable)
    {
        return TEXTURES;
    }

    @Override
    public ResourceLocation getAnimationResource(SimpleInternalControlGeoModel animatable)
    {
        if (ANIMATIONS.getPath().isEmpty()) return null;
        return ANIMATIONS;
    }
}
