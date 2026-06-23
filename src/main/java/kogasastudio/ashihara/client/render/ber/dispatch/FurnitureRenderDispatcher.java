package kogasastudio.ashihara.client.render.ber.dispatch;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import kogasastudio.ashihara.block.building.component.ComponentStateDefinition;
import kogasastudio.ashihara.block.blockentity.MultiBuiltBlockEntity;
import kogasastudio.ashihara.block.furniture.*;
import kogasastudio.ashihara.client.render.StandaloneModelRenderer;
import kogasastudio.ashihara.client.render.state.FurnitureRenderState;
import kogasastudio.ashihara.helper.RenderHelper;
import kogasastudio.ashihara.registry.FoodModelRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.block.BlockAndTintGetter;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.transfer.fluid.FluidStacksResourceHandler;
import net.neoforged.neoforge.transfer.item.ItemStacksResourceHandler;

import java.util.HashMap;
import java.util.Map;

public final class FurnitureRenderDispatcher
{
    private static final Map<Class<?>, IFurnitureRenderer<?>> RENDERERS = new HashMap<>();

    private FurnitureRenderDispatcher() {}

    public static <T extends FurnitureComponent & ICustomRender>
    IFurnitureRenderer<T> register(Class<T> clazz, IFurnitureRenderer<T> renderer)
    {
        RENDERERS.put(clazz, renderer);
        return renderer;
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public static FurnitureRenderState dispatchCollect(FurnitureComponent fc, ComponentStateDefinition def, MultiBuiltBlockEntity be)
    {
        IFurnitureRenderer renderer = RENDERERS.getOrDefault(fc.getClass(), NONE);
        return renderer.collect(fc, def, be);
    }

    public static final IFurnitureRenderer NONE = (component, def, be) -> new FurnitureRenderState(holder -> {});

    public static <T extends ContainerComponent> IFurnitureRenderer<T> containerDefault()
    {
        return (component, def, be) ->
        new FurnitureRenderState(holder ->
        {
            PoseStack pose = holder.poseStack();
            SubmitNodeCollector collector = holder.submitNodeCollector();
            int packedLight = holder.state().lightCoords;
            if (component == null || !(def.customData() instanceof ContainerContent content)) return;

            // ---- FLUID rendering ----
            if (content.handler() instanceof FluidStacksResourceHandler fluidHandler && fluidHandler.getAmountAsLong(0) > 0)
            {
                FluidStack fluid = fluidHandler.getResource(0).toStack((int) fluidHandler.getAmountAsLong(0));
                pose.pushPose();
                Vec3 pos = def.inBlockPos();
                pose.translate(pos.x, pos.y, pos.z);
                if (def.rotationY() != 0) pose.mulPose(Axis.YP.rotationDegrees(def.rotationY()));
                if (def.rotationX() != 0) pose.mulPose(Axis.XP.rotationDegrees(def.rotationX()));
                if (def.rotationZ() != 0) pose.mulPose(Axis.ZP.rotationDegrees(def.rotationZ()));
                pose.translate(4/16f, 0.05f, 12/16f);
                pose.mulPose(Axis.XP.rotationDegrees(-90));
                pose.scale(1/4f, 1/4f, 1/4f);
                var buf = Minecraft.getInstance().renderBuffers().bufferSource();
                RenderHelper.blitFluid(pose, buf, fluid, 0, 1, 0, 1, 0, OverlayTexture.NO_OVERLAY, packedLight);
                pose.popPose();
                return;
            }

            // ---- ITEM rendering ----
            if (!(content.handler() instanceof ItemStacksResourceHandler handler) || handler.getResource(0).isEmpty()) return;

            var itemStack = handler.getResource(0).toStack(1);
            int chopLeft = content.chopLeft();
            int maxBites = component.maxBites();
            var foodKey = FoodModelRegistry.lookup(BuiltInRegistries.ITEM.getKey(itemStack.getItem()), component.containerType().contextKey(component.size()), chopLeft > 0 ? chopLeft : maxBites);

            pose.pushPose();
            Vec3 pos = def.inBlockPos();
            pose.translate(pos.x, pos.y, pos.z);
            pose.translate(0.5, 0, 0.5);
            if (def.rotationY() != 0) pose.mulPose(Axis.YP.rotationDegrees(def.rotationY()));
            if (def.rotationX() != 0) pose.mulPose(Axis.XP.rotationDegrees(def.rotationX()));
            if (def.rotationZ() != 0) pose.mulPose(Axis.ZP.rotationDegrees(def.rotationZ()));
            pose.translate(0, 0.2, 0);

            if (foodKey != null)
            {
                pose.translate(-0.5f, -0.2f, -0.5f);
                var model = Minecraft.getInstance().getModelManager().getStandaloneModel(foodKey);
                var buf = Minecraft.getInstance().renderBuffers().bufferSource();
                StandaloneModelRenderer.render(model, (BlockAndTintGetter) be.getLevel(), be.getBlockPos(), be.getBlockState(), pose, buf, packedLight);
            }
            else
            {
                int hash = Float.floatToIntBits((float) pos.x)
                    ^ Float.floatToIntBits((float) pos.y) * 31
                    ^ Float.floatToIntBits((float) pos.z) * 37
                    ^ Float.floatToIntBits(be.getBlockPos().getX()) * 41
                    ^ Float.floatToIntBits(be.getBlockPos().getY()) * 43
                    ^ Float.floatToIntBits(be.getBlockPos().getZ()) * 47;
                float deg = Math.abs(hash % 360);
                pose.mulPose(Axis.YP.rotationDegrees(deg));
                pose.mulPose(Axis.XP.rotationDegrees(Math.clamp(deg / 4f, -45, 45)));
                pose.scale(1/4f, 1/4f, 1/4f);
                float scaling = chopLeft > 0 ? (float) chopLeft / maxBites : 1;
                float yDown = chopLeft > 0 ? ((float) (maxBites / chopLeft)) * 0.1f : 0f;
                pose.translate(0, -yDown, 0);
                pose.scale(scaling, scaling, scaling);
                RenderHelper.renderItem(pose, collector, itemStack, ItemDisplayContext.FIXED, be.getLevel(), null, 42, packedLight, OverlayTexture.NO_OVERLAY, 0);
            }
            pose.popPose();
        });
    }

    public static final IFurnitureRenderer<WoodenBowlComponent> BOWL = register(WoodenBowlComponent.class, containerDefault());
}