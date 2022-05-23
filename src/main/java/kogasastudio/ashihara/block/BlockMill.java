package kogasastudio.ashihara.block;

import kogasastudio.ashihara.block.tileentities.MillTE;
import kogasastudio.ashihara.block.tileentities.TERegistryHandler;
import kogasastudio.ashihara.block.tileentities.TickableTileEntity;
import kogasastudio.ashihara.helper.FluidHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.Nullable;

// todo Block 默认没有 BlockEntity 了，需要手动实现 EntityBlock
public class BlockMill extends Block implements EntityBlock
{
    public BlockMill()
    {
        super
        (
            Properties.of(Material.STONE)
            .strength(2.0F, 6.0F)
            // todo tag .harvestTool(ToolType.PICKAXE)
            .requiresCorrectToolForDrops()
            .sound(SoundType.STONE)
        );
    }

    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;



    @Override
    public void playerDestroy(Level worldIn, Player player, BlockPos pos, BlockState state, BlockEntity te, ItemStack stack)
    {
        if (te instanceof MillTE)
        {
            NonNullList<ItemStack> stacks = NonNullList.create();
            for (int i = 0; i < ((MillTE) te).getInput().getSlots(); i += 1)
            {
                ItemStack stack1 =((MillTE) te).getInput().getStackInSlot(i);
                if (!stack.isEmpty()) {
                    stacks.add(stack1);
                }
            }
            Containers.dropContents(worldIn, pos, stacks);
            Containers.dropContents(worldIn, pos, ((MillTE) te).getOutput().getContent());
        }
        super.playerDestroy(worldIn, player, pos, state, te, stack);
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
    public InteractionResult use(BlockState state, Level worldIn, BlockPos pos, Player player, InteractionHand handIn, BlockHitResult hit)
    {
        ItemStack stack = player.getItemInHand(handIn);
        MillTE te = (MillTE) worldIn.getBlockEntity(pos);

        if (te != null)
        {
            FluidTank tank = te.getTank().orElse(new FluidTank(0));
            if (!stack.isEmpty() && FluidHelper.notifyFluidTankInteraction(player, handIn, stack, tank, worldIn, pos))
            {
                player.getInventory().setChanged();
                worldIn.sendBlockUpdated(pos, state, state, 3);
                return InteractionResult.SUCCESS;
            }
            else if (!worldIn.isClientSide && handIn == InteractionHand.MAIN_HAND)
            {
                NetworkHooks.openGui((ServerPlayer) player, te, (FriendlyByteBuf packerBuffer) -> packerBuffer.writeBlockPos(te.getBlockPos()));
                return InteractionResult.SUCCESS;
            }
            else return InteractionResult.SUCCESS;
        }

        return InteractionResult.PASS;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {builder.add(FACING);}

    @org.jetbrains.annotations.Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new MillTE(pos, state);
    }

    // todo 需要 tick 的 BlockEntity 都需要在 EntityBlock 里注册 Ticker
    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level p_153212_, BlockState p_153213_, BlockEntityType<T> p_153214_) {
        return TickableTileEntity.orEmpty(p_153214_, TERegistryHandler.MILL_TE.get());
    }
}
