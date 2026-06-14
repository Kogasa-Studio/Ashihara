package kogasastudio.ashihara.client.render.ber;

import com.geckolib.animation.state.BoneSnapshot;
import com.geckolib.cache.model.GeoBone;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import kogasastudio.ashihara.block.FermentationBlock;
import kogasastudio.ashihara.block.blockentity.FermentationBlockEntity;
import kogasastudio.ashihara.client.gui3d.util.BoneTracer;
import kogasastudio.ashihara.client.models.geo.FermentationDisplayModel;
import kogasastudio.ashihara.helper.RenderHelper;
import kogasastudio.ashihara.registry.Blocks;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

public class FermentationBER implements BlockEntityRenderer<FermentationBlockEntity, BlockEntityRenderState>
{
    private final ItemModelResolver itemModelResolver;
    private float partialTicks = 0f;

    public FermentationBER(BlockEntityRendererProvider.Context context) {this.itemModelResolver = context.itemModelResolver();}

    @Override
    public BlockEntityRenderState createRenderState()
    {
        return new BlockEntityRenderState();
    }

    @Override
    public void extractRenderState(FermentationBlockEntity blockEntity, BlockEntityRenderState state, float partialTicks, Vec3 cameraPosition, ModelFeatureRenderer.@Nullable CrumblingOverlay breakProgress)
    {
        BlockEntityRenderer.super.extractRenderState(blockEntity, state, partialTicks, cameraPosition, breakProgress);
        updateModelStat(blockEntity);
        this.partialTicks = partialTicks;
    }

    @Override
    public void submit(BlockEntityRenderState state, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, CameraRenderState camera)
    {
        if (Minecraft.getInstance().level == null) return;
        BlockEntity be = Minecraft.getInstance().level.getBlockEntity(state.blockPos);
        if (!(be instanceof FermentationBlockEntity fbe)) return;
        if (fbe.inventory.isEmpty() && fbe.fluid.isEmpty()) return;
        poseStack.pushPose();
        if (Minecraft.getInstance().level.getBlockState(be.getBlockPos()).is(Blocks.LARGE_FERMENTATION_VAT))
        {
            Direction direction = Minecraft.getInstance().level.getBlockState(be.getBlockPos()).getValue(BlockStateProperties.HORIZONTAL_FACING);
            float degree = switch (direction)
            {
                case NORTH -> 0;
                case SOUTH -> 180;
                case WEST -> 90;
                case EAST -> 270;
                default -> 0;
            };
            poseStack.translate(0.5, 0, 0.5);
            poseStack.mulPose(Axis.YP.rotationDegrees(degree));
            poseStack.translate(-0.5, 0, -0.5);
        }

        poseStack.pushPose();
        poseStack.translate(0, -0.5, 0);
        fbe.getModel().RENDERER.performRenderPass(fbe.getModel(), null, poseStack, submitNodeCollector, camera, state.lightCoords, partialTicks);
        poseStack.popPose();

        poseStack.pushPose(); //————————————————————————————————————————————————————————————————————————————————————————————————————————————————物品开始
        BoneTracer item_float_level = fbe.getBoneTracer("item_display_");
        if (item_float_level != null && item_float_level.snapshot() != null)
        {
            BoneSnapshot snapshot = item_float_level.snapshot();
            poseStack.translate(snapshot.getTranslateX() / 16f, snapshot.getTranslateY() / 16f, snapshot.getTranslateZ() / 16f);
        }

        if (!fbe.inventory.isEmpty())
        {

            for (int i = 0; i < fbe.inventory.size(); ++i)
            {
                ItemStack item = fbe.inventory.getStackInSlot(i);
                if (item.isEmpty()) continue;
                BoneTracer tracer = fbe.boneTracers.get("item_display_" + fbe.getSizePrefix() + i);
                if (tracer == null || tracer.snapshot() == null) continue;
                ItemStackRenderState itemState = new ItemStackRenderState();
                this.itemModelResolver.updateForTopItem(itemState, item, ItemDisplayContext.FIXED, fbe.getLevel(), null, 42);
                GeoBone bone = tracer.snapshot().getBone();

                RenderHelper.extractItemToGeoBone(poseStack, bone, itemState, submitNodeCollector, state.lightCoords, 5f);
            }
        }
        poseStack.popPose();//————————————————————————————————————————————————————————————————————————————————————————————————————————————————————物品结束

        if (!fbe.fluid.isEmpty())
        {
            BoneTracer fTracer = fbe.getBoneTracer("fluid_display_");
            if (fTracer != null && !fTracer.collisionBoxes().isEmpty())
            {
                float scale = fbe.size == FermentationBlockEntity.Size.LARGE_VAT ? 26f : 12f;
                RenderHelper.renderFluidToBoneSnapshot(poseStack, fTracer.snapshot(), fbe.fluid.getFluidStack(), submitNodeCollector, state.lightCoords, scale);
            }
        }
        poseStack.popPose();
    }

    public void updateModelStat(FermentationBlockEntity be)
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
    public boolean shouldRender(FermentationBlockEntity blockEntity, Vec3 cameraPosition)
    {
        if (blockEntity.getLevel() != null)
        {
            BlockState bs = blockEntity.getLevel().getBlockState(blockEntity.getBlockPos());
            if (!bs.is(Blocks.WOODEN_BASIN) && bs.hasProperty(FermentationBlock.HAS_LID) && bs.getValue(FermentationBlock.HAS_LID))
            {
                return false;
            }
        }
        if (blockEntity.inventory.isEmpty() && blockEntity.fluid.isEmpty()) return false;
        return BlockEntityRenderer.super.shouldRender(blockEntity, cameraPosition);
    }
}
