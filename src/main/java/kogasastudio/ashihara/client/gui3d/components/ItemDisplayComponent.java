package kogasastudio.ashihara.client.gui3d.components;

import com.mojang.blaze3d.vertex.PoseStack;
import kogasastudio.ashihara.client.render.state.GUI3DComponentRenderState;
import kogasastudio.ashihara.helper.RenderHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.joml.Matrix4f;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class ItemDisplayComponent extends AbstractComponent
{
    public Supplier<ItemStack> item;
    public Supplier<Matrix4f> transform;
    public float itemRenderScale = 1f;

    public ItemDisplayComponent(Supplier<ItemStack> item, Supplier<Matrix4f> transform)
    {
        this.item = item;
        this.transform = transform;
    }

    public Consumer<PoseStack> getItemTranslate()
    {
        return (poseStack) ->
        {
            poseStack.scale(-this.itemRenderScale, this.itemRenderScale, this.itemRenderScale);
        };
    }

    @Override
    public void collectRenderStates(List<GUI3DComponentRenderState> output, int mouseX, int mouseY, float partialTick)
    {
        super.collectRenderStates(output, mouseX, mouseY, partialTick);
        if (this.item.get() == null || this.transform.get() == null) return;
        ItemStack stack = this.item.get();
        if (stack.isEmpty())
        {
            return;
        }

        output.add(new GUI3DComponentRenderState
        ((poseStack, submitNodeCollector) ->
        {
            poseStack.pushPose();
            poseStack.last().pose().set(this.transform.get());
            this.getItemTranslate().accept(poseStack);
            poseStack.last().normal().identity();
            RenderHelper.renderItem
            (
                poseStack,
                submitNodeCollector,
                stack,
                ItemDisplayContext.GUI,
                Minecraft.getInstance().level,
                Minecraft.getInstance().player,
                42,
                15728880,
                OverlayTexture.NO_OVERLAY,
                0
            );
            poseStack.scale(1/32f, -1/32f, 1/32f);
            poseStack.translate(0, 0, 2f);
            submitNodeCollector.submitText(poseStack, 8f, 8f, Language.getInstance().getVisualOrder(FormattedText.of(String.valueOf(this.item.get().count()))), true, Font.DisplayMode.NORMAL, 15728880, 0xffa7d888, 0, 0);
            poseStack.popPose();
        }));
    }
}
