package kogasastudio.ashihara.client.gui;

import kogasastudio.ashihara.Ashihara;
import kogasastudio.ashihara.registry.KeyMappings;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.toasts.Toast;
import net.minecraft.client.gui.components.toasts.ToastComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class InWorldToolTipTipToast implements Toast
{
    private static final ResourceLocation BACKGROUND_SPRITE = ResourceLocation.fromNamespaceAndPath(Ashihara.MODID, "textures/gui/toast/check_in_world_tooltip.png");
    private static final Component TITLE_TEXT = Component.translatable("gui.ashihara.toast.iwt.staring_at");
    private static final Component DESCRIPTION_TEXT = Component.translatable("gui.ashihara.toast.iwt.info.pre").append(KeyMappings.SHOW_IN_WORLD_TOOLTIP.getKey().getDisplayName()).append(Component.translatable("gui.ashihara.toast.iwt.info.post"));

    @Override
    public Visibility render(GuiGraphics guiGraphics, ToastComponent toastComponent, long timeSinceLastVisible)
    {
        guiGraphics.pose().pushPose();
        guiGraphics.blit(BACKGROUND_SPRITE, 0, 0, 0, width(), height(), width(), height(), width(), height());
        guiGraphics.drawString(toastComponent.getMinecraft().font, TITLE_TEXT, 8, 7, -11534256, false);
        guiGraphics.drawString(toastComponent.getMinecraft().font, DESCRIPTION_TEXT, 8, 18, -16777216, false);
        guiGraphics.pose().popPose();
        return (double)(timeSinceLastVisible) >= 5000.0 * toastComponent.getNotificationDisplayTimeMultiplier()
        ? Toast.Visibility.HIDE
        : Toast.Visibility.SHOW;
    }
}
