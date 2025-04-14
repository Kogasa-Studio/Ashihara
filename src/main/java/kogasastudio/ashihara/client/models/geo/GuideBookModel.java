package kogasastudio.ashihara.client.models.geo;

import kogasastudio.ashihara.Ashihara;
import kogasastudio.ashihara.client.gui.GuideBookScreen;
import kogasastudio.ashihara.client.render.geo.GuideBookRenderer;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.animatable.SingletonGeoAnimatable;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.*;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.List;

@SuppressWarnings("removal")
public class GuideBookModel extends InternalControlGeoModel<GuideBookModel> implements SingletonGeoAnimatable
{
    public static final ResourceLocation MODEL = ResourceLocation.fromNamespaceAndPath(Ashihara.MODID, "geo/item/guidebook.geo.json");
    public static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(Ashihara.MODID, "textures/geo/guidebook.png");
    public static final ResourceLocation ANIMATION = ResourceLocation.fromNamespaceAndPath(Ashihara.MODID, "animations/item/guidebook.animation.json");

    public static final String ANIM_INTRO = "use.intro";
    public static final String ANIM_OPEN_FROM_LEFT = "use.open_from_left";
    public static final String ANIM_FLIP_INITIAL_LEFT = "flip.initial_flip_left";
    public static final String ANIM_FLIP_INITIAL_LEFT_S = "flip.initial_flip_left_s";
    public static final String ANIM_FLIP_COMMON_LEFT = "flip.flip_left";
    public static final String ANIM_FLIP_FINAL_LEFT_S = "flip.final_flip_left_s";
    public static final String ANIM_FLIP_FINAL_LEFT = "flip.final_flip_left";
    public static final String ANIM_CLOSEUP_FROM_LEFT = "use.closeup_from_left";
    public static final String ANIM_FLIP_BUFFERED_LEFT_0 = "flip.buffered_left_0";
    public static final String ANIM_FLIP_BUFFERED_LEFT_1 = "flip.buffered_left_1";
    public static final String ANIM_FLIP_BUFFERED_LEFT_2 = "flip.buffered_left_2";
    public static final String ANIM_FLIP_BUFFERED_LEFT_3 = "flip.buffered_left_3";
    public static final String ANIM_FLIP_BUFFERED_LEFT_4 = "flip.buffered_left_4";
    public static final String ANIM_FLIP_BUFFERED_LEFT_5 = "flip.buffered_left_5";
    public static final String ANIM_OPEN_FROM_RIGHT = "use.open_from_right";
    public static final String ANIM_FLIP_INITIAL_RIGHT = "flip.initial_flip_right";
    public static final String ANIM_FLIP_INITIAL_RIGHT_S = "flip.initial_flip_right_s";
    public static final String ANIM_FLIP_COMMON_RIGHT = "flip.flip_right";
    public static final String ANIM_FLIP_FINAL_RIGHT_S = "flip.final_flip_right_s";
    public static final String ANIM_FLIP_FINAL_RIGHT = "flip.final_flip_right";
    public static final String ANIM_CLOSEUP_FROM_RIGHT = "use.closeup_from_right";
    public static final String ANIM_FLIP_BUFFERED_RIGHT_0 = "flip.buffered_right_0";
    public static final String ANIM_FLIP_BUFFERED_RIGHT_1 = "flip.buffered_right_1";
    public static final String ANIM_FLIP_BUFFERED_RIGHT_2 = "flip.buffered_right_2";
    public static final String ANIM_FLIP_BUFFERED_RIGHT_3 = "flip.buffered_right_3";
    public static final String ANIM_FLIP_BUFFERED_RIGHT_4 = "flip.buffered_right_4";
    public static final String ANIM_FLIP_BUFFERED_RIGHT_5 = "flip.buffered_right_5";

    public static final String CONTROLLER_FLIP = "flip";
    public static final String CONTROLLER_BUFFERED = "buffered";

    public static final RawAnimation INTRO = RawAnimation.begin().thenPlay(ANIM_INTRO);
    public static final RawAnimation OPEN_FROM_LEFT = RawAnimation.begin().thenPlay(ANIM_OPEN_FROM_LEFT);
    public static final RawAnimation FLIP_INITIAL_LEFT = RawAnimation.begin().thenPlay(ANIM_FLIP_INITIAL_LEFT);
    public static final RawAnimation FLIP_INITIAL_LEFT_S = RawAnimation.begin().thenPlay(ANIM_FLIP_INITIAL_LEFT_S);
    public static final RawAnimation FLIP_COMMON_LEFT = RawAnimation.begin().thenPlay(ANIM_FLIP_COMMON_LEFT);
    public static final RawAnimation FLIP_FINAL_LEFT_S = RawAnimation.begin().thenPlay(ANIM_FLIP_FINAL_LEFT_S);
    public static final RawAnimation FLIP_FINAL_LEFT = RawAnimation.begin().thenPlay(ANIM_FLIP_FINAL_LEFT);
    public static final RawAnimation CLOSEUP_FROM_LEFT = RawAnimation.begin().thenPlay(ANIM_CLOSEUP_FROM_LEFT);
    public static final RawAnimation FLIP_BUFFERED_LEFT_0 = RawAnimation.begin().thenPlay(ANIM_FLIP_BUFFERED_LEFT_0);
    public static final RawAnimation FLIP_BUFFERED_LEFT_1 = RawAnimation.begin().thenPlay(ANIM_FLIP_BUFFERED_LEFT_1);
    public static final RawAnimation FLIP_BUFFERED_LEFT_2 = RawAnimation.begin().thenPlay(ANIM_FLIP_BUFFERED_LEFT_2);
    public static final RawAnimation FLIP_BUFFERED_LEFT_3 = RawAnimation.begin().thenPlay(ANIM_FLIP_BUFFERED_LEFT_3);
    public static final RawAnimation FLIP_BUFFERED_LEFT_4 = RawAnimation.begin().thenPlay(ANIM_FLIP_BUFFERED_LEFT_4);
    public static final RawAnimation FLIP_BUFFERED_LEFT_5 = RawAnimation.begin().thenPlay(ANIM_FLIP_BUFFERED_LEFT_5);
    public static final RawAnimation OPEN_FROM_RIGHT = RawAnimation.begin().thenPlay(ANIM_OPEN_FROM_RIGHT);
    public static final RawAnimation FLIP_INITIAL_RIGHT = RawAnimation.begin().thenPlay(ANIM_FLIP_INITIAL_RIGHT);
    public static final RawAnimation FLIP_INITIAL_RIGHT_S = RawAnimation.begin().thenPlay(ANIM_FLIP_INITIAL_RIGHT_S);
    public static final RawAnimation FLIP_COMMON_RIGHT = RawAnimation.begin().thenPlay(ANIM_FLIP_COMMON_RIGHT);
    public static final RawAnimation FLIP_FINAL_RIGHT_S = RawAnimation.begin().thenPlay(ANIM_FLIP_FINAL_RIGHT_S);
    public static final RawAnimation FLIP_FINAL_RIGHT = RawAnimation.begin().thenPlay(ANIM_FLIP_FINAL_RIGHT);
    public static final RawAnimation CLOSEUP_FROM_RIGHT = RawAnimation.begin().thenPlay(ANIM_CLOSEUP_FROM_RIGHT);
    public static final RawAnimation FLIP_BUFFERED_RIGHT_0 = RawAnimation.begin().thenPlay(ANIM_FLIP_BUFFERED_RIGHT_0);
    public static final RawAnimation FLIP_BUFFERED_RIGHT_1 = RawAnimation.begin().thenPlay(ANIM_FLIP_BUFFERED_RIGHT_1);
    public static final RawAnimation FLIP_BUFFERED_RIGHT_2 = RawAnimation.begin().thenPlay(ANIM_FLIP_BUFFERED_RIGHT_2);
    public static final RawAnimation FLIP_BUFFERED_RIGHT_3 = RawAnimation.begin().thenPlay(ANIM_FLIP_BUFFERED_RIGHT_3);
    public static final RawAnimation FLIP_BUFFERED_RIGHT_4 = RawAnimation.begin().thenPlay(ANIM_FLIP_BUFFERED_RIGHT_4);
    public static final RawAnimation FLIP_BUFFERED_RIGHT_5 = RawAnimation.begin().thenPlay(ANIM_FLIP_BUFFERED_RIGHT_5);

    public final GuideBookRenderer RENDERER = new GuideBookRenderer(this);

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    private int pageIndex = 0;

    public static int getTotalPages()
    {
        return totalPages;
    }

    public static int totalPages = 150;

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar)
    {
        controllerRegistrar.add(new AnimationController<>(this, "internal", animationState -> PlayState.STOP).triggerableAnim("internal", INTERNAL));
        controllerRegistrar.add(new AnimationController<>(this, "Intro", animationState -> PlayState.STOP).triggerableAnim(ANIM_INTRO, INTRO));
        controllerRegistrar.add(new AnimationController<>(this, CONTROLLER_FLIP, animationState -> PlayState.STOP)
                                .triggerableAnim(ANIM_OPEN_FROM_LEFT, OPEN_FROM_LEFT)
                                .triggerableAnim(ANIM_OPEN_FROM_RIGHT, OPEN_FROM_RIGHT)
                                .triggerableAnim(ANIM_FLIP_INITIAL_LEFT, FLIP_INITIAL_LEFT)
                                .triggerableAnim(ANIM_FLIP_INITIAL_LEFT, FLIP_INITIAL_LEFT)
                                .triggerableAnim(ANIM_FLIP_INITIAL_LEFT_S, FLIP_INITIAL_LEFT_S)
                                .triggerableAnim(ANIM_FLIP_COMMON_LEFT, FLIP_COMMON_LEFT)
                                .triggerableAnim(ANIM_FLIP_FINAL_LEFT_S, FLIP_FINAL_LEFT_S)
                                .triggerableAnim(ANIM_FLIP_FINAL_LEFT, FLIP_FINAL_LEFT)
                                .triggerableAnim(ANIM_CLOSEUP_FROM_LEFT, CLOSEUP_FROM_LEFT)
                                .triggerableAnim(ANIM_FLIP_BUFFERED_LEFT_0, FLIP_BUFFERED_LEFT_0)
                                .triggerableAnim(ANIM_FLIP_BUFFERED_LEFT_1, FLIP_BUFFERED_LEFT_1)
                                .triggerableAnim(ANIM_FLIP_BUFFERED_LEFT_2, FLIP_BUFFERED_LEFT_2)
                                .triggerableAnim(ANIM_FLIP_BUFFERED_LEFT_3, FLIP_BUFFERED_LEFT_3)
                                .triggerableAnim(ANIM_FLIP_BUFFERED_LEFT_4, FLIP_BUFFERED_LEFT_4)
                                .triggerableAnim(ANIM_FLIP_BUFFERED_LEFT_5, FLIP_BUFFERED_LEFT_5)
                                .triggerableAnim(ANIM_FLIP_INITIAL_RIGHT, FLIP_INITIAL_RIGHT)
                                .triggerableAnim(ANIM_FLIP_INITIAL_RIGHT_S, FLIP_INITIAL_RIGHT_S)
                                .triggerableAnim(ANIM_FLIP_COMMON_RIGHT, FLIP_COMMON_RIGHT)
                                .triggerableAnim(ANIM_FLIP_FINAL_RIGHT_S, FLIP_FINAL_RIGHT_S)
                                .triggerableAnim(ANIM_FLIP_FINAL_RIGHT, FLIP_FINAL_RIGHT)
                                .triggerableAnim(ANIM_CLOSEUP_FROM_RIGHT, CLOSEUP_FROM_RIGHT));
        controllerRegistrar.add(new AnimationController<>(this, ANIM_FLIP_BUFFERED_LEFT_0, animationState -> PlayState.STOP).triggerableAnim(ANIM_FLIP_BUFFERED_LEFT_0, FLIP_BUFFERED_LEFT_0));
        controllerRegistrar.add(new AnimationController<>(this, ANIM_FLIP_BUFFERED_LEFT_1, animationState -> PlayState.STOP).triggerableAnim(ANIM_FLIP_BUFFERED_LEFT_1, FLIP_BUFFERED_LEFT_1));
        controllerRegistrar.add(new AnimationController<>(this, ANIM_FLIP_BUFFERED_LEFT_2, animationState -> PlayState.STOP).triggerableAnim(ANIM_FLIP_BUFFERED_LEFT_2, FLIP_BUFFERED_LEFT_2));
        controllerRegistrar.add(new AnimationController<>(this, ANIM_FLIP_BUFFERED_LEFT_3, animationState -> PlayState.STOP).triggerableAnim(ANIM_FLIP_BUFFERED_LEFT_3, FLIP_BUFFERED_LEFT_3));
        controllerRegistrar.add(new AnimationController<>(this, ANIM_FLIP_BUFFERED_LEFT_4, animationState -> PlayState.STOP).triggerableAnim(ANIM_FLIP_BUFFERED_LEFT_4, FLIP_BUFFERED_LEFT_4));
        controllerRegistrar.add(new AnimationController<>(this, ANIM_FLIP_BUFFERED_LEFT_5, animationState -> PlayState.STOP).triggerableAnim(ANIM_FLIP_BUFFERED_LEFT_5, FLIP_BUFFERED_LEFT_5));
        controllerRegistrar.add(new AnimationController<>(this, ANIM_FLIP_BUFFERED_RIGHT_0, animationState -> PlayState.STOP).triggerableAnim(ANIM_FLIP_BUFFERED_RIGHT_0, FLIP_BUFFERED_RIGHT_0));
        controllerRegistrar.add(new AnimationController<>(this, ANIM_FLIP_BUFFERED_RIGHT_1, animationState -> PlayState.STOP).triggerableAnim(ANIM_FLIP_BUFFERED_RIGHT_1, FLIP_BUFFERED_RIGHT_1));
        controllerRegistrar.add(new AnimationController<>(this, ANIM_FLIP_BUFFERED_RIGHT_2, animationState -> PlayState.STOP).triggerableAnim(ANIM_FLIP_BUFFERED_RIGHT_2, FLIP_BUFFERED_RIGHT_2));
        controllerRegistrar.add(new AnimationController<>(this, ANIM_FLIP_BUFFERED_RIGHT_3, animationState -> PlayState.STOP).triggerableAnim(ANIM_FLIP_BUFFERED_RIGHT_3, FLIP_BUFFERED_RIGHT_3));
        controllerRegistrar.add(new AnimationController<>(this, ANIM_FLIP_BUFFERED_RIGHT_4, animationState -> PlayState.STOP).triggerableAnim(ANIM_FLIP_BUFFERED_RIGHT_4, FLIP_BUFFERED_RIGHT_4));
        controllerRegistrar.add(new AnimationController<>(this, ANIM_FLIP_BUFFERED_RIGHT_5, animationState -> PlayState.STOP).triggerableAnim(ANIM_FLIP_BUFFERED_RIGHT_5, FLIP_BUFFERED_RIGHT_5));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache()
    {
        return this.cache;
    }

    @Override
    public double getTick(Object o)
    {
        return 0;
    }

    @Override
    public ResourceLocation getModelResource(GuideBookModel guideBookModel)
    {
        return MODEL;
    }

    @Override
    public ResourceLocation getTextureResource(GuideBookModel guideBookModel)
    {
        return TEXTURE;
    }

    @Override
    public ResourceLocation getAnimationResource(GuideBookModel guideBookModel)
    {
        return ANIMATION;
    }

    public int getPageIndex()
    {
        return pageIndex;
    }

    public void setPageIndex(int pageIndex)
    {
        this.pageIndex = pageIndex;
    }

    /**
     * PageIndex和PageNumber是不同的，前者表示纸张的序数，后者表示页码。后者的总长度是前者的两倍。
     * @param currentPageIndex 当前页面左侧显示的纸张序数
     */
    public static String getFlipAnim(int currentPageIndex, int nextPageIndex, int buffer)
    {
        if (currentPageIndex == 0 && nextPageIndex == 1) return ANIM_OPEN_FROM_LEFT;
        if (currentPageIndex == 1 && nextPageIndex == 2) return ANIM_FLIP_INITIAL_LEFT;
        if (currentPageIndex == 2 && nextPageIndex == 3) return ANIM_FLIP_INITIAL_LEFT_S;
        if (currentPageIndex > 1 && currentPageIndex < totalPages - 3 && nextPageIndex == currentPageIndex + 1)
        {
            switch (buffer)
            {
                case 0 -> {return ANIM_FLIP_COMMON_LEFT;}
                case 1 -> {return ANIM_FLIP_BUFFERED_LEFT_0;}
                case 2 -> {return ANIM_FLIP_BUFFERED_LEFT_1;}
                case 3 -> {return ANIM_FLIP_BUFFERED_LEFT_2;}
                case 4 -> {return ANIM_FLIP_BUFFERED_LEFT_3;}
                case 5 -> {return ANIM_FLIP_BUFFERED_LEFT_4;}
                case 6 -> {return ANIM_FLIP_BUFFERED_LEFT_5;}
            }
        }
        //if (currentPageIndex > 1 && nextPageIndex > currentPageIndex + 1) return FLIP_LEFT_MASSIVE;
        if (currentPageIndex == totalPages - 3 && nextPageIndex == totalPages - 2) return ANIM_FLIP_FINAL_LEFT_S;
        if (currentPageIndex == totalPages - 2 && nextPageIndex == totalPages - 1) return ANIM_FLIP_FINAL_LEFT;
        if (currentPageIndex == totalPages - 1 && nextPageIndex == totalPages) return ANIM_CLOSEUP_FROM_LEFT;
        if (currentPageIndex == totalPages && nextPageIndex == totalPages - 1) return ANIM_OPEN_FROM_RIGHT;
        if (currentPageIndex == totalPages - 1 && nextPageIndex == totalPages - 2) return ANIM_FLIP_INITIAL_RIGHT;
        if (currentPageIndex == totalPages - 2 && nextPageIndex == totalPages - 3) return ANIM_FLIP_INITIAL_RIGHT_S;
        if (currentPageIndex <= totalPages - 3 && currentPageIndex > 3 && nextPageIndex == currentPageIndex - 1)
        {
            switch (buffer)
            {
                case 0 -> {return ANIM_FLIP_COMMON_RIGHT;}
                case 1 -> {return ANIM_FLIP_BUFFERED_RIGHT_0;}
                case 2 -> {return ANIM_FLIP_BUFFERED_RIGHT_1;}
                case 3 -> {return ANIM_FLIP_BUFFERED_RIGHT_2;}
                case 4 -> {return ANIM_FLIP_BUFFERED_RIGHT_3;}
                case 5 -> {return ANIM_FLIP_BUFFERED_RIGHT_4;}
                case 6 -> {return ANIM_FLIP_BUFFERED_RIGHT_5;}
            }
        }
        //if (currentPageIndex < 150 && nextPageIndex < currentPageIndex - 1) return FLIP_RIGHT_MASSIVE;
        if (currentPageIndex == 3 && nextPageIndex == 2) return ANIM_FLIP_FINAL_RIGHT_S;
        if (currentPageIndex == 2 && nextPageIndex == 1) return ANIM_FLIP_FINAL_RIGHT;
        if (currentPageIndex == 1 && nextPageIndex == 0) return ANIM_CLOSEUP_FROM_RIGHT;
        return null;
    }
}
