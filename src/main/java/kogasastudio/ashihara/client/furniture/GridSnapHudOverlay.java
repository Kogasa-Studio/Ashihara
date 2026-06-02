package kogasastudio.ashihara.client.furniture;

import kogasastudio.ashihara.Ashihara;
import kogasastudio.ashihara.block.furniture.FurnitureComponentItem;
import kogasastudio.ashihara.utils.GridSnapHelper;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.InteractionHand;
import net.neoforged.neoforge.client.gui.GuiLayer;

public final class GridSnapHudOverlay implements GuiLayer
{
    public static final Identifier LAYER_ID =
        Identifier.fromNamespaceAndPath(Ashihara.MODID, "grid_snap_hud");
    private static final Identifier ICON =
        Identifier.fromNamespaceAndPath(Ashihara.MODID, "gui/grid_tip");

    @Override
    public void render(GuiGraphicsExtractor graphics, DeltaTracker deltaTracker)
    {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return;
        if (!(mc.player.getItemInHand(InteractionHand.MAIN_HAND).getItem() instanceof FurnitureComponentItem)) return;

        int gridStep = GridSnapHelper.getGridStep(mc.player);
        String label = GridSnapHelper.getDisplayLabelPrefix(gridStep);

        int screenHeight = mc.getWindow().getGuiScaledHeight();
        int x = 0;
        int y = screenHeight / 8;

        graphics.blitSprite(RenderPipelines.GUI_TEXTURED, ICON, x, y, 16, 16);

        graphics.text(mc.font, Component.translatable("tooltip.ashihara.grid_level", label), x + 18, y + 4, 0xFFFFFFFF);
    }
}