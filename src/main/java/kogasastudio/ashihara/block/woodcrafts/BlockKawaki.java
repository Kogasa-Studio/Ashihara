package kogasastudio.ashihara.block.woodcrafts;

import kogasastudio.ashihara.block.IVariable;
import kogasastudio.ashihara.helper.BlockActionHelper;
import kogasastudio.ashihara.utils.AshiharaWoodTypes;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;

import net.minecraft.block.AbstractBlock.Properties;

public class BlockKawaki extends Block implements IVariable<AshiharaWoodTypes>
{
    private static AshiharaWoodTypes type;
    public BlockKawaki(AshiharaWoodTypes typeIn)
    {
        super
        (
            Properties.of(Material.WOOD)
            .strength(0.5F)
            .sound(SoundType.WOOD)
        );
        type = typeIn;
    }

    public static final EnumProperty<Direction> FACING = BlockStateProperties.HORIZONTAL_FACING;
    public static final BooleanProperty ISLONG = BooleanProperty.create("is_long");

    @Override
    protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder) {builder.add(FACING, ISLONG);}

    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context)
    {
        World worldIn = context.getLevel();
        BlockPos posIn = context.getClickedPos();
        Direction facingIn = context.getHorizontalDirection();
        BlockState facingState = worldIn.getBlockState(posIn.relative(facingIn.getOpposite()));

        return this.defaultBlockState()
        .setValue(FACING, facingIn.getOpposite())
        .setValue(ISLONG, facingState.isFaceSturdy(worldIn, posIn.relative(facingIn), facingIn.getOpposite())
            || canConnect(this.defaultBlockState(), facingState));
    }

    private boolean canConnect(BlockState state, BlockState toCheck)
    {
        return BlockActionHelper.typeMatches(state, toCheck) && (toCheck.getBlock() instanceof BlockKawaki || toCheck.getBlock() instanceof BlockKumimono);
    }

    @Override
    public void neighborChanged(BlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos, boolean isMoving)
    {
        BlockState expandedState = worldIn.getBlockState(pos.relative(state.getValue(FACING)));
        boolean shouldBeLong = expandedState.isFaceSturdy(worldIn, pos.relative(state.getValue(FACING)), state.getValue(FACING).getOpposite())
        || canConnect(state, expandedState);
        if (state.getValue(ISLONG) != shouldBeLong)
        {
            worldIn.setBlockAndUpdate(pos, state.setValue(ISLONG, shouldBeLong));
        }
    }

    @Override
    public boolean canSurvive(BlockState state, IWorldReader worldIn, BlockPos pos)
    {
        return !worldIn.getBlockState(pos.relative(state.getValue(FACING).getOpposite())).isAir();
    }

    @Override
    public BlockState updateShape
    (BlockState stateIn, Direction facing, BlockState facingState, IWorld worldIn, BlockPos currentPos, BlockPos facingPos)
    {
        return !this.canSurvive(stateIn, worldIn, currentPos)
        ? Blocks.AIR.defaultBlockState()
        : super.updateShape(stateIn, facing, facingState, worldIn, currentPos, facingPos);
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context)
    {
        VoxelShape n = box(5.0d, 6.0d, 4.5d, 11.0d, 16.0d, 16.0d);
        VoxelShape e = box(0.0d, 6.0d, 5.0d, 11.5d, 16.0d, 11.0d);
        VoxelShape s = box(5.0d, 6.0d, 0.0d, 11.0d, 16.0d, 11.5d);
        VoxelShape w = box(4.5d, 6.0d, 5.0d, 16.0d, 16.0d, 11.0d);

        VoxelShape long_X = box(0.0d, 6.0d, 5.0d, 16.0d, 16.0d, 11.0d);
        VoxelShape long_Z = box(5.0d, 6.0d, 0.0d, 11.0d, 16.0d, 16.0d);

        if (state.getValue(ISLONG))
        {
            if (state.getValue(FACING).getAxis().equals(Direction.Axis.X)) return long_X;
            else return long_Z;
        }
        else
        {
            switch (state.getValue(FACING))
            {
                case NORTH: return n;
                case EAST: return e;
                case SOUTH: return s;
                case WEST: return w;
                default: return long_Z;
            }
        }
    }

    @Override
    public AshiharaWoodTypes getType() {return type;}
}
