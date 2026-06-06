package kogasastudio.ashihara.client.render.item;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.mojang.serialization.MapCodec;
import kogasastudio.ashihara.block.furniture.ContainerComponent;
import kogasastudio.ashihara.helper.RenderHelper;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.special.SpecialModelRenderer;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.joml.Vector3fc;
import org.jspecify.annotations.Nullable;

import java.util.function.Consumer;

public class BowlContentSpecialRenderer implements SpecialModelRenderer<ItemStack>
{
    public static final BowlContentSpecialRenderer INSTANCE = new BowlContentSpecialRenderer();

    @Override
    @Nullable
    public ItemStack extractArgument(ItemStack stack)
    {
        ItemStack content = ContainerComponent.getContent(stack);
        if (content.isEmpty()) return null;

        if (!content.has(DataComponents.ITEM_MODEL))
        {
            Identifier modelId = Identifier.fromNamespaceAndPath
            (
                BuiltInRegistries.ITEM.getKey(content.getItem()).getNamespace(),
                BuiltInRegistries.ITEM.getKey(content.getItem()).getPath()
            );
            content.set(DataComponents.ITEM_MODEL, modelId);
        }
        return content;
    }

    @Override
    public void submit
    (
        @Nullable ItemStack content,
        PoseStack poseStack,
        SubmitNodeCollector collector,
        int lightCoords,
        int overlayCoords,
        boolean hasFoil,
        int outlineColor
    )
    {
        if (content == null || content.isEmpty()) return;

        poseStack.pushPose();
        poseStack.translate(0.5, 0.2, 0.5);
        poseStack.scale(0.25f, 0.25f, 0.25f);
        poseStack.mulPose(Axis.YP.rotationDegrees(135));
        poseStack.mulPose(Axis.XP.rotationDegrees(0f));

        RenderHelper.renderItem
        (
            poseStack, collector, content,
            ItemDisplayContext.GUI, null, null, 42,
            lightCoords, overlayCoords, outlineColor
        );

        poseStack.popPose();
    }

    @Override
    public void getExtents(Consumer<Vector3fc> output) {}

    public record Unbaked() implements SpecialModelRenderer.Unbaked<ItemStack>
    {
        public static final MapCodec<BowlContentSpecialRenderer.Unbaked> MAP_CODEC =
            MapCodec.unit(new BowlContentSpecialRenderer.Unbaked());

        @Override
        @Nullable
        public SpecialModelRenderer<ItemStack> bake(SpecialModelRenderer.BakingContext context)
        {
            return BowlContentSpecialRenderer.INSTANCE;
        }

        @Override
        public MapCodec<? extends SpecialModelRenderer.Unbaked<ItemStack>> type()
        {
            return MAP_CODEC;
        }
    }
}