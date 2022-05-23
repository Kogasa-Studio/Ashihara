package kogasastudio.ashihara.block;

import kogasastudio.ashihara.block.tileentities.MillTE;
import kogasastudio.ashihara.helper.FluidHelper;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.common.ToolType;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import net.minecraftforge.fml.network.NetworkHooks;

import javax.annotation.Nullable;

import net.minecraft.block.AbstractBlock.Properties;

public class BlockMill extends Block
{
    public BlockMill()
    {
        super
        (
            Properties.of(Material.STONE)
            .strength(2.0F, 6.0F)
            .harvestTool(ToolType.PICKAXE)
            .requiresCorrectToolForDrops()
            .sound(SoundType.STONE)
        );
    }

    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;

    @Override
    public void playerDestroy(World worldIn, PlayerEntity player, BlockPos pos, BlockState state, TileEntity te, ItemStack stack)
    {
        if (te instanceof MillTE)
        {
            NonNullList<ItemStack> stacks = NonNullList.create();
            for (int i = 0; i < ((MillTE) te).getInput().getSlots(); i += 1)
            {
                ItemStack stack1 =((MillTE) te).getInput().getStackInSlot(i);
                if (!stack.isEmpty()) stacks.add(stack1);
            }
            InventoryHelper.dropContents(worldIn, pos, stacks);
            InventoryHelper.dropContents(worldIn, pos, ((MillTE) te).getOutput());
        }
        super.playerDestroy(worldIn, player, pos, state, te, stack);
    }

    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context)
    {
        return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection());
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context)
    {
        VoxelShape shape1 = Block.box(2, 0, 2, 14, 5, 14);
        VoxelShape shape2 = Block.box(4, 5, 4, 12, 10, 12);
        return VoxelShapes.or(shape1, shape2);
    }

    @Override
    public ActionResultType use(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit)
    {
        ItemStack stack = player.getItemInHand(handIn);
        MillTE te = (MillTE) worldIn.getBlockEntity(pos);

        if (te != null)
        {
            FluidTank tank = te.getTank().orElse(new FluidTank(0));
            if (!stack.isEmpty() && FluidHelper.notifyFluidTankInteraction(player, handIn, stack, tank, worldIn, pos))
            {
                player.inventory.setChanged();
                worldIn.sendBlockUpdated(pos, state, state, 3);
                return ActionResultType.SUCCESS;
            }
            else if (!worldIn.isClientSide && handIn == Hand.MAIN_HAND)
            {
                NetworkHooks.openGui((ServerPlayerEntity) player, te, (PacketBuffer packerBuffer) -> packerBuffer.writeBlockPos(te.getBlockPos()));
                return ActionResultType.SUCCESS;
            }
            else return ActionResultType.SUCCESS;
        }

        return ActionResultType.PASS;
    }

    @Override
    protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder) {builder.add(FACING);}

    @Override
    public boolean hasTileEntity(BlockState state) {return true;}

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {return new MillTE();}
}
