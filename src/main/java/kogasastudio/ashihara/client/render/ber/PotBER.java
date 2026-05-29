package kogasastudio.ashihara.client.render.ber;

import com.geckolib.animation.state.BoneSnapshot;
import com.geckolib.cache.model.GeoBone;
import com.mojang.blaze3d.vertex.PoseStack;
import kogasastudio.ashihara.block.blockentity.PotBlockEntity;
import kogasastudio.ashihara.client.gui3d.util.BoneTracer;
import kogasastudio.ashihara.client.models.geo.PotModel;
import kogasastudio.ashihara.client.sounds.PotSoundManager;
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
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

public class PotBER implements BlockEntityRenderer<PotBlockEntity, BlockEntityRenderState>
{
    private final ItemModelResolver itemModelResolver;
    private float partialTicks = 0f;

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

        poseStack.pushPose(); //————————————————————————————————————————————————————————————————————————————————————————————————————————————————物品开始
        BoneTracer item_float_level = potBE.boneTracers.get("item_display");
        if (item_float_level != null && item_float_level.snapshot() != null)
        {
            BoneSnapshot snapshot = item_float_level.snapshot();
            poseStack.translate(snapshot.getTranslateX() / 16f, snapshot.getTranslateY() / 16f, snapshot.getTranslateZ() / 16f);
        }
        if (!potBE.inventory.isEmpty())
        {

            for (int i = 0; i < potBE.inventory.size(); ++i)
            {
                ItemStack item = potBE.inventory.getStackInSlot(i);
                if (item.isEmpty()) continue;
                BoneTracer tracer = potBE.boneTracers.get("item_display_" + i);
                if (tracer == null || tracer.snapshot() == null) continue;
                ItemStackRenderState itemState = new ItemStackRenderState();
                this.itemModelResolver.updateForTopItem(itemState, item, ItemDisplayContext.FIXED, potBE.getLevel(), null, 42);
                GeoBone bone = tracer.snapshot().getBone();

                RenderHelper.extractItemToGeoBone(poseStack, bone, itemState, submitNodeCollector, state.lightCoords, 5f);
            }
        }

        if (!potBE.output.isEmpty())
        {
            ItemStack outputItem = potBE.output.getStackInSlot(0);
            BoneTracer tracer = potBE.boneTracers.get("item_display_4");
            if (tracer != null && tracer.snapshot() != null)
            {
                ItemStackRenderState itemState = new ItemStackRenderState();
                this.itemModelResolver.updateForTopItem(itemState, outputItem, ItemDisplayContext.FIXED, potBE.getLevel(), null, 42);
                GeoBone bone = tracer.snapshot().getBone();

                RenderHelper.extractItemToGeoBone(poseStack, bone, itemState, submitNodeCollector, state.lightCoords, 7f);
            }
        }
        poseStack.popPose();//————————————————————————————————————————————————————————————————————————————————————————————————————————————————————物品结束

        if (!potBE.fluidTank.isEmpty())
        {
            BoneTracer fTracer = potBE.boneTracers.get("fluid_display");
            if (fTracer != null && !fTracer.collisionBoxes().isEmpty())
            {
                RenderHelper.renderFluidToBoneSnapshot(poseStack, fTracer.snapshot(), potBE.fluidTank.getFluidStack(), submitNodeCollector, state.lightCoords, 10f);
            }
        }
    }

    public void updateModelStat(PotBlockEntity be)
    {
        if (Minecraft.getInstance().player == null) return;
        PotModel model = be.getPotModel();
        Player player = Minecraft.getInstance().player;
        long instanceId = model.hashCode();

        if (be.fluidLevelChanged || !be.inited)
        {
            model.syncFluid(be.prevFluidLevel, be.fluidLevel);
            model.triggerAnim(player, instanceId, PotModel.FLUID_LEVEL_SYNC, PotModel.FLUID_LEVEL_SYNC);
            model.triggerAnim(player, instanceId, PotModel.ITEM_FLOAT_SYNC, PotModel.ITEM_FLOAT_SYNC);
            if (!be.inited)
            {
                model.setAnimTime(PotModel.FLUID_LEVEL_SYNC, Double.MAX_VALUE);
                model.setAnimTime(PotModel.ITEM_FLOAT_SYNC, Double.MAX_VALUE);
                be.inited = true;
            }
            be.fluidLevelChanged = false;
        }
        if (be.fluidLevel >= 0.2f)
        {
            model.setAnimSpeed(PotModel.ITEM_FLOAT_IDLE, 1);
            if (!RenderHelper.animControllerPlaying(model, c -> c.getName().equals(PotModel.ITEM_FLOAT_IDLE)))
            {
                model.triggerAnim(player, instanceId, PotModel.ITEM_FLOAT_IDLE, PotModel.ITEM_FLOAT_IDLE);
            }
        }
        else
        {
            model.setAnimSpeed(PotModel.ITEM_FLOAT_IDLE, 0);
        }
    }

    @Override
    public void extractRenderState(PotBlockEntity blockEntity, BlockEntityRenderState state, float partialTicks, Vec3 cameraPosition, ModelFeatureRenderer.@Nullable CrumblingOverlay breakProgress)
    {
        BlockEntityRenderer.super.extractRenderState(blockEntity, state, partialTicks, cameraPosition, breakProgress);
        PotSoundManager.tick(blockEntity);
        updateModelStat(blockEntity);
        this.partialTicks = partialTicks;
    }
}
