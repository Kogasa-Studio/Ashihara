package kogasastudio.ashihara.client.gui.overlay;

import kogasastudio.ashihara.Ashihara;
import kogasastudio.ashihara.block.blockentity.MultiBuiltBlockEntity;
import kogasastudio.ashihara.block.furniture.ContainerComponent;
import kogasastudio.ashihara.block.furniture.ContainerContent;
import kogasastudio.ashihara.block.building.component.ComponentStateDefinition;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.client.gui.GuiLayer;
import net.neoforged.neoforge.transfer.item.ItemStacksResourceHandler;

public final class ContainerFoodHudOverlay implements GuiLayer
{
    public static final Identifier LAYER_ID = Identifier.fromNamespaceAndPath(Ashihara.MODID, "container_food_hud");

    @Override
    public void render(GuiGraphicsExtractor graphics, DeltaTracker deltaTracker)
    {
        Minecraft mc = Minecraft.getInstance();
        Player player = mc.player;
        if (player == null) return;

        HitResult hit = mc.hitResult;
        if (!(hit instanceof BlockHitResult bhr)) return;

        BlockEntity be = player.level().getBlockEntity(bhr.getBlockPos());
        if (!(be instanceof MultiBuiltBlockEntity mbe)) return;

        Vec3 inBlock = mbe.inBlockVec(hit.getLocation());
        ComponentStateDefinition def = mbe.getComponentByPosition(inBlock, MultiBuiltBlockEntity.OPCODE_FURNITURE);
        if (!(def != null && def.component() instanceof ContainerComponent cc)) return;
        if (!(def.customData() instanceof ContainerContent content)) return;
        if (!(content.handler() instanceof ItemStacksResourceHandler ih)) return;
        if (ih.getAmountAsLong(0) <= 0) return;

        var food = ih.getResource(0).toStack(1);
        int count = (int) ih.getAmountAsLong(0);
        int cl = content.chopLeft() > 0 ? content.chopLeft() : (int) Math.ceil((double) count * cc.maxBites() / cc.containerStorage());

        int screenW = mc.getWindow().getGuiScaledWidth();
        int screenH = mc.getWindow().getGuiScaledHeight();
        int cx = screenW / 2;

        Component line1 = Component.translatable("tooltip.ashihara.container_food", food.getHoverName(), count);
        graphics.text(mc.font, line1, cx - mc.font.width(line1) / 2, screenH / 2 + 10, 0xFFFFFFFF);

        Component line2 = Component.translatable("tooltip.ashihara.chops_left", cl);
        graphics.text(mc.font, line2, cx - mc.font.width(line2) / 2, screenH / 2 + 22, 0xFFFFFFFF);
    }
}
