package kogasastudio.ashihara.client.models.geo;

import com.geckolib.renderer.base.GeoRenderState;
import com.mojang.blaze3d.vertex.PoseStack;
import kogasastudio.ashihara.Ashihara;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Player;
import com.geckolib.animatable.instance.AnimatableInstanceCache;
import com.geckolib.util.GeckoLibUtil;

public class SimpleInternalControlGeoModel extends InternalControlGeoModel<SimpleInternalControlGeoModel>
{
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    public final Player player;

    private final Identifier MODEL;
    private final Identifier TEXTURES;
    private final Identifier ANIMATIONS;
    //private final RenderType RENDER_TYPE;

    public SimpleInternalControlGeoModel(String modelPrefix, String texturePrefix, String animationsPrefix, Player player)
    {
        this.MODEL = Identifier.fromNamespaceAndPath(Ashihara.MODID, modelPrefix);
        this.TEXTURES = Identifier.fromNamespaceAndPath(Ashihara.MODID, texturePrefix);
        this.ANIMATIONS = Identifier.fromNamespaceAndPath(Ashihara.MODID, animationsPrefix);
        //this.RENDER_TYPE = RenderType.entityTranslucent(TEXTURES);
        this.player = player;
    }

    public void render(PoseStack stack, MultiBufferSource buffers, int light, int overlay)
    {
        //this.RENDERER.render(stack, this, buffers, RENDER_TYPE, buffers.getBuffer(RENDER_TYPE), light, overlay);
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
    public Identifier getModelResource(GeoRenderState state)
    {
        return MODEL;
    }

    @Override
    public Identifier getTextureResource(GeoRenderState state)
    {
        return TEXTURES;
    }

    @Override
    public Identifier getAnimationResource(SimpleInternalControlGeoModel animatable)
    {
        if (ANIMATIONS.getPath().isEmpty()) return null;
        return ANIMATIONS;
    }
}
