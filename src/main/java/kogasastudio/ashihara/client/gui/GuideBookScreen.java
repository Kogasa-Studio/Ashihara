package kogasastudio.ashihara.client.gui;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import kogasastudio.ashihara.client.models.geo.GuideBookModel;
import kogasastudio.ashihara.client.models.geo.InternalControlGeoModel;
import kogasastudio.ashihara.network.GuidebookProgressPacket;
import kogasastudio.ashihara.registry.DataComponentTypes;
import kogasastudio.ashihara.utils.OptionalUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.PacketDistributor;
import org.apache.commons.lang3.mutable.MutableFloat;
import oshi.util.tuples.Pair;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.animation.Animation;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.EasingType;
import software.bernie.geckolib.cache.object.GeoBone;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static kogasastudio.ashihara.utils.OptionalUtil.getWithDefault;

public class GuideBookScreen extends Screen
{
    private final GuideBookModel book = new GuideBookModel();
    private final Player player;
    private final RenderType renderType = RenderType.ENTITY_CUTOUT.apply(book.getTextureResource(book));
    private int ticks = 0;
    private int coolDown = 0;

    private int currentPageIndex = 145;
    private Map<Integer, Pair<MutableFloat, String>> flipQueue = new HashMap<>();

    public GuideBookScreen(Component title, Player player)
    {
        super(title);
        this.player = player;
    }

    @Override
    public boolean isPauseScreen()
    {
        return false;
    }

    @Override
    public void tick()
    {
        ticks += 1;
        coolDown -= coolDown <= 0 ? 0 : 1;
        List<Integer> toRemove = new ArrayList<>();
        for (int i : flipQueue.keySet())
        {
            flipQueue.get(i).getA().addAndGet(-1);
            if (flipQueue.get(i).getA().getValue() <= 0)
            {
                toRemove.add(i);
                String anim = flipQueue.get(i).getB();
                String controller = anim.contains("buffer") ? anim : GuideBookModel.CONTROLLER_FLIP;
                if (anim.contains("buffer")) book.stopTriggeredAnim(player, book.hashCode(), controller, anim);
            }
        }
        toRemove.forEach(i -> flipQueue.remove(i));
        super.tick();
    }

    @Override
    protected void init()
    {
        book.triggerAnim(player, book.hashCode(), "Intro", GuideBookModel.ANIM_INTRO);
        //this.currentPageIndex = this.player.getData(DataComponentTypes.GUIDEBOOK_READING_PAGE.get());
        if (this.currentPageIndex != 0 && this.currentPageIndex <= GuideBookModel.getTotalPages())
        {
            String anim = GuideBookModel.getFlipAnim(currentPageIndex - 1, currentPageIndex, 0);
            book.triggerAnim(player, book.hashCode(), GuideBookModel.CONTROLLER_FLIP, anim);
        }
        book.setPageIndex(currentPageIndex);
        book.triggerInternal(player, book.hashCode(), catchProgress(null).build());
        super.init();
    }

    public Pair<MutableFloat, String> appendBufferedFlip(boolean flipToLeft)
    {
        for (int i = 0; i < 6; i++)
        {
            if (!flipQueue.containsKey(i))
            {
                String animation = GuideBookModel.getFlipAnim(currentPageIndex, currentPageIndex + (flipToLeft ? -1 : 1), i);
                if (animation == null) return null;
                Pair<MutableFloat, String> p = new Pair<>(new MutableFloat(30f), animation);
                flipQueue.put(i, p);
                return new Pair<>(new MutableFloat(i), animation);
            }
        }
        return null;
    }

    public void resetBuffer()
    {
        for (int i : flipQueue.keySet())
        {
            flipQueue.get(i).getA().setValue(0);
        }
    }

    /*@Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers)
    {
        if (keyCode == 87)
        {
            book.getAnimatableInstanceCache().getManagerForId(book.hashCode()).getAnimationControllers().get("use.close_from_left").forceAnimationReset();
            book.triggerAnim(player, book.hashCode(), "use.close_from_left", "use.close_from_left");
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }*/

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button)
    {
        if (coolDown > 0) return super.mouseClicked(mouseX, mouseY, button);
        boolean flip = false;
        boolean flipToLeft = false;
        boolean buffered = false;
        boolean resetBuffer = true;
        if (mouseX <= this.width / 2f && currentPageIndex <= GuideBookModel.getTotalPages()) flip = true;
        if (mouseX >= this.width / 2f && currentPageIndex > 0) {flip = true; flipToLeft = true;}

        if (flip)
        {
            String animation = GuideBookModel.getFlipAnim(currentPageIndex, currentPageIndex + (flipToLeft ? -1 : 1), 0);

            if (animation == null) return super.mouseClicked(mouseX, mouseY, button);
            if (animation.equals(GuideBookModel.ANIM_FLIP_COMMON_LEFT) || animation.equals(GuideBookModel.ANIM_FLIP_COMMON_RIGHT))
            {
                resetBuffer = false;
                Pair<MutableFloat, String> p = appendBufferedFlip(flipToLeft);
                int i = p.getA().getValue().intValue();
                animation = p.getB();
                buffered = (i > 0);
                if (animation == null) return super.mouseClicked(mouseX, mouseY, button);
            }

            //book.stopTriggeredAnim(player, book.hashCode(), controller, animation);
            if (resetBuffer) resetBuffer();
            String controller = buffered ? animation : GuideBookModel.CONTROLLER_FLIP;
            book.getAnimatableInstanceCache().getManagerForId(book.hashCode()).getAnimationControllers().get(controller).forceAnimationReset();

            book.triggerInternal(player, book.hashCode(), catchProgress(null).build());
            book.triggerAnim(player, book.hashCode(), controller, animation);
            currentPageIndex += flipToLeft ? -1 : 1;
            book.setPageIndex(currentPageIndex);
            coolDown = 5;
            return true;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    private InternalControlGeoModel.InternalAnimationBuilder catchProgress(InternalControlGeoModel.InternalAnimationBuilder builder)
    {
        if (builder == null) builder = new InternalControlGeoModel.InternalAnimationBuilder("catchProgress", Animation.LoopType.HOLD_ON_LAST_FRAME);
        {
            double progress = (double) book.getPageIndex() / GuideBookModel.getTotalPages();
            double invertedProgress = 1d - progress;
            builder = builder
            .startBone("pos_sim")
            .lerpX(InternalControlGeoModel.InternalAnimationBuilder.VarType.ROTATION, 10, getWithDefault(0f, book.getBone("pos_sim"), GeoBone::getRotX), Math.toRadians(progress * 160f), EasingType.EASE_IN_OUT_QUAD)
            .endBone()
            .startBone("part_left")
            .lerpY(InternalControlGeoModel.InternalAnimationBuilder.VarType.SCALE, 10, getWithDefault(1f, book.getBone("part_left"), GeoBone::getScaleY), invertedProgress * 2f, EasingType.EASE_IN_OUT_QUAD)
            .endBone()
            .startBone("part_right")
            .lerpY(InternalControlGeoModel.InternalAnimationBuilder.VarType.SCALE, 10, getWithDefault(1f, book.getBone("part_right"), GeoBone::getScaleY), progress * 2f, EasingType.EASE_IN_OUT_QUAD)
            .endBone()
            .startBone("content_left")
            .lerpX(InternalControlGeoModel.InternalAnimationBuilder.VarType.ROTATION, 10, getWithDefault(0f, book.getBone("content_left"), GeoBone::getRotX), -Math.toRadians(progress * 160f), EasingType.EASE_IN_OUT_QUAD)
            .endBone()
            .startBone("content_right")
            .lerpX(InternalControlGeoModel.InternalAnimationBuilder.VarType.ROTATION, 10, getWithDefault(0f, book.getBone("content_right"), GeoBone::getRotX), -Math.toRadians(progress * 160f), EasingType.EASE_IN_OUT_QUAD)
            .endBone()
            .startBone("left")
            .lerpY(InternalControlGeoModel.InternalAnimationBuilder.VarType.POSITION, 10, getWithDefault(0f, book.getBone("left"), GeoBone::getPosY), invertedProgress * 3f - 1.5f, EasingType.EASE_IN_OUT_QUAD)
            .endBone()
            .startBone("right")
            .lerpY(InternalControlGeoModel.InternalAnimationBuilder.VarType.POSITION, 10, getWithDefault(0f, book.getBone("right"), GeoBone::getPosY), progress * -3f + 1.5f, EasingType.EASE_IN_OUT_QUAD)
            .endBone()
            .startBone("rightcover")
            .lerpY(InternalControlGeoModel.InternalAnimationBuilder.VarType.POSITION, 10, getWithDefault(0f, book.getBone("rightcover"), GeoBone::getPosY), progress * 3f - 1.5f, EasingType.EASE_IN_OUT_QUAD)
            .endBone()
            .startBone("leftcover")
            .lerpY(InternalControlGeoModel.InternalAnimationBuilder.VarType.POSITION, 10, getWithDefault(0f, book.getBone("leftcover"), GeoBone::getPosY), invertedProgress * -3f + 1.5f, EasingType.EASE_IN_OUT_QUAD)
            .endBone();/*
            .startBone("buffer_pages")
            .lerpY(InternalControlGeoModel.InternalAnimationBuilder.VarType.POSITION, 10, getWithDefault(0f, book.getBone("buffer_pages"), GeoBone::getPosY), progress * -3f + 1.5f, EasingType.EASE_IN_OUT_QUAD)
            .lerpX(InternalControlGeoModel.InternalAnimationBuilder.VarType.ROTATION, 10, getWithDefault(0f, book.getBone("buffer_pages"), GeoBone::getRotX), Math.toRadians(progress * -160f), EasingType.EASE_IN_OUT_QUAD)
            .endBone();
            /*.startBone("spine")
            .lerpX(InternalControlGeoModel.InternalAnimationBuilder.VarType.ROTATION, 10, getWithDefault(0f, book.getBone("spine"), GeoBone::getRotX), Math.toRadians(book.getPageIndex() / 150f * 160f), EasingType.EASE_IN_OUT_QUAD)
            .endBone();
            /*.startBone("spine_pos")
            .lerpY(InternalControlGeoModel.InternalAnimationBuilder.VarType.POSITION, 10, getWithDefault(0f, book.getBone("spine_pos"), GeoBone::getPosY), book.getPageIndex() / -150f*3, EasingType.EASE_IN_OUT_QUAD)
            .endBone();*/
        }
        return builder;
    }

    @Override
    public void onClose()
    {
        PacketDistributor.sendToServer(new GuidebookProgressPacket(0, this.currentPageIndex));
        super.onClose();
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick)
    {
        PoseStack pose = guiGraphics.pose();
        pose.pushPose();
        pose.translate((float) (this.width / 2d - 72.5), (float) (this.height - 40), 0);
        pose.mulPose(Axis.YP.rotationDegrees(90));
        pose.scale(64, -64, 64);
        book.RENDERER.render(guiGraphics.pose(), book, guiGraphics.bufferSource(), renderType, guiGraphics.bufferSource().getBuffer(renderType), 15728880, partialTick);
        pose.popPose();

        pose.pushPose();
        guiGraphics.drawString(Minecraft.getInstance().font, "X: " + mouseX + ", Y: " + mouseY + ", Current page: " + currentPageIndex, 0, 0, 0xffffff);
        pose.popPose();

        super.render(guiGraphics, mouseX, mouseY, partialTick);
    }

    @Override
    public void renderBackground(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick)
    {
        //this.renderBlurredBackground(partialTick);
    }
}
