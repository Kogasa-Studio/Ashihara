package kogasastudio.ashihara.client.render.ber;

import com.geckolib.cache.model.GeoBone;
import com.mojang.blaze3d.vertex.PoseStack;
import kogasastudio.ashihara.block.blockentity.PotBlockEntity;
import kogasastudio.ashihara.client.gui3d.util.BoneTracer;
import kogasastudio.ashihara.client.models.geo.PotModel;
import kogasastudio.ashihara.helper.RenderHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

public class PotBER implements BlockEntityRenderer<PotBlockEntity, BlockEntityRenderState>
{
    private final ItemModelResolver itemModelResolver;
    private float partialTicks = 0f;
    private boolean inited = false;

    public PotBER(BlockEntityRendererProvider.Context context)
    {
        this.itemModelResolver = context.itemModelResolver();
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
        if (!(be instanceof PotBlockEntity potBE)) return;
        if (potBE.inventory.isEmpty() && potBE.output.isEmpty() && potBE.fluidTank.isEmpty()) return;

        poseStack.pushPose();
        poseStack.translate(0, -0.5, 0);
        potBE.getPotModel().RENDERER.performRenderPass(potBE.getPotModel(), null, poseStack, submitNodeCollector, camera, state.lightCoords, partialTicks);
        poseStack.popPose();
        if (!potBE.inventory.isEmpty())
        {

            for (int i = 0; i < potBE.inventory.size(); ++i)
            {
                ItemStack item = potBE.inventory.getStackInSlot(i);
                if (item.isEmpty()) continue;
                BoneTracer tracer = potBE.boneTracers.get("item_display_" + i);
                if (tracer == null || tracer.collisionBoxes().isEmpty()) continue;
                ItemStackRenderState itemState = new ItemStackRenderState();
                this.itemModelResolver.updateForTopItem(itemState, item, ItemDisplayContext.FIXED, potBE.getLevel(), null, 42);
                GeoBone bone = tracer.snapshot().getBone();

                RenderHelper.extractItemToGeoBone(poseStack, bone, itemState, submitNodeCollector, state.lightCoords, 5f);
            }
        }

        if (!potBE.output.isEmpty())
        {
            ItemStack outputItem = potBE.output.getStackInSlot(0);
            BoneTracer tracer = potBE.boneTracers.get("item_display_5");
            if (tracer != null && !tracer.collisionBoxes().isEmpty())
            {
                ItemStackRenderState itemState = new ItemStackRenderState();
                this.itemModelResolver.updateForTopItem(itemState, outputItem, ItemDisplayContext.FIXED, potBE.getLevel(), null, 42);
                GeoBone bone = tracer.snapshot().getBone();

                RenderHelper.extractItemToGeoBone(poseStack, bone, itemState, submitNodeCollector, state.lightCoords, 7f);
            }
        }

        if (!potBE.fluidTank.isEmpty())
        {
            BoneTracer fTracer = potBE.boneTracers.get("fluid_display");
            if (fTracer != null && !fTracer.collisionBoxes().isEmpty())
            {
                GeoBone bone = fTracer.snapshot().getBone();
                RenderHelper.renderFluidToGeoBone(poseStack, bone, potBE.fluidTank.getFluidStack(), submitNodeCollector, state.lightCoords, 8f);
            }
        }
    }

    public void updateModelStat(PotBlockEntity blockEntity)
    {
        if (Minecraft.getInstance().player == null) return;
        boolean levelChanged = blockEntity.prevFluidLevel != blockEntity.fluidLevel;

        if (levelChanged || !inited)
        {
            blockEntity.getPotModel().syncFluid(blockEntity.prevFluidLevel, blockEntity.fluidLevel);
            blockEntity.getPotModel().triggerAnim(Minecraft.getInstance().player, blockEntity.getPotModel().hashCode(), PotModel.FLUID_LEVEL_SYNC_CONTROLLER, PotModel.FLUID_LEVEL_SYNC);
            blockEntity.getPotModel().triggerAnim(Minecraft.getInstance().player, blockEntity.getPotModel().hashCode(), PotModel.FLUID_LEVEL_SYNC_CONTROLLER, PotModel.ITEM_FLOAT_SYNC);
            if (!inited)
            {
                blockEntity.getPotModel().setAnimTime(PotModel.FLUID_LEVEL_SYNC_CONTROLLER, Double.MAX_VALUE);
                this.inited = true;
            }
        }
        if (blockEntity.fluidLevel >= 0.25f)
        {
            if (!RenderHelper.animControllerPlaying(blockEntity.getPotModel(), c -> c.getName().equals(PotModel.ITEM_FLOAT_IDLE)))
            {
                blockEntity.getPotModel().triggerAnim(Minecraft.getInstance().player, blockEntity.getPotModel().hashCode(), PotModel.ITEM_FLOAT_IDLE, PotModel.ITEM_FLOAT_IDLE);
            }
        }
        else
        {
            blockEntity.getPotModel().stopTriggeredAnim(Minecraft.getInstance().player, blockEntity.getPotModel().hashCode(), PotModel.ITEM_FLOAT_IDLE, PotModel.ITEM_FLOAT_IDLE);
        }
    }

    @Override
    public void extractRenderState(PotBlockEntity blockEntity, BlockEntityRenderState state, float partialTicks, Vec3 cameraPosition, ModelFeatureRenderer.@Nullable CrumblingOverlay breakProgress)
    {
        BlockEntityRenderer.super.extractRenderState(blockEntity, state, partialTicks, cameraPosition, breakProgress);
        updateModelStat(blockEntity);
        this.partialTicks = partialTicks;
    }
}
