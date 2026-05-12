package kogasastudio.ashihara.client.models.geo;

import com.geckolib.animatable.manager.AnimatableManager;
import com.geckolib.animation.object.EasingType;
import com.geckolib.animation.object.LoopType;
import com.geckolib.animation.object.PlayState;
import kogasastudio.ashihara.utils.mixin.GeoRendererPoseSyncProvider;
import net.minecraft.world.entity.Entity;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import com.geckolib.animatable.SingletonGeoAnimatable;
import com.geckolib.animation.*;
import com.geckolib.cache.animation.*;
import com.geckolib.cache.animation.BoneAnimation;
import com.geckolib.cache.animation.Keyframe;
import com.geckolib.cache.animation.KeyframeStack;
import com.geckolib.cache.animation.keyframeevent.CustomInstructionKeyframeData;
import com.geckolib.cache.animation.keyframeevent.ParticleKeyframeData;
import com.geckolib.cache.animation.keyframeevent.SoundKeyframeData;
import com.geckolib.loading.math.value.Constant;
import com.geckolib.model.GeoModel;
import com.geckolib.renderer.GeoObjectRenderer;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@NullMarked
public abstract class InternalControlGeoModel<T extends SingletonGeoAnimatable> extends GeoModel<T> implements SingletonGeoAnimatable
{
    public @Nullable Animation triggeredInternal;
    public static final RawAnimation INTERNAL = RawAnimation.begin().thenPlay("internal");

    public final GeoObjectRenderer<T, ?, ?> RENDERER = new GeoObjectRenderer<>(this);

    public GeoRendererPoseSyncProvider getRendererPoseSync()
    {
        return (GeoRendererPoseSyncProvider) this.RENDERER;
    }

    @Override
    @SuppressWarnings("UnstableApiUsage")
    public @Nullable Animation getBakedAnimation(T animatable, String name)
    {
        if (name.equals("internal") && triggeredInternal != null) return triggeredInternal;
        return super.getBakedAnimation(animatable, name);
    }

    public void triggerInternal(Entity entity, long instanceID, Animation anim)
    {
        this.triggeredInternal = anim;
        this.stopTriggeredAnim(entity, instanceID, "internal", "internal");
        this.triggerAnim(entity, instanceID, "internal", "internal");
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers)
    {
        controllers.add(new AnimationController<>("internal", animationState -> PlayState.STOP).triggerableAnim("internal", INTERNAL));
    }

    public Animation lerpTest()
    {
        return new InternalAnimationBuilder("in", LoopType.HOLD_ON_LAST_FRAME)
        .startBone("cat")
        .lerpSingle(InternalAnimationBuilder.VarType.POSITION, 10, 1, 2, 3, 4, 5, 6, EasingType.EASE_IN_OUT_CUBIC)
        .lerpSingle(InternalAnimationBuilder.VarType.ROTATION, 10, Math.toRadians(30), Math.toRadians(60), Math.toRadians(90), Math.toRadians(120), Math.toRadians(150), Math.toRadians(180), EasingType.EASE_IN_BOUNCE)
        .endBone()
        .startBone("chick")
        .lerpX(InternalAnimationBuilder.VarType.SCALE, 10, 114, 514, EasingType.EASE_IN_BOUNCE)
        .lerpX(InternalAnimationBuilder.VarType.SCALE, 10, 514, 114, EasingType.EASE_IN_OUT_BOUNCE)
        .endBone().build();
    }

    public static class InternalAnimationBuilder
    {
        private final String name;
        private final LoopType loopType;
        public double lengthInTicks = 0;

        public List<BoneAnimation> anims = new ArrayList<>();

        public InternalAnimationBuilder(String name, LoopType loopType)
        {
            this.name = name;
            this.loopType = loopType;
        }

        public InternalBoneAnimationBuilder startBone(String boneName)
        {
            return new InternalBoneAnimationBuilder(boneName, this);
        }

        public Animation build()
        {
            return new Animation(name, lengthInTicks, loopType, anims.toArray(new BoneAnimation[0]), Set.of(), new Animation.KeyframeMarkers(new SoundKeyframeData[0], new ParticleKeyframeData[0], new CustomInstructionKeyframeData[0]));
        }

        public static class InternalBoneAnimationBuilder
        {
            private final String boneName;
            private final InternalAnimationBuilder parent;

            private RawKeyframeStack positionStack = new RawKeyframeStack(new ArrayList<>(), new ArrayList<>(), new ArrayList<>());
            private RawKeyframeStack rotationStack = new RawKeyframeStack(new ArrayList<>(), new ArrayList<>(), new ArrayList<>());
            private RawKeyframeStack scaleStack = new RawKeyframeStack(new ArrayList<>(), new ArrayList<>(), new ArrayList<>());

            public InternalBoneAnimationBuilder(String boneName, InternalAnimationBuilder parent)
            {
                this.boneName = boneName;
                this.parent = parent;
            }

            public InternalBoneAnimationBuilder lerpX(VarType type, double length, double start, double end, EasingType easingType)
            {
                if (type == VarType.SCALE)
                {
                    lerpSingle(type, length, start, end, 1, 1, 1, 1, easingType);
                }
                else lerpSingle(type, length, start, end, 0, 0, 0, 0, easingType);
                return this;
            }

            public InternalBoneAnimationBuilder lerpY(VarType type, double length, double start, double end, EasingType easingType)
            {
                if (type == VarType.SCALE)
                {
                    lerpSingle(type, length, 1, 1, start, end, 1, 1, easingType);
                }
                else lerpSingle(type, length, 0, 0, start, end, 0, 0, easingType);
                return this;
            }

            public InternalBoneAnimationBuilder lerpZ(VarType type, double length, double start, double end, EasingType easingType)
            {
                if (type == VarType.SCALE)
                {
                    lerpSingle(type, length, 1, 1, 1, 1, start, end, easingType);
                }
                else lerpSingle(type, length, 0, 0, 0, 0, start, end, easingType);
                return this;
            }

            public InternalBoneAnimationBuilder lerpSingle(VarType type, double length, double xStart, double xEnd, double yStart, double yEnd, double zStart, double zEnd, EasingType easingType)
            {
                get(type).xKeyframes().add(new Keyframe(0, length, new Constant(xStart), new Constant(xEnd), easingType));
                get(type).yKeyframes().add(new Keyframe(0, length, new Constant(yStart), new Constant(yEnd), easingType));
                get(type).zKeyframes().add(new Keyframe(0, length, new Constant(zStart), new Constant(zEnd), easingType));
                return this;
            }

            public InternalAnimationBuilder endBone()
            {
                KeyframeStack rot = rotationStack.finalizeKeyframe(), pos = positionStack.finalizeKeyframe(), scl = scaleStack.finalizeKeyframe();
                this.parent.anims.add(new BoneAnimation(boneName, rot, pos, scl));
                this.parent.lengthInTicks = Math.max(this.parent.lengthInTicks, Math.max(pos.getTotalKeyframeTime(), Math.max(rot.getTotalKeyframeTime(), scl.getTotalKeyframeTime())));
                return this.parent;
            }

            public RawKeyframeStack get(VarType type)
            {
                return switch (type)
                {
                    case POSITION -> positionStack;
                    case ROTATION -> rotationStack;
                    case SCALE -> scaleStack;
                };
            }
        }

        public enum VarType
        {
            POSITION,
            ROTATION,
            SCALE
        }

        public record RawKeyframeStack(List<Keyframe> xKeyframes, List<Keyframe> yKeyframes, List<Keyframe> zKeyframes)
        {
            public KeyframeStack finalizeKeyframe()
            {
                return new KeyframeStack(xKeyframes, yKeyframes, zKeyframes);
            }
        }
    }
}
