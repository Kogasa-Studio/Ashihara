package kogasastudio.ashihara.block;

import kogasastudio.ashihara.block.tileentities.MillTE;
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
import net.minecraftforge.fml.network.NetworkHooks;

import javax.annotation.Nullable;

public class BlockMill extends Block
{
    public BlockMill()
    {
        super
        (
            Properties.create(Material.ROCK)
            .hardnessAndResistance(2.0F, 6.0F)
            .harvestTool(ToolType.PICKAXE)
            .setRequiresTool()
            .sound(SoundType.STONE)
        );
    }

    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;

    @Override
    public void harvestBlock(World worldIn, PlayerEntity player, BlockPos pos, BlockState state, TileEntity te, ItemStack stack)
    {
        if (te instanceof MillTE)
        {
            NonNullList<ItemStack> stacks = NonNullList.create();
            for (int i = 0; i < ((MillTE) te).getInput().getSlots(); i += 1)
            {
                ItemStack stack1 =((MillTE) te).getInput().getStackInSlot(i);
                if (!stack.isEmpty()) stacks.add(stack1);
            }
            InventoryHelper.dropItems(worldIn, pos, stacks);
            InventoryHelper.dropInventoryItems(worldIn, pos, ((MillTE) te).getOutput());
        }
        super.harvestBlock(worldIn, player, pos, state, te, stack);
    }

    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context)
    {
        return this.getDefaultState().with(FACING, context.getPlacementHorizontalFacing());
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context)
    {
        VoxelShape shape1 = Block.makeCuboidShape(2, 0, 2, 14, 5, 14);
        VoxelShape shape2 = Block.makeCuboidShape(4, 5, 4, 12, 10, 12);
        return VoxelShapes.or(shape1, shape2);
    }

    @Override
    public ActionResultType onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit)
    {
        if (!worldIn.isRemote && handIn == Hand.MAIN_HAND)
        {
            MillTE mill = (MillTE) worldIn.getTileEntity(pos);
            NetworkHooks.openGui((ServerPlayerEntity) player, mill, (PacketBuffer packerBuffer) -> packerBuffer.writeBlockPos(mill.getPos()));
        }
        return ActionResultType.SUCCESS;
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {builder.add(FACING);}

    @Override
    public boolean hasTileEntity(BlockState state) {return true;}

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {return new MillTE();}
}
