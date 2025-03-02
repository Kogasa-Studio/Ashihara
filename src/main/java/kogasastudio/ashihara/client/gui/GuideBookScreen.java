package kogasastudio.ashihara.client.gui;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import kogasastudio.ashihara.client.models.geo.GuideBookModel;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import software.bernie.geckolib.cache.AnimatableIdCache;

public class GuideBookScreen extends Screen
{
    private final GuideBookModel book = new GuideBookModel();
    private final Player player;
    private final RenderType renderType = RenderType.ENTITY_CUTOUT.apply(book.getTextureResource(book));

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
    protected void init()
    {
        super.init();
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button)
    {
        book.triggerAnim(player, book.hashCode(), "Open", "open");
        return true;
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick)
    {
        PoseStack pose = guiGraphics.pose();
        pose.pushPose();
        pose.translate(122, 122,0);
        pose.mulPose(Axis.XP.rotationDegrees(132));
        pose.mulPose(Axis.YP.rotationDegrees(38));
        pose.mulPose(Axis.ZP.rotationDegrees(181));
        pose.scale(32, -32, 32);
        book.RENDERER.render(guiGraphics.pose(), book, guiGraphics.bufferSource(), renderType, guiGraphics.bufferSource().getBuffer(renderType), 15728880, partialTick);
        pose.popPose();
        super.render(guiGraphics, mouseX, mouseY, partialTick);
    }

    @Override
    public void renderBackground(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick)
    {
        if (this.minecraft == null)
        {
            this.renderBlurredBackground(partialTick);
        }
    }
}
