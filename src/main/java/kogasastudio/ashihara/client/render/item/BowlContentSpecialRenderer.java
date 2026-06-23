package kogasastudio.ashihara.client.render.item;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.mojang.serialization.MapCodec;
import kogasastudio.ashihara.block.furniture.ContainerComponent;
import kogasastudio.ashihara.block.furniture.ContainerState;
import kogasastudio.ashihara.client.render.StandaloneModelRenderer;
import kogasastudio.ashihara.item.block.FurnitureComponentItem;
import kogasastudio.ashihara.registry.DataComponentTypes;
import kogasastudio.ashihara.registry.FoodModelRegistry;
import kogasastudio.ashihara.helper.RenderHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.block.BlockAndTintGetter;
import net.minecraft.client.renderer.special.SpecialModelRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.fluids.FluidStack;
import org.joml.Vector3fc;
import org.jspecify.annotations.Nullable;

import java.util.function.Consumer;

public class BowlContentSpecialRenderer implements SpecialModelRenderer<BowlContentSpecialRenderer.OverlayArg>
{
    public static final BowlContentSpecialRenderer INSTANCE = new BowlContentSpecialRenderer();

    public record OverlayArg(@Nullable ItemStack item, @Nullable FluidStack fluid, boolean hasFluid, @Nullable String containerCtx, int chopLeft, int maxBites) {}

    @Override
    @Nullable
    public OverlayArg extractArgument(ItemStack stack)
    {
        String ctx = null;
        int cl = 0, mb = 0;
        if (stack.getItem() instanceof FurnitureComponentItem fci && fci.getComponent() instanceof ContainerComponent cc)
        {
            ctx = cc.containerType().contextKey(cc.size());
            mb = cc.maxBites();
        }
        cl = stack.getOrDefault(DataComponentTypes.CHOP_LEFT.get(), 0);
        mb = stack.getOrDefault(DataComponentTypes.MAX_BITES.get(), mb);
        FluidStack fluid = ContainerComponent.getFluidContent(stack);
        if (!fluid.isEmpty())
        {
            return new OverlayArg(null, fluid, true, ctx, cl, mb);
        }

        ItemStack content = ContainerComponent.getContent(stack);
        if (content.isEmpty()) return null;

        if (!content.has(DataComponents.ITEM_MODEL))
        {
            Identifier modelId = Identifier.fromNamespaceAndPath(BuiltInRegistries.ITEM.getKey(content.getItem()).getNamespace(), BuiltInRegistries.ITEM.getKey(content.getItem()).getPath());
            content.set(DataComponents.ITEM_MODEL, modelId);
        }
        return new OverlayArg(content, null, false, ctx, cl, mb);
    }

    @Override
    public void submit(@Nullable OverlayArg arg, PoseStack poseStack, SubmitNodeCollector collector, int lightCoords, int overlayCoords, boolean hasFoil, int outlineColor)
    {
        if (arg == null) return;

        if (arg.hasFluid && arg.fluid != null && !arg.fluid.isEmpty())
        {
            poseStack.pushPose();
            poseStack.translate(4/16f, 0.05f, 12/16f);
            poseStack.mulPose(Axis.XP.rotationDegrees(-90));
            poseStack.scale(1/4f, 1/4f, 1/4f);
            var buf = Minecraft.getInstance().renderBuffers().bufferSource();
            RenderHelper.blitFluid(poseStack, buf, arg.fluid, 0, 1, 0, 1, 0, OverlayTexture.NO_OVERLAY, lightCoords);
            poseStack.popPose();
            return;
        }

        if (arg.item != null && !arg.item.isEmpty())
        {
            var foodKey = arg.containerCtx != null ? FoodModelRegistry.lookup(BuiltInRegistries.ITEM.getKey(arg.item.getItem()), arg.containerCtx, arg.chopLeft > 0 ? arg.chopLeft : arg.maxBites) : null;
            if (foodKey != null)
            {
                poseStack.pushPose();
                Level lvl = Minecraft.getInstance().level;
                var model = Minecraft.getInstance().getModelManager().getStandaloneModel(foodKey);
                var buf = Minecraft.getInstance().renderBuffers().bufferSource();
                if (lvl != null) StandaloneModelRenderer.renderItem(model, (BlockAndTintGetter) lvl, BlockPos.ZERO, Blocks.AIR.defaultBlockState(), poseStack, buf, lightCoords);
                poseStack.popPose();
                return;
            }
            // Fallback: item render
            poseStack.pushPose();
            poseStack.translate(0.5, 0.25, 0.5);
            poseStack.scale(0.35f, 0.35f, 0.35f);
            poseStack.mulPose(Axis.YP.rotationDegrees(-45));
            float scaling = arg.chopLeft > 0 ? (float) arg.chopLeft / arg.maxBites : 1;
            float yDown = arg.chopLeft > 0 ? ((float) (arg.maxBites / arg.chopLeft)) * 0.1f : 0f;
            poseStack.translate(0, -yDown, 0);
            poseStack.scale(scaling, scaling, scaling);
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
