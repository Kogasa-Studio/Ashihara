package kogasastudio.ashihara.client.gui.hud;

import kogasastudio.ashihara.Ashihara;
import kogasastudio.ashihara.registry.DataAttachmentTypes;
import kogasastudio.ashihara.registry.Items;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.level.GameType;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderGuiEvent;

@EventBusSubscriber(modid = Ashihara.MODID)
public class FlightHudOverlay
{
    private static final int BAR_WIDTH = 8;
    private static final int BAR_HEIGHT = 64;
    private static final int THRESHOLD = 100;

    @SubscribeEvent
    public static void onRenderGui(RenderGuiEvent.Pre event)
    {
        var mc = Minecraft.getInstance();
        var player = mc.player;
        if (player == null) return;
        if (!player.getItemBySlot(EquipmentSlot.CHEST).is(Items.HAGOROMO)) return;

        if (mc.gameMode == null) return;
        var mode = mc.gameMode.getPlayerMode();
        if (mode != GameType.SURVIVAL && mode != GameType.ADVENTURE) return;

        var data = player.getData(DataAttachmentTypes.HAGOROMO_FLIGHT);
        var gui = event.getGuiGraphics();

        int screenHeight = gui.guiHeight();
        int screenWidth = gui.guiWidth();

        int x = screenWidth - 12;
        int y = (screenHeight - BAR_HEIGHT) / 2;

        gui.fill(x - 1, y - 1, x + BAR_WIDTH + 1, y + BAR_HEIGHT + 1, 0xFF333333);

        if (data.hasFlight())
        {
            int progress = Math.min(data.airborneTicks(), THRESHOLD);
            int fillHeight = BAR_HEIGHT - (BAR_HEIGHT * progress / THRESHOLD);
            if (fillHeight > 0)
                gui.fill(x, y + BAR_HEIGHT - fillHeight, x + BAR_WIDTH, y + BAR_HEIGHT, 0xFFFFFFFF);
        }
        else if (data.cooldownTicks() > 0)
        {
            int fillHeight = BAR_HEIGHT * data.cooldownTicks() / THRESHOLD;
            if (fillHeight > 0)
                gui.fill(x, y, x + BAR_WIDTH, y + fillHeight, 0xFFFFFFFF);
        }
    }
}