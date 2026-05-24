package kogasastudio.ashihara.client.render.ber;

import com.geckolib.cache.model.GeoBone;
import com.geckolib.util.RenderUtil;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import kogasastudio.ashihara.block.blockentity.MortarBE;
import kogasastudio.ashihara.client.gui3d.util.BoneTracer;
import kogasastudio.ashihara.client.models.geo.UIPanelModel;
import kogasastudio.ashihara.client.render.SectionRenderContext;
import kogasastudio.ashihara.client.render.WithLevelRenderer;
import kogasastudio.ashihara.inventory.BEItemStackHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MortarTER implements BlockEntityRenderer<MortarBE, BlockEntityRenderState>, WithLevelRenderer<MortarBE>, InWorldToolTipBER<MortarBE>
{
    public Map<Integer, ItemStack> items = new HashMap<>(8);
    private final ItemModelResolver itemModelResolver;
    private float partialTicks = 0f;
    public MortarTER(BlockEntityRendererProvider.Context context)
    {
        this.itemModelResolver = context.itemModelResolver();
    }

    @Override
    public void renderStatic(SectionRenderContext context, ModelRenderer renderer)
    {
    }

    @Override
    public void renderInfo(MortarBE be, PoseStack poseStack, UIPanelModel animatable, MultiBufferSource bufferSource, RenderType renderType, VertexConsumer buffer, int packedLight, float partialTick)
    {
    }

    @Override
    public BlockEntityRenderState createRenderState()
    {
        return new BlockEntityRenderState();
    }

    @Override
    public void submit(BlockEntityRenderState state, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, CameraRenderState camera)
    {
        if (Minecraft.getInstance().level == null) return;
        BlockEntity be = Minecraft.getInstance().level.getBlockEntity(state.blockPos);
        if (!(be instanceof MortarBE mortarBE) || mortarBE.inventory.isEmpty()) return;
        syncItem(mortarBE.inventory);
        poseStack.pushPose();
        poseStack.translate(0, -0.5, 0);
        mortarBE.item_display_positions.RENDERER.performRenderPass(mortarBE.item_display_positions, null, poseStack, submitNodeCollector, camera, state.lightCoords, partialTicks);
        poseStack.popPose();
        for (int i = 0; i < this.items.size(); ++i)
        {
            ItemStack item = this.items.get(i);
            BoneTracer tracer = mortarBE.boneTracers.get("level" + i);
            if (tracer == null || tracer.collisionBoxes().isEmpty()) continue;
            ItemStackRenderState itemState = new ItemStackRenderState();
            this.itemModelResolver.updateForTopItem(itemState, item, ItemDisplayContext.FIXED, mortarBE.getLevel(), null, 42);
            GeoBone bone = tracer.snapshot().getBone();
            poseStack.pushPose();
            poseStack.scale(1f/16f, 1f/16f, 1f/16f);
            poseStack.translate(bone.pivotX(), bone.pivotY(), bone.pivotZ());

            poseStack.pushPose();

            poseStack.translate(8f, 0, 8f);

            RenderUtil.translateAndRotateMatrixForBone(poseStack, bone);
            poseStack.mulPose(Axis.XP.rotationDegrees(90));
            poseStack.translate(0, -1/16f, 0);

            poseStack.scale(6f, 6f, 6f);
            itemState.submit(poseStack, submitNodeCollector, state.lightCoords, OverlayTexture.NO_OVERLAY, 0);
            poseStack.popPose();

            poseStack.popPose();
        }
    }

    @Override
    public void extractRenderState(MortarBE blockEntity, BlockEntityRenderState state, float partialTicks, Vec3 cameraPosition, ModelFeatureRenderer.@Nullable CrumblingOverlay breakProgress)
    {
        BlockEntityRenderer.super.extractRenderState(blockEntity, state, partialTicks, cameraPosition, breakProgress);
        this.partialTicks = partialTicks;
    }

    public void syncItem(BEItemStackHandler<?> inventory)
    {
        this.items.clear();
        int k = 0;
        for (int i = 0; i < inventory.size(); i++)
        {
            ItemStack stack = inventory.getStackInSlot(i).copy();
            if (!stack.isEmpty())
            {
                if (stack.getCount() > 32)
                {
                    this.items.put(k, stack);
                    k += 1;
                }
                this.items.put(k, stack);
                k += 1;
            }
        }
    }
}
