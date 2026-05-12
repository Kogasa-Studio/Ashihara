package kogasastudio.ashihara.client.gui;

import kogasastudio.ashihara.Ashihara;
import kogasastudio.ashihara.registry.KeyMappings;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ActiveTextCollector;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.toasts.Toast;
import net.minecraft.client.gui.components.toasts.ToastManager;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

public class InWorldToolTipTipToast implements Toast
{
    private static final Identifier BACKGROUND_SPRITE = Identifier.fromNamespaceAndPath(Ashihara.MODID, "textures/gui/toast/check_in_world_tooltip.png");
    private static final Component TITLE_TEXT = Component.translatable("gui.ashihara.toast.iwt.staring_at").withColor(11534256);
    private static final Component DESCRIPTION_TEXT = Component.translatable("gui.ashihara.toast.iwt.info.pre").append(KeyMappings.SHOW_IN_WORLD_TOOLTIP.getKey().getDisplayName()).append(Component.translatable("gui.ashihara.toast.iwt.info.post")).withColor(16777216);

    @Override
    public Visibility getWantedVisibility()
    {
        return Visibility.SHOW;
    }

    @Override
    public void update(ToastManager manager, long fullyVisibleForMs)
    {
        if (fullyVisibleForMs >= 5000L)
        {
            manager.hideNowPlayingToast();
        }
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor graphics, Font font, long fullyVisibleForMs)
    {
        graphics.pose().pushMatrix();
        graphics.blit(RenderPipelines.GUI, BACKGROUND_SPRITE, 0, 0, 0, 0, width(), height(), width(), height(), width(), height());
        graphics.drawScrollingString(graphics.textRenderer(), Minecraft.getInstance().font, TITLE_TEXT, 8, 28, 7);
        graphics.drawScrollingString(graphics.textRenderer(), Minecraft.getInstance().font, DESCRIPTION_TEXT, 8, 28, 18);
        graphics.pose().popMatrix();
    }
}
