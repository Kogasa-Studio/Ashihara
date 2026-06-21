package kogasastudio.ashihara.client.render.ber;

import com.geckolib.animation.state.BoneSnapshot;
import com.geckolib.cache.model.GeoBone;
import com.mojang.blaze3d.vertex.PoseStack;
import kogasastudio.ashihara.block.blockentity.MortarBE;
import kogasastudio.ashihara.client.gui3d.util.BoneTracer;
import kogasastudio.ashihara.client.models.geo.FermentationDisplayModel;
import kogasastudio.ashihara.helper.RenderHelper;
import kogasastudio.ashihara.inventory.BEItemStackHandler;
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

import java.util.*;

public class MortarBER implements BlockEntityRenderer<MortarBE, BlockEntityRenderState>
{
    public Map<Integer, ItemStack> items = new HashMap<>(8);
    private final ItemModelResolver itemModelResolver;
    private float partialTicks = 0f;

    public MortarBER(BlockEntityRendererProvider.Context context)
    {
        this.itemModelResolver = context.itemModelResolver();
    }

    @Override
    public boolean shouldRender(MortarBE blockEntity, Vec3 cameraPosition)
    {
        if (blockEntity.inventory.isEmpty() && blockEntity.fluidTank.isEmpty()) return false;
        return BlockEntityRenderer.super.shouldRender(blockEntity, cameraPosition);
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
        if (!(be instanceof MortarBE mortarBE) || (mortarBE.inventory.isEmpty() && mortarBE.fluidTank.isEmpty()) || mortarBE.getModel() == null) return;
        syncItem(mortarBE.inventory);
        poseStack.pushPose();
        poseStack.translate(0, -0.5, 0);
        mortarBE.getModel().RENDERER.performRenderPass(mortarBE.getModel(), null, poseStack, submitNodeCollector, camera, state.lightCoords, partialTicks);
        poseStack.popPose();

        poseStack.pushPose(); //————————————————————————————————————————————————————————————————————————————————————————————————————————————————物品开始
        BoneTracer item_float_level = mortarBE.getBoneTracer("item_display");
        if (item_float_level != null && item_float_level.snapshot() != null)
        {
            BoneSnapshot snapshot = item_float_level.snapshot();
            poseStack.translate(snapshot.getTranslateX() / 16f, snapshot.getTranslateY() / 16f, snapshot.getTranslateZ() / 16f);
        }

        if (!mortarBE.inventory.isEmpty())
        {
            List<ItemStack> itemsToDisplay = new ArrayList<>();
            for (int i = 0; i < mortarBE.inventory.size(); ++i)
            {
                ItemStack item = mortarBE.inventory.getStackInSlot(i);
                if (item.isEmpty()) continue;
                itemsToDisplay.add(item.copy());
                if (item.count() > 1 && item.count() > item.getMaxStackSize() / 2) itemsToDisplay.add(item.copy());
            }

            for (int i = 0; i < Math.min(itemsToDisplay.size(), 8); ++i)
            {
                ItemStack item = itemsToDisplay.get(i);
                BoneTracer tracer = mortarBE.boneTracers.get("level" + i);
                if (tracer == null || tracer.snapshot() == null) continue;
                ItemStackRenderState itemState = new ItemStackRenderState();
                this.itemModelResolver.updateForTopItem(itemState, item, ItemDisplayContext.FIXED, mortarBE.getLevel(), null, 42);
                GeoBone bone = tracer.snapshot().getBone();
                RenderHelper.extractItemToGeoBone(poseStack, bone, itemState, submitNodeCollector, state.lightCoords, 4f);
            }
        }
        poseStack.popPose();//————————————————————————————————————————————————————————————————————————————————————————————————————————————————————物品结束

        poseStack.pushPose();
        if (!mortarBE.fluidTank.isEmpty())
        {
            BoneTracer fTracer = mortarBE.getBoneTracer("fluid_display");
            if (fTracer != null && !fTracer.collisionBoxes().isEmpty())
            {
                RenderHelper.renderFluidToBoneSnapshot(poseStack, fTracer.snapshot(), mortarBE.fluidTank.getFluidStack(), submitNodeCollector, state.lightCoords, 10);
            }
        }
        poseStack.popPose();
    }

    public void updateModelStat(MortarBE be)
    {
        if (Minecraft.getInstance().player == null) return;
        FermentationDisplayModel model = be.getModel();
        Player player = Minecraft.getInstance().player;
        long instanceId = model.hashCode();

        if (be.fluidLevelChanged || !be.inited)
        {
            model.syncFluid(be.prevFluidLevel, be.fluidLevel);
            model.triggerAnim(player, instanceId, FermentationDisplayModel.FLUID_LEVEL_SYNC, FermentationDisplayModel.FLUID_LEVEL_SYNC);
            model.triggerAnim(player, instanceId, FermentationDisplayModel.ITEM_FLOAT_SYNC, FermentationDisplayModel.ITEM_FLOAT_SYNC);
            if (!be.inited)
            {
                model.setAnimTime(FermentationDisplayModel.FLUID_LEVEL_SYNC, Double.MAX_VALUE);
                model.setAnimTime(FermentationDisplayModel.ITEM_FLOAT_SYNC, Double.MAX_VALUE);
                be.inited = true;
            }
            be.fluidLevelChanged = false;
        }
        if (be.fluidLevel >= 0.2f)
        {
            model.setAnimSpeed(FermentationDisplayModel.ITEM_FLOAT_IDLE, 1);
            if (!RenderHelper.animControllerPlaying(model, c -> c.getName().equals(FermentationDisplayModel.ITEM_FLOAT_IDLE)))
            {
                model.triggerAnim(player, instanceId, FermentationDisplayModel.ITEM_FLOAT_IDLE, FermentationDisplayModel.ITEM_FLOAT_IDLE);
            }
        }
        else
        {
            model.setAnimSpeed(FermentationDisplayModel.ITEM_FLOAT_IDLE, 0);
        }
    }

    @Override
    public void extractRenderState(MortarBE blockEntity, BlockEntityRenderState state, float partialTicks, Vec3 cameraPosition, ModelFeatureRenderer.@Nullable CrumblingOverlay breakProgress)
    {
        BlockEntityRenderer.super.extractRenderState(blockEntity, state, partialTicks, cameraPosition, breakProgress);
        updateModelStat(blockEntity);
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
