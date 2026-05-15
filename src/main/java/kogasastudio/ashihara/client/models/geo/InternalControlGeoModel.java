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
import kogasastudio.ashihara.client.render.geo.GUI3DObjectRenderer;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@SuppressWarnings("rawtypes")
@NullMarked
public abstract class InternalControlGeoModel<T extends SingletonGeoAnimatable> extends GeoModel<T> implements SingletonGeoAnimatable
{
    public @Nullable Animation triggeredInternal;
    public static final RawAnimation INTERNAL = RawAnimation.begin().thenPlay("internal");

    /**
     * 此模型的专属渲染器。类型为 {@link GUI3DObjectRenderer} 以支持骨骼修改中间层；
     * 同时通过 {@link kogasastudio.ashihara.mixin.geckolib.MixinGeoObjectRenderer}
     * 植入了 BoneTracer 列表（用于 OBB 追踪）。
     *
     * <p>使用 {@link #addBoneModifier} / {@link #clearBoneModifiers} 操作骨骼，
     * 而非直接覆写 GeoRenderer 方法。
     */
    public final GUI3DObjectRenderer RENDERER = new GUI3DObjectRenderer<>(this);

    public GeoRendererPoseSyncProvider getRendererPoseSync()
    {
        return (GeoRendererPoseSyncProvider) this.RENDERER;
    }

    /** 注册一个骨骼修改回调，每帧渲染前（动画计算之后）调用。 */
    public void addBoneModifier(GUI3DObjectRenderer.BoneModifier modifier)
    {
        this.RENDERER.addBoneModifier(modifier);
    }

    /** 移除一个骨骼修改回调。 */
    public boolean removeBoneModifier(GUI3DObjectRenderer.BoneModifier modifier)
    {
        return this.RENDERER.removeBoneModifier(modifier);
    }

    /** 清除所有骨骼修改回调（关闭 GUI 时应调用）。 */
    public void clearBoneModifiers()
    {
        this.RENDERER.clearBoneModifiers();
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
                appendLerpSegment(get(type).xKeyframes(), length, xStart, xEnd, easingType);
                appendLerpSegment(get(type).yKeyframes(), length, yStart, yEnd, easingType);
                appendLerpSegment(get(type).zKeyframes(), length, zStart, zEnd, easingType);
                return this;
            }

            private void appendLerpSegment(List<Keyframe> keyframes, double length, double start, double end, EasingType easingType)
            {
                if (keyframes.isEmpty())
                {
                    // Anchor frame: gives GeckoLib a valid "from" keyframe at t=0.
                    keyframes.add(new Keyframe(0, 0, new Constant(start), new Constant(start), easingType));
                }

                final double segmentStart = keyframes.get(keyframes.size() - 1).startTime();
                keyframes.add(new Keyframe(segmentStart + length, length, new Constant(start), new Constant(end), easingType));
            }

            public InternalAnimationBuilder endBone()
            {
                KeyframeStack rot = rotationStack.finalizeKeyframe(), pos = positionStack.finalizeKeyframe(), scl = scaleStack.finalizeKeyframe();
                this.parent.lengthInTicks = Math.max(this.parent.lengthInTicks, Math.max(positionStack.getAnimationDuration(), Math.max(rotationStack.getAnimationDuration(), scaleStack.getAnimationDuration())));
                this.parent.anims.add(new BoneAnimation(boneName, rot, pos, scl));
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

            public double getAnimationDuration()
            {
                return Math.max(getAxisDuration(xKeyframes), Math.max(getAxisDuration(yKeyframes), getAxisDuration(zKeyframes)));
            }

            private double getAxisDuration(List<Keyframe> keyframes)
            {
                if (keyframes.isEmpty())
                {
                    return 0;
                }

                return keyframes.get(keyframes.size() - 1).startTime();
            }
        }
    }
}
