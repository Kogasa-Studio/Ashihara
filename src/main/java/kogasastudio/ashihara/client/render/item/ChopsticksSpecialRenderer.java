package kogasastudio.ashihara.client.render.item;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.serialization.MapCodec;
import kogasastudio.ashihara.client.render.StandaloneModelRenderer;
import kogasastudio.ashihara.datacomponent.ChopsticksFood;
import kogasastudio.ashihara.helper.RenderHelper;
import kogasastudio.ashihara.registry.DataComponentTypes;
import kogasastudio.ashihara.registry.FoodModelRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.block.BlockAndTintGetter;
import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
import net.minecraft.client.renderer.special.SpecialModelRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.client.model.standalone.StandaloneModelKey;
import org.joml.Vector3fc;
import org.jspecify.annotations.Nullable;

import java.util.function.Consumer;

public class ChopsticksSpecialRenderer implements SpecialModelRenderer<ChopsticksSpecialRenderer.OverlayArg>
{
    public static final ChopsticksSpecialRenderer INSTANCE = new ChopsticksSpecialRenderer();

    public record OverlayArg(@Nullable ItemStack food, @Nullable StandaloneModelKey<BlockStateModel> modelKey) {}

    @Override
    @Nullable
    public OverlayArg extractArgument(ItemStack stack)
    {
        ChopsticksFood food = stack.getOrDefault(DataComponentTypes.CHOPSTICKS_FOOD.get(), ChopsticksFood.EMPTY);
        if (food.food().isEmpty()) return null;
        var foodKey = FoodModelRegistry.lookupExact(BuiltInRegistries.ITEM.getKey(food.food().getItem()), "on_chopsticks");
        return new OverlayArg(food.food(), foodKey);
    }

    @Override
    public void submit(@Nullable OverlayArg arg, PoseStack poseStack, SubmitNodeCollector collector, int lightCoords, int overlayCoords, boolean hasFoil, int outlineColor)
    {
        if (arg == null || arg.food == null) return;

        poseStack.pushPose();

        if (arg.modelKey != null)
        {
            var lvl = Minecraft.getInstance().level;
            if (lvl != null)
            {
                var model = Minecraft.getInstance().getModelManager().getStandaloneModel(arg.modelKey);
                var buf = Minecraft.getInstance().renderBuffers().bufferSource();
                StandaloneModelRenderer.renderItem(model, lvl, BlockPos.ZERO, Blocks.AIR.defaultBlockState(), poseStack, buf, lightCoords);
            }
        }
        else
        {
            poseStack.translate(0.5, 0, 0.5);
            poseStack.scale(0.3f, 0.3f, 0.3f);
            RenderHelper.renderItem(poseStack, collector, arg.food, ItemDisplayContext.FIXED, Minecraft.getInstance().level, null, 42, lightCoords, overlayCoords, outlineColor);
        }

        poseStack.popPose();
    }

    @Override
    public void getExtents(Consumer<Vector3fc> output) {}

    public record Unbaked() implements SpecialModelRenderer.Unbaked<OverlayArg>
    {
        public static final MapCodec<ChopsticksSpecialRenderer.Unbaked> MAP_CODEC = MapCodec.unit(new ChopsticksSpecialRenderer.Unbaked());

        @Override
        @Nullable
        public SpecialModelRenderer<OverlayArg> bake(BakingContext context)
        {
            return ChopsticksSpecialRenderer.INSTANCE;
        }

        @Override
        public MapCodec<? extends SpecialModelRenderer.Unbaked<OverlayArg>> type()
        {
            return MAP_CODEC;
        }
    }
}
