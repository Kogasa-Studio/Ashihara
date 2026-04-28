package kogasastudio.ashihara.client.models.geo;

import com.mojang.blaze3d.vertex.PoseStack;
import kogasastudio.ashihara.Ashihara;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import software.bernie.geckolib.animatable.SingletonGeoAnimatable;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.util.GeckoLibUtil;

public class SimpleInternalControlGeoModel extends InternalControlGeoModel<SimpleInternalControlGeoModel>
{
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    public final Player player;

    private final ResourceLocation MODEL;
    private final ResourceLocation TEXTURES;
    private final ResourceLocation ANIMATIONS;
    private final RenderType RENDER_TYPE;

    public SimpleInternalControlGeoModel(String modelPrefix, String texturePrefix, String animationsPrefix, Player player)
    {
        this.MODEL = ResourceLocation.fromNamespaceAndPath(Ashihara.MODID, modelPrefix);
        this.TEXTURES = ResourceLocation.fromNamespaceAndPath(Ashihara.MODID, texturePrefix);
        this.ANIMATIONS = ResourceLocation.fromNamespaceAndPath(Ashihara.MODID, animationsPrefix);
        this.RENDER_TYPE = RenderType.entityTranslucent(TEXTURES);
        this.player = player;
        SingletonGeoAnimatable.registerSyncedAnimatable(this);
    }

    public void render(PoseStack stack, MultiBufferSource buffers, int light, int overlay)
    {
        this.RENDERER.render(stack, this, buffers, RENDER_TYPE, buffers.getBuffer(RENDER_TYPE), light, overlay);
    }

    public SimpleInternalControlGeoModel(String modelPrefix, String texturePrefix, Player player)
    {
        this(modelPrefix, texturePrefix, "", player);
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
