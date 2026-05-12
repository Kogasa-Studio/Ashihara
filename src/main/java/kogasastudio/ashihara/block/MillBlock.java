package kogasastudio.ashihara.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class MillBlock extends Block
{
    public static final EnumProperty<Direction> FACING = BlockStateProperties.HORIZONTAL_FACING;

    public MillBlock(Properties properties)
    {
        super(properties);
    }

    public MillBlock()
    {
        this
        (
            Properties.of()
            .mapColor(MapColor.STONE)
            .strength(2.0F, 6.0F)
            // todo tag .harvestTool(ToolType.PICKAXE)
            .requiresCorrectToolForDrops()
            .sound(SoundType.STONE)
        );
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context)
    {
        return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection());
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context)
    {
        VoxelShape shape1 = Block.box(2, 0, 2, 14, 5, 14);
        VoxelShape shape2 = Block.box(4, 5, 4, 12, 10, 12);
        return Shapes.or(shape1, shape2);
    }

    @Override
    public InteractionResult useItemOn(ItemStack stack, BlockState state, Level worldIn, BlockPos pos, Player player, InteractionHand handIn, BlockHitResult hit)
    {
        /*MillBE te = (MillBE) worldIn.getBlockEntity(pos);

        if (te != null)
        {
            FluidTank tank = te.getTank().orElse(new FluidTank(0));
            if (!stack.isEmpty() && FluidHelper.notifyFluidTankInteraction(player, handIn, stack, tank, worldIn, pos))
            {
                player.getInventory().setChanged();
                worldIn.sendBlockUpdated(pos, state, state, 3);
                return InteractionResult.SUCCESS;
            } else if (!worldIn.isClientSide && handIn == InteractionHand.MAIN_HAND)
            {
                NetworkHooks.openScreen((ServerPlayer) player, te, (FriendlyByteBuf packerBuffer) -> packerBuffer.writeBlockPos(te.getBlockPos()));
                return InteractionResult.SUCCESS;
            } else return InteractionResult.SUCCESS;
        }*/
        return super.useItemOn(stack, state, worldIn, pos, player, handIn, hit);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        builder.add(FACING);
    }

    /*@org.jetbrains.annotations.Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state)
    {
        return new MillBE(pos, state);
    }

    // todo 需要 tick 的 BlockEntity 都需要在 EntityBlock 里注册 Ticker
    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level p_153212_, BlockState p_153213_, BlockEntityType<T> p_153214_)
    {
        return TickableTileEntity.orEmpty(p_153214_, BlockEntities.MILL_TE.get());
    }*/
}
