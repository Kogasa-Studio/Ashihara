package kogasastudio.ashihara.block.building;

import kogasastudio.ashihara.block.IVariable;
import kogasastudio.ashihara.utils.AshiharaWoodTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class ColumnBlock extends Block implements IVariable<AshiharaWoodTypes>, SimpleWaterloggedBlock
{
    public final AshiharaWoodTypes TYPE;

    public static final BooleanProperty WATER_LOGGED = BlockStateProperties.WATERLOGGED;

    public ColumnBlock(Properties p_49795_, AshiharaWoodTypes typeIn)
    {
        super(p_49795_);
        this.TYPE = typeIn;
        this.registerDefaultState(defaultBlockState().setValue(WATER_LOGGED, false));
    }

    public ColumnBlock(AshiharaWoodTypes typeIn)
    {

        this
        (
                Properties.of()
                        .mapColor(MapColor.WOOD)
                        .strength(1.5F)
                        .sound(SoundType.WOOD),
                typeIn
        );
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        builder.add(WATER_LOGGED);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter getter, BlockPos pos, CollisionContext context)
    {
        return Block.box(3d, 0d, 3d, 13d, 16d, 13d);
    }

    @Override
    public VoxelShape getOcclusionShape(BlockState state, BlockGetter getter, BlockPos pos)
    {
        VoxelShape base = Block.box(3d, 0d, 3d, 13d, 16d, 13d);

        BlockState n = getter.getBlockState(pos.north());
        BlockState w = getter.getBlockState(pos.west());
        BlockState s = getter.getBlockState(pos.south());
        BlockState e = getter.getBlockState(pos.east());

        if(n.getBlock() instanceof IExpandable && n.isFaceSturdy(getter, pos.north(), Direction.SOUTH, SupportType.CENTER))
            base = Shapes.or(base, Block.box(7, 0, 0, 9, 16, 3));
        if(w.getBlock() instanceof IExpandable && w.isFaceSturdy(getter, pos.west(), Direction.EAST, SupportType.CENTER))
            base = Shapes.or(base, Block.box(0, 0, 7, 3, 16, 9));
        if(s.getBlock() instanceof IExpandable && s.isFaceSturdy(getter, pos.south(), Direction.NORTH, SupportType.CENTER))
            base = Shapes.or(base, Block.box(7, 0, 13, 9, 16, 16));
        if(e.getBlock() instanceof IExpandable && e.isFaceSturdy(getter, pos.east(), Direction.WEST, SupportType.CENTER))
            base = Shapes.or(base, Block.box(13, 0, 7, 16, 16, 9));

        return base;
    }

    @Override
    public AshiharaWoodTypes getType()
    {
        return this.TYPE;
    }

    @Override
    public FluidState getFluidState(BlockState state)
    {
        return state.getValue(WATER_LOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
    }
}
