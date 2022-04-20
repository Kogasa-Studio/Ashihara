package kogasastudio.ashihara.block;

import kogasastudio.ashihara.block.tileentities.CuttingBoardTE;
import kogasastudio.ashihara.block.tileentities.TERegistryHandler;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

public class BlockCuttingBoard extends Block
{
    public BlockCuttingBoard()
    {
        super
        (
            Properties.create(Material.WOOD)
            .sound(SoundType.WOOD)
            .hardnessAndResistance(0.4F)
        );
    }

    public static final EnumProperty<Direction> FACING = BlockStateProperties.HORIZONTAL_FACING;

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {builder.add(FACING);}

    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context)
    {
        return this.getDefaultState().with(FACING, context.getPlacementHorizontalFacing().getOpposite());
    }

    @Override
    public ActionResultType onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit)
    {
        TileEntity teIn = worldIn.getTileEntity(pos);
        if (teIn == null || !teIn.getType().equals(TERegistryHandler.CUTTING_BOARD_TE.get())) return ActionResultType.FAIL;
        CuttingBoardTE te = (CuttingBoardTE) teIn;
        if (te.handleInteraction(player, handIn, worldIn, pos)) return ActionResultType.SUCCESS;
        return ActionResultType.PASS;
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context)
    {
        VoxelShape x = makeCuboidShape(2.0d, 0.0d, 1.0d, 14.0d, 1.0d, 15.0d);
        VoxelShape z = makeCuboidShape(1.0d, 0.0d, 2.0d, 15.0d, 1.0d, 14.0d);

        return state.get(FACING).getAxis().equals(Direction.Axis.X) ? x : z;
    }

    @Override
    public boolean hasTileEntity(BlockState state) {return true;}

    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {return new CuttingBoardTE();}
}
