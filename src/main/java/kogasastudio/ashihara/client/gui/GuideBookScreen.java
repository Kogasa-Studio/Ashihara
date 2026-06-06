package kogasastudio.ashihara.client.gui;

import com.geckolib.animation.object.EasingType;
import com.geckolib.animation.object.LoopType;
import kogasastudio.ashihara.client.gui3d.Screen3D;
import kogasastudio.ashihara.client.gui3d.util.OBB;
import kogasastudio.ashihara.client.gui3d.util.ObbInterSector;
import kogasastudio.ashihara.client.gui3d.util.Ray;
import kogasastudio.ashihara.client.models.geo.GuideBookModel;
import kogasastudio.ashihara.client.models.geo.InternalControlGeoModel;
import kogasastudio.ashihara.client.render.state.GUI3DComponentRenderState;
import kogasastudio.ashihara.client.render.state.Screen3DPiPRenderState;
import kogasastudio.ashihara.network.GuidebookProgressPacket;
import kogasastudio.ashihara.registry.DataAttachmentTypes;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;
import org.apache.commons.lang3.mutable.MutableFloat;
import oshi.util.tuples.Pair;
import org.jspecify.annotations.Nullable;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/*
 * Thanks to @TT432 for helping to debug animation.
 */
public class GuideBookScreen extends Screen3D
{
    private final GuideBookModel book = new GuideBookModel();
    private final Player player;
    private int coolDown = 0;

    private int currentPageIndex = 0;
    private final Map<Integer, Pair<MutableFloat, String>> flipQueue = new HashMap<>();

    // 3D UI minimum migration: page hit areas in the same GUI-space used by PiP rendering.
    @Nullable
    private OBB leftPageObb;
    @Nullable
    private OBB rightPageObb;

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
        coolDown -= coolDown <= 0 ? 0 : 1;
        List<Integer> toRemove = new ArrayList<>();
        for (int i : flipQueue.keySet())
        {
            flipQueue.get(i).getA().addAndGet(-1);
            if (flipQueue.get(i).getA().floatValue() <= 0)
            {
                toRemove.add(i);
                String anim = flipQueue.get(i).getB();
                String controller = anim.contains("buffer") ? anim : GuideBookModel.CONTROLLER_FLIP;
                if (anim.contains("buffer")) book.stopTriggeredAnim(player, book.hashCode(), controller, anim);
            }
        }
        toRemove.forEach(flipQueue::remove);
        super.tick();
    }

    @Override
    public void init()
    {
        book.triggerAnim(player, book.hashCode(), "Intro", GuideBookModel.ANIM_INTRO);
        this.currentPageIndex = this.player.getData(DataAttachmentTypes.GUIDEBOOK_READING_PAGE.get());
        if (this.currentPageIndex != 0 && this.currentPageIndex <= GuideBookModel.getTotalPages())
        {
            String anim = GuideBookModel.getFlipAnim(currentPageIndex - 1, currentPageIndex, 0);
            if (anim != null)
            {
                book.triggerAnim(player, book.hashCode(), GuideBookModel.CONTROLLER_FLIP, anim);
            }
        }
        book.setPageIndex(currentPageIndex);
        book.updateCurrentPage(false);
        book.triggerInternal(player, book.hashCode(), catchProgress(null).build());
        rebuildPageObbs();
        super.init();
    }

    @Override
    public void resize(int width, int height)
    {
        super.resize(width, height);
        rebuildPageObbs();
    }

    private void rebuildPageObbs()
    {
        // Keep this geometry strictly aligned with extractRenderState PiP bounds.
        final int bookWidth = Math.max(32, Math.min(this.width - 48, 220));
        final int bookHeight = Math.max(32, Math.min(this.height - 84, 148));
        final int bookLeft = this.width / 2 - bookWidth / 2;
        final int bookTop = this.height / 2 - bookHeight / 2 + 4;

        final int spineLeft = this.width / 2 - 5;
        final int pageInset = 8;
        final int pageGap = 3;
        final int obbDepth = 50;

        final int leftStart = bookLeft + pageInset;
        final int leftEnd = Math.max(leftStart + 1, spineLeft - pageGap);
        final int leftWidth = leftEnd - leftStart;

        final int rightStart = spineLeft + 10 + pageGap;
        final int rightEnd = Math.max(rightStart + 1, bookLeft + bookWidth - pageInset);
        final int rightWidth = rightEnd - rightStart;

        this.leftPageObb = new OBB
        (
                new Vector3f(leftWidth * 0.5f, bookHeight * 0.5f, obbDepth * 0.5f),
                new Vector3f(0, 0, 0),
                new Vector3f(leftWidth, bookHeight, obbDepth),
                new Matrix4f().translation(leftStart, bookTop, 0)
        );

        this.rightPageObb = new OBB
        (
                new Vector3f(rightWidth * 0.5f, bookHeight * 0.5f, obbDepth * 0.5f),
                new Vector3f(0, 0, 0),
                new Vector3f(rightWidth, bookHeight, obbDepth),
                new Matrix4f().translation(rightStart, bookTop, 0)
        );
    }

    private Ray createGuidebookPageRay(double mouseX, double mouseY)
    {
        Ray pipRay = this.createMouseRay(mouseX, mouseY);
        float toGui = this.getPickingGuiScale() * this.getPickingPipScale();

        if (!Float.isFinite(toGui) || Math.abs(toGui) < 1e-6f)
        {
            return new Ray(new Vector3f((float) mouseX, (float) mouseY, -2000f), new Vector3f(0f, 0f, 1f));
        }

        float guiX = pipRay.origin().x / toGui + this.getPickingPipX0();
        float guiY = pipRay.origin().y / toGui + this.getPickingPipY0();
        return new Ray(new Vector3f(guiX, guiY, pipRay.origin().z), pipRay.direction());
    }

    public @Nullable Pair<MutableFloat, String> appendBufferedFlip(boolean flipToLeft)
    {
        for (int i = 0; i < 6; i++)
        {
            if (flipQueue.get(i) != null)
            {
                String anim = flipQueue.get(i).getB();
                if ((flipToLeft && anim.contains("left")) || (!flipToLeft && anim.contains("right")))
                {
                    String controller = anim.contains("buffer") ? anim : GuideBookModel.CONTROLLER_FLIP;
                    book.stopTriggeredAnim(player, book.hashCode(), controller, anim);
                    flipQueue.remove(i);
                }
            }
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
    public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick)
    {
        if (coolDown > 0) return super.mouseClicked(event, doubleClick);

        boolean flip = false;
        boolean flipToLeft = false;
        boolean buffered = false;
        boolean resetBuffer = true;

        // Reuse Screen3D picking path, then convert back to GUI-space for current page OBBs.
        final Ray mouseRay = this.createGuidebookPageRay(event.x(), event.y());
        final float tLeft = this.leftPageObb != null ? ObbInterSector.rayOBBIntersect(mouseRay, this.leftPageObb) : -1f;
        final float tRight = this.rightPageObb != null ? ObbInterSector.rayOBBIntersect(mouseRay, this.rightPageObb) : -1f;

        // Left page -> next page (flipToLeft=false); Right page -> previous page (flipToLeft=true).
        if (tLeft >= 0f && (tRight < 0f || tLeft <= tRight) && currentPageIndex < GuideBookModel.getTotalPages())
        {
            flip = true;
        }
        else if (tRight >= 0f && currentPageIndex > 0)
        {
            flip = true;
            flipToLeft = true;
        }

        if (flip)
        {
            String animation = GuideBookModel.getFlipAnim(currentPageIndex, currentPageIndex + (flipToLeft ? -1 : 1), 0);

            if (animation == null) return super.mouseClicked(event, doubleClick);
            if (animation.equals(GuideBookModel.ANIM_FLIP_COMMON_LEFT) || animation.equals(GuideBookModel.ANIM_FLIP_COMMON_RIGHT))
            {
                resetBuffer = false;
                Pair<MutableFloat, String> p = appendBufferedFlip(flipToLeft);
                if (p == null) return super.mouseClicked(event, doubleClick);
                int i = p.getA().intValue();
                animation = p.getB();
                buffered = (i > 0);
                if (animation == null) return super.mouseClicked(event, doubleClick);
            }

            if (resetBuffer) resetBuffer();
            String controller = buffered ? animation : GuideBookModel.CONTROLLER_FLIP;

            book.triggerInternal(player, book.hashCode(), catchProgress(null).build());
            book.triggerAnim(player, book.hashCode(), controller, animation);
            currentPageIndex += flipToLeft ? -1 : 1;
            book.setPageIndex(currentPageIndex);
            book.updateCurrentPage(flipToLeft);
            coolDown = 5;
            return true;
        }
        return super.mouseClicked(event, doubleClick);
    }

    private InternalControlGeoModel.InternalAnimationBuilder catchProgress(InternalControlGeoModel.InternalAnimationBuilder builder)
    {
        if (builder == null)
        {
            builder = new InternalControlGeoModel.InternalAnimationBuilder("catchProgress", LoopType.HOLD_ON_LAST_FRAME);
        }

        {
            double progress = (double) book.getPageIndex() / GuideBookModel.getTotalPages();
            double invertedProgress = 1d - progress;

            builder = builder
            .startBone("pos_sim")
            .lerpX(InternalControlGeoModel.InternalAnimationBuilder.VarType.ROTATION, 10, 0f, Math.toRadians(progress * 160f), EasingType.EASE_IN_OUT_QUAD)
            .endBone()
            .startBone("part_left")
            .lerpY(InternalControlGeoModel.InternalAnimationBuilder.VarType.SCALE, 10, 1f, invertedProgress * 2f, EasingType.EASE_IN_OUT_QUAD)
            .endBone()
            .startBone("part_right")
            .lerpY(InternalControlGeoModel.InternalAnimationBuilder.VarType.SCALE, 10, 1f, progress * 2f, EasingType.EASE_IN_OUT_QUAD)
            .endBone()
            .startBone("content_left")
            .lerpX(InternalControlGeoModel.InternalAnimationBuilder.VarType.ROTATION, 10, 0f, -Math.toRadians(progress * 160f), EasingType.EASE_IN_OUT_QUAD)
            .endBone()
            .startBone("content_right")
            .lerpX(InternalControlGeoModel.InternalAnimationBuilder.VarType.ROTATION, 10, 0f, -Math.toRadians(progress * 160f), EasingType.EASE_IN_OUT_QUAD)
            .endBone()
            .startBone("left")
            .lerpY(InternalControlGeoModel.InternalAnimationBuilder.VarType.POSITION, 10, 0f, invertedProgress * 3f - 1.5f, EasingType.EASE_IN_OUT_QUAD)
            .endBone()
            .startBone("right")
            .lerpY(InternalControlGeoModel.InternalAnimationBuilder.VarType.POSITION, 10, 0f, progress * -3f + 1.5f, EasingType.EASE_IN_OUT_QUAD)
            .endBone()
            .startBone("rightcover")
            .lerpY(InternalControlGeoModel.InternalAnimationBuilder.VarType.POSITION, 10, 0f, progress * 3f - 1.5f, EasingType.EASE_IN_OUT_QUAD)
            .endBone()
            .startBone("leftcover")
            .lerpY(InternalControlGeoModel.InternalAnimationBuilder.VarType.POSITION, 10, 0f, invertedProgress * -3f + 1.5f, EasingType.EASE_IN_OUT_QUAD)
            .endBone();
        }

        return builder;
    }

    @Override
    public void onClose()
    {
        player.setData(DataAttachmentTypes.GUIDEBOOK_READING_PAGE, currentPageIndex);
        ClientPacketDistributor.sendToServer(new GuidebookProgressPacket(0, this.currentPageIndex));
        super.onClose();
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick)
    {
        super.extractRenderState(graphics, mouseX, mouseY, partialTick);

        // Stage2 minimal: submit PiP state to render the 3D book model in GUI.
        final int bookWidth = Math.max(32, Math.min(this.width - 48, 220));
        final int bookHeight = Math.max(32, Math.min(this.height - 84, 148));
        final int bookLeft = this.width / 2 - bookWidth / 2;
        final int bookTop = this.height / 2 - bookHeight / 2 + 4;

        graphics.submitPictureInPictureRenderState(
            new Screen3DPiPRenderState
            (
                bookLeft,
                bookTop,
                bookLeft + bookWidth,
                bookTop + bookHeight,
                1.0f,
                graphics.peekScissorStack(),
                List.of(GUI3DComponentRenderState.of(this.book, this.book.RENDERER, null, new CameraRenderState(), 0xF000F0, partialTick)),
                0xF000F0,
                partialTick
            )
        );

        final int leftColor = this.currentPageIndex < GuideBookModel.getTotalPages() ? 0x66306090 : 0x33202020;
        final int rightColor = this.currentPageIndex > 0 ? 0x66906030 : 0x33202020;

        graphics.fill(0, 0, this.width / 2, this.height, leftColor);
        graphics.fill(this.width / 2, 0, this.width, this.height, rightColor);

        graphics.text(this.font, "GuideBook PiP 3D + fallback overlay", 6, 6, 0xFFFFFF, true);
        graphics.text(this.font, "pageIndex=" + this.currentPageIndex + "/" + GuideBookModel.getTotalPages(), 6, 18, 0xE0E0E0, false);
        graphics.text(this.font, "Left half: next page", 6, this.height - 24, 0xC8D8FF, false);
        graphics.text(this.font, "Right half: previous page", this.width / 2 + 6, this.height - 24, 0xFFD8C8, false);

        if (!this.flipQueue.isEmpty())
        {
            graphics.text(this.font, "buffered flips=" + this.flipQueue.size(), 6, 30, 0xFFD54F, false);
        }

        if (this.coolDown > 0)
        {
            graphics.text(this.font, "cooldown=" + this.coolDown, 6, 42, 0xFF8A80, false);
        }
    }
}
