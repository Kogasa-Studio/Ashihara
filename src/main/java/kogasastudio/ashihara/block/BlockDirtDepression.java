package kogasastudio.ashihara.block;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.block.IWaterLoggable;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Material;
import net.minecraft.fluid.Fluids;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.level.BlockGetter;
import net.minecraftforge.common.ToolType;

public class BlockDirtDepression extends Block implements IWaterLoggable
{
    public BlockDirtDepression()
    {
        super
        (
            Properties.of(Material.DIRT)
            .strength(0.5F)
            .harvestTool(ToolType.SHOVEL)
            .harvestLevel(2)
            .sound(SoundType.GRAVEL)
            .noOcclusion()
        );
        this.registerDefaultState(this.getStateDefinition().any().setValue(WATERLOGGED, false));
    }

    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        builder.add(WATERLOGGED);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) //碰撞箱的设定
    {
        return Block.box(0.0D, 0.0D, 0.0D, 16.0D, 10.0D, 16.0D);
    }

    @Override
    public FluidState getFluidState(BlockState state)
    {
        return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context)
    {
        return this.defaultBlockState().setValue(WATERLOGGED, context.getLevel().getFluidState(context.getClickedPos()).getType().equals(Fluids.WATER));
    }
}
