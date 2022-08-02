package kogasastudio.ashihara.block;

import kogasastudio.ashihara.block.tileentities.PailTE;
import kogasastudio.ashihara.block.tileentities.TERegistryHandler;
import kogasastudio.ashihara.helper.FluidHelper;
import kogasastudio.ashihara.item.ItemRegistryHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

import static kogasastudio.ashihara.item.ItemRegistryHandler.MINATO_AQUA;
import static kogasastudio.ashihara.item.ItemRegistryHandler.PAIL;
import static net.minecraft.world.item.Items.GLASS_BOTTLE;
import static net.minecraft.world.item.Items.POTION;
import static net.minecraft.world.level.block.state.properties.BlockStateProperties.HORIZONTAL_AXIS;

public class BlockPail extends Block implements EntityBlock {
    public static final EnumProperty<Direction.Axis> AXIS = HORIZONTAL_AXIS;

    public BlockPail() {
        super
                (
                        Properties.of(Material.WOOD)
                                .strength(3.0F)
                                // todo tag .harvestTool(ToolType.AXE)
                                .sound(SoundType.WOOD)
                                .noOcclusion()
                );
    }

    private ItemStack getIdentifiedItem(Level worldIn, BlockPos pos) {
        PailTE te = (PailTE) worldIn.getBlockEntity(pos);
        ItemStack stack = new ItemStack(PAIL.get());
        if (te != null && !worldIn.isClientSide() && !te.getTank().orElse(new FluidTank(0)).isEmpty()) {
            CompoundTag nbt = te.serializeNBT();
            if (!nbt.isEmpty()) {
                stack.addTagElement("BlockEntityTag", nbt);
            }
        }
        return stack;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(AXIS);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState().setValue(AXIS, context.getHorizontalDirection().getAxis());
    }

    @Override
    public int getLightEmission(BlockState state, BlockGetter level, BlockPos pos) {
        int ambientLight = super.getLightEmission(state, level, pos);
        if (level == null || ambientLight == 15) {
            return ambientLight;
        }
        BlockEntity tileEntity = level.getBlockEntity(pos);
        if (tileEntity != null && tileEntity.getType().equals(TERegistryHandler.PAIL_TE.get())) {
            PailTE te = (PailTE) tileEntity;
            FluidStack fluid = te.getTank().orElse(new FluidTank(0)).getFluid();
            if (!fluid.isEmpty()) {
                FluidAttributes fluidAttributes = fluid.getFluid().getAttributes();
                ambientLight = Math.max(ambientLight, level instanceof BlockAndTintGetter btg
                        ? fluidAttributes.getLuminosity(btg, pos)
                        : fluidAttributes.getLuminosity(fluid));
            }
        }
        return ambientLight;
    }

    @Override
    public void playerWillDestroy(Level worldIn, BlockPos pos, BlockState state, Player player) {
        if (!player.isCreative()) {
            ItemStack stack = getIdentifiedItem(worldIn, pos);

            ItemEntity entity = new ItemEntity(worldIn, (double) pos.getX() + 0.5d, (double) pos.getY() + 0.5d, pos.getZ() + 0.5d, stack);
            entity.setDefaultPickUpDelay();
            worldIn.addFreshEntity(entity);
        }
        super.playerWillDestroy(worldIn, pos, state, player);
    }

    @Override
    public InteractionResult use(BlockState state, Level worldIn, BlockPos pos, Player player, InteractionHand handIn, BlockHitResult hit) {
        ItemStack stack = player.getItemInHand(handIn);
        PailTE te = (PailTE) worldIn.getBlockEntity(pos);

        if (te == null) return InteractionResult.FAIL;

        FluidTank bucket = te.getTank().orElse(new FluidTank(0));
        if (!stack.isEmpty() && FluidHelper.notifyFluidTankInteraction(player, handIn, stack, bucket, worldIn, pos)) {
            player.getInventory().setChanged();
            worldIn.sendBlockUpdated(pos, state, state, 3);
            return InteractionResult.SUCCESS;
        }

        if (stack.isEmpty() && !player.isShiftKeyDown()) {
            ItemStack item = getIdentifiedItem(worldIn, pos);
            player.setItemInHand(handIn, item);
            worldIn.removeBlock(pos, false);
            return InteractionResult.SUCCESS;
        }

        if (stack.getItem().equals(ItemRegistryHandler.KOISHI.get())) {
            if (!worldIn.isClientSide()) {
                player.sendMessage
                        (
                                new TranslatableComponent
                                        (
                                                "\n{\n    fluid: " + bucket.getFluid().getFluid().getRegistryName()
                                                        + ";\n    amount: " + bucket.getFluidAmount() + ";\n}"
                                        ), UUID.randomUUID()
                        );
            }
            return InteractionResult.SUCCESS;
        }
        if (stack.getItem().equals(MINATO_AQUA.get()) && !worldIn.isClientSide()) {
            player.sendMessage(new TranslatableComponent("Debu!"), UUID.randomUUID());
            return InteractionResult.SUCCESS;
        }

        if (!bucket.isEmpty()) {
            if (stack.getItem().equals(GLASS_BOTTLE) && bucket.getFluid().getFluid() == Fluids.WATER) {
                if (bucket.getFluidAmount() >= 250) {
                    player.setItemInHand(handIn, new ItemStack(POTION));
                    bucket.drain(250, IFluidHandler.FluidAction.EXECUTE);
                    return InteractionResult.SUCCESS;
                } else return InteractionResult.PASS;
            } else if (stack.isEmpty() && player.isShiftKeyDown()) {
                bucket.drain(bucket.getFluidAmount(), IFluidHandler.FluidAction.EXECUTE);
                return InteractionResult.SUCCESS;
            }
        }
        return InteractionResult.PASS;
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
        VoxelShape sideN = box(3.0d, 0.0d, 3.0d, 13.0d, 10.0d, 4.0d);
        VoxelShape sideE = box(12.0d, 0.0d, 4.0d, 13.0d, 10.0d, 12.0d);
        VoxelShape sideS = box(3.0d, 0.0d, 12.0d, 13.0d, 10.0d, 13.0d);
        VoxelShape sideW = box(3.0d, 0.0d, 4.0d, 4.0d, 10.0d, 12.0d);
        VoxelShape bottom = box(4.0d, 0.5d, 4.0d, 12.0d, 1.5d, 12.0d);
        VoxelShape plankLeftX = box(7.0d, 10.0d, 3.0d, 9.0d, 16.0d, 4.0d);
        VoxelShape plankRightX = box(7.0d, 10.0d, 12.0d, 9.0d, 16.0d, 13.0d);
        VoxelShape stickX = box(7.5d, 14.5d, 4.0d, 8.5d, 15.5d, 12.0d);
        VoxelShape plankLeftZ = box(3.0d, 10.0d, 7.0d, 4.0d, 16.0d, 9.0d);
        VoxelShape plankRightZ = box(12.0d, 10.0d, 7.0d, 13.0d, 16.0d, 9.0d);
        VoxelShape stickZ = box(4.0d, 14.5d, 7.5d, 12.0d, 15.5d, 8.5d);

        VoxelShape bucket = Shapes.or(bottom, sideN, sideE, sideS, sideW);
        VoxelShape handleX = Shapes.or(stickX, plankLeftX, plankRightX);
        VoxelShape handleZ = Shapes.or(stickZ, plankLeftZ, plankRightZ);

        return state.getValue(AXIS).equals(Direction.Axis.X) ? Shapes.or(bucket, handleX) : Shapes.or(bucket, handleZ);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return new PailTE(pPos, pState);
    }
}
