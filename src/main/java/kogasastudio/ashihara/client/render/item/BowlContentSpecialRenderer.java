package kogasastudio.ashihara.client.render.item;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.mojang.serialization.MapCodec;
import kogasastudio.ashihara.block.furniture.ContainerComponent;
import kogasastudio.ashihara.helper.RenderHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.special.SpecialModelRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.fluids.FluidStack;
import org.joml.Vector3fc;
import org.jspecify.annotations.Nullable;

import java.util.function.Consumer;

public class BowlContentSpecialRenderer implements SpecialModelRenderer<BowlContentSpecialRenderer.OverlayArg>
{
    public static final BowlContentSpecialRenderer INSTANCE = new BowlContentSpecialRenderer();

    public record OverlayArg(@Nullable ItemStack item, @Nullable FluidStack fluid, boolean hasFluid) {}

    @Override
    @Nullable
    public OverlayArg extractArgument(ItemStack stack)
    {
        FluidStack fluid = ContainerComponent.getFluidContent(stack);
        if (!fluid.isEmpty())
        {
            return new OverlayArg(null, fluid, true);
        }

        ItemStack content = ContainerComponent.getContent(stack);
        if (content.isEmpty()) return null;

        if (!content.has(DataComponents.ITEM_MODEL))
        {
            Identifier modelId = Identifier.fromNamespaceAndPath(
                BuiltInRegistries.ITEM.getKey(content.getItem()).getNamespace(),
                BuiltInRegistries.ITEM.getKey(content.getItem()).getPath());
            content.set(DataComponents.ITEM_MODEL, modelId);
        }
        return new OverlayArg(content, null, false);
    }

    @Override
    public void submit(@Nullable OverlayArg arg, PoseStack poseStack, SubmitNodeCollector collector,
        int lightCoords, int overlayCoords, boolean hasFoil, int outlineColor)
    {
        if (arg == null) return;

        if (arg.hasFluid && arg.fluid != null && !arg.fluid.isEmpty())
        {
            poseStack.pushPose();
            poseStack.translate(0.5, 0.2, 0.5);
            poseStack.scale(0.3f, 0.3f, 0.3f);
            var buf = Minecraft.getInstance().renderBuffers().bufferSource();
            RenderHelper.blitFluid(poseStack, buf, arg.fluid,
                2/16f, 14/16f, 2/16f, 14/16f, 0,
                OverlayTexture.NO_OVERLAY, lightCoords);
            buf.endBatch();
            poseStack.popPose();
            return;
        }

        if (arg.item != null && !arg.item.isEmpty())
        {
            poseStack.pushPose();
            poseStack.translate(0.5, 0.25, 0.5);
            poseStack.scale(0.35f, 0.35f, 0.35f);
            poseStack.mulPose(Axis.YP.rotationDegrees(-45));
            RenderHelper.renderItem(poseStack, collector, arg.item,
                ItemDisplayContext.GUI, null, null, 42,
                lightCoords, overlayCoords, outlineColor);
            poseStack.popPose();
        }
    }

    @Override
    public void getExtents(Consumer<Vector3fc> output) {}

    public record Unbaked() implements SpecialModelRenderer.Unbaked<OverlayArg>
    {
        public static final MapCodec<BowlContentSpecialRenderer.Unbaked> MAP_CODEC =
            MapCodec.unit(new BowlContentSpecialRenderer.Unbaked());

        @Override
        @Nullable
        public SpecialModelRenderer<OverlayArg> bake(BakingContext context)
        {
            return BowlContentSpecialRenderer.INSTANCE;
        }

        @Override
        public MapCodec<? extends SpecialModelRenderer.Unbaked<OverlayArg>> type()
        {
            return MAP_CODEC;
        }
    }
}