package kogasastudio.ashihara.client.render.ber.dispatch;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import kogasastudio.ashihara.Ashihara;
import kogasastudio.ashihara.block.building.component.ComponentStateDefinition;
import kogasastudio.ashihara.block.blockentity.MultiBuiltBlockEntity;
import kogasastudio.ashihara.block.furniture.*;
import kogasastudio.ashihara.client.render.state.FurnitureRenderState;
import kogasastudio.ashihara.helper.RenderHelper;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.transfer.item.ItemStacksResourceHandler;

import java.util.HashMap;
import java.util.Map;

public final class FurnitureRenderDispatcher
{
    private static final Map<Class<?>, IFurnitureRenderer<?>> RENDERERS = new HashMap<>();

    /*static
    {
        // Auto-register known containers with the default container renderer
        register(WoodenBowlComponent.class, containerDefault());
    }*/

    private FurnitureRenderDispatcher() {}

    public static <T extends FurnitureComponent & ICustomRender>
    IFurnitureRenderer<T> register(Class<T> clazz, IFurnitureRenderer<T> renderer)
    {
        RENDERERS.put(clazz, renderer);
        return renderer;
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public static FurnitureRenderState dispatchCollect(
        FurnitureComponent fc, ComponentStateDefinition def, MultiBuiltBlockEntity be)
    {
        IFurnitureRenderer renderer = RENDERERS.getOrDefault(fc.getClass(), NONE);
        return renderer.collect(fc, def, be);
    }

    /** 无渲染（未注册自定义渲染时兜底）。 */
    public static final IFurnitureRenderer NONE = (component, def, be) ->
        new FurnitureRenderState(holder -> {});

    /** 容器覆盖层默认渲染：在 inBlockPos 中央渲染物品。 */
    public static <T extends ContainerComponent> IFurnitureRenderer<T> containerDefault()
    {
        return (component, def, be) ->
        new FurnitureRenderState(holder ->
        {
            PoseStack pose = holder.poseStack();
            SubmitNodeCollector collector = holder.submitNodeCollector();
            int packedLight = holder.state().lightCoords;
            if
            (
                component == null
                || !(def.customData() instanceof ContainerContent content)
                || ! (content.handler() instanceof ItemStacksResourceHandler handler)
                || handler.getResource(0).isEmpty()
            ) return;

            pose.pushPose();
            Vec3 pos = def.inBlockPos();
            pose.translate(pos.x, pos.y, pos.z);
            pose.translate(0.5, 0, 0.5);
            if (def.rotationY() != 0) pose.mulPose(Axis.YP.rotationDegrees(def.rotationY()));
            if (def.rotationX() != 0) pose.mulPose(Axis.XP.rotationDegrees(def.rotationX()));
            if (def.rotationZ() != 0) pose.mulPose(Axis.ZP.rotationDegrees(def.rotationZ()));
            pose.translate(0, 0.2, 0);
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

            RenderHelper.renderItem(pose, collector, handler.getResource(0).toStack(), ItemDisplayContext.FIXED, be.getLevel(), null, 42, packedLight, OverlayTexture.NO_OVERLAY, 0);
            pose.popPose();
        });
    }

    public static final IFurnitureRenderer<WoodenBowlComponent> BOWL = register(WoodenBowlComponent.class, containerDefault());
}