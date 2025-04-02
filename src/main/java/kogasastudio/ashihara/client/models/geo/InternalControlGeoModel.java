package kogasastudio.ashihara.client.models.geo;

import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.SingletonGeoAnimatable;
import software.bernie.geckolib.animation.*;
import software.bernie.geckolib.animation.keyframe.BoneAnimation;
import software.bernie.geckolib.animation.keyframe.Keyframe;
import software.bernie.geckolib.animation.keyframe.KeyframeStack;
import software.bernie.geckolib.animation.keyframe.event.data.CustomInstructionKeyframeData;
import software.bernie.geckolib.animation.keyframe.event.data.ParticleKeyframeData;
import software.bernie.geckolib.animation.keyframe.event.data.SoundKeyframeData;
import software.bernie.geckolib.loading.math.MathValue;
import software.bernie.geckolib.loading.math.value.Constant;
import software.bernie.geckolib.model.GeoModel;

import java.util.ArrayList;
import java.util.List;

public abstract class InternalControlGeoModel<T extends SingletonGeoAnimatable> extends GeoModel<T> implements SingletonGeoAnimatable
{
    public Animation triggeredInternal;
    public static final RawAnimation INTERNAL = RawAnimation.begin().thenPlay("internal");

    @Override
    public @Nullable Animation getAnimation(T animatable, String name)
    {
        if (name.equals("internal") && triggeredInternal != null) return triggeredInternal;
        return super.getAnimation(animatable, name);
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
        controllers.add(new AnimationController<>(this, "internal", animationState -> PlayState.STOP).triggerableAnim("internal", INTERNAL));
    }

    public Animation lerpTo()
    {
        return new InternalAnimationBuilder("in", Animation.LoopType.HOLD_ON_LAST_FRAME)
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
        private final Animation.LoopType loopType;
        public double lengthInTicks = 0;

        public List<BoneAnimation> anims = new ArrayList<>();

        public InternalAnimationBuilder(String name, Animation.LoopType loopType)
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
            return new Animation(name, lengthInTicks, loopType, anims.toArray(new BoneAnimation[0]), new Animation.Keyframes(new SoundKeyframeData[0], new ParticleKeyframeData[0], new CustomInstructionKeyframeData[0]));
        }

        public static class InternalBoneAnimationBuilder
        {
            private final String boneName;
            private final InternalAnimationBuilder parent;

            private KeyframeStack<Keyframe<MathValue>> positionStack = new KeyframeStack<>();
            private KeyframeStack<Keyframe<MathValue>> rotationStack = new KeyframeStack<>();
            private KeyframeStack<Keyframe<MathValue>> scaleStack = new KeyframeStack<>();

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
                get(type).xKeyframes().add(new Keyframe<>(length, new Constant(xStart), new Constant(xEnd), easingType));
                get(type).yKeyframes().add(new Keyframe<>(length, new Constant(yStart), new Constant(yEnd), easingType));
                get(type).zKeyframes().add(new Keyframe<>(length, new Constant(zStart), new Constant(zEnd), easingType));
                return this;
            }

            public InternalAnimationBuilder endBone()
            {
                this.parent.anims.add(new BoneAnimation(boneName, rotationStack, positionStack, scaleStack));
                this.parent.lengthInTicks = Math.max(this.parent.lengthInTicks, Math.max(this.positionStack.getLastKeyframeTime(), Math.max(this.rotationStack.getLastKeyframeTime(), this.scaleStack.getLastKeyframeTime())));
                return this.parent;
            }

            public KeyframeStack<Keyframe<MathValue>> get(VarType type)
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
            SCALE;
        }
    }
}
