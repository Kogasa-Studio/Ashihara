package kogasastudio.ashihara.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class CherryBlossomVinesBlock extends Block
{
    public static final EnumProperty<LocationType> LOCATION = EnumProperty.create("location", LocationType.class);

    public CherryBlossomVinesBlock()
    {
        super
                (
                        Properties.of()
                                .mapColor(DyeColor.PINK)
                                .strength(0.05f)
                                .sound(SoundType.PINK_PETALS)
                                .noOcclusion()
                                .noCollission()
                );
        this.registerDefaultState(this.defaultBlockState().setValue(LOCATION, LocationType.TOP));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        builder.add(LOCATION);
    }

    @Override
    public BlockState updateShape(BlockState stateIn, Direction facing, BlockState facingState, LevelAccessor worldIn, BlockPos currentPos, BlockPos facingPos)
    {
        return !stateIn.canSurvive(worldIn, currentPos) ? Blocks.AIR.defaultBlockState() : super.updateShape(stateIn, facing, facingState, worldIn, currentPos, facingPos);
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader reader, BlockPos pos)
    {
        return reader.getBlockState(pos.above()).is(BlockRegistryHandler.CHERRY_BLOSSOM.get()) || reader.getBlockState(pos.above()).is(this.asBlock());
    }

    @Override
    public void neighborChanged(BlockState state, Level level, BlockPos pos, Block block, BlockPos neighborPos, boolean flag)
    {
        BlockState updated = this.updateState(state, level, pos);
        if (!state.equals(updated))
        {
            level.setBlockAndUpdate(pos, updated);
        }
        if
        (
                state.getValue(LOCATION).equals(LocationType.FRONT)
                        && level.getBlockState(pos.below()).is(this.asBlock())
                        && level.getBlockState(pos.below()).getValue(LOCATION).equals(LocationType.FRONT)
                        && level.getBlockState(pos.above()).is(this.asBlock())
                        && !level.getBlockState(pos.above()).getValue(LOCATION).equals(LocationType.MID)
        )
        {
            level.setBlockAndUpdate(pos.above(), level.getBlockState(pos.above()).setValue(LOCATION, LocationType.MID));
        }
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter getter, BlockPos pos, CollisionContext context)
    {
        VoxelShape shape;

        VoxelShape base = box(2, 0, 2, 14, 16, 14);
        VoxelShape mid = box(3, 0, 3, 13, 16, 13);
        VoxelShape front = box(4, 0, 4, 12, 16, 12);
        VoxelShape top = box(4, 1, 4, 12, 16, 12);

        LocationType type = state.getValue(LOCATION);
        switch (type)
        {
            case BASE -> shape = base;
            case MID -> shape = mid;
            case FRONT -> shape = front;
            default -> shape = top;
        }
        return shape;
    }

    private BlockState updateState(BlockState state, Level level, BlockPos pos)
    {
        BlockState up = level.getBlockState(pos.above());
        BlockState down = level.getBlockState(pos.below());
        BlockState down2 = level.getBlockState(pos.below(2));

        LocationType updatedType = state.getValue(LOCATION);

        if (up.is(BlockRegistryHandler.CHERRY_BLOSSOM.get()))
        {
            if (down.isAir()) updatedType = LocationType.TOP;
            else if (down.is(this.asBlock()))
            {
                LocationType type = down.getValue(LOCATION);
                switch (type)
                {
                    case TOP -> updatedType = LocationType.FRONT;
                    case FRONT -> updatedType = LocationType.MID;
                    default -> updatedType = LocationType.BASE;
                }
            }
        }
        else if (up.is(this.asBlock()))
        {
            if (down.isAir()) updatedType = LocationType.TOP;
            else if (down.is(this.asBlock()))
            {
                LocationType type = down.getValue(LOCATION);
                switch (type)
                {
                    case TOP -> updatedType = LocationType.FRONT;
                    case FRONT ->
                    {
                        if (down2.is(this.asBlock()) && down2.getValue(LOCATION).equals(LocationType.FRONT)) updatedType = LocationType.MID;
                        else updatedType = LocationType.FRONT;
                    }
                    default -> updatedType = LocationType.MID;
                }
            }
        }

        return state.getValue(LOCATION).equals(updatedType) ? state : state.setValue(LOCATION, updatedType);
    }

    public enum LocationType implements StringRepresentable
    {
        BASE("base"),
        MID("mid"),
        FRONT("front"),
        TOP("top");

        final String name;

        LocationType(String nameIn) {this.name = nameIn;}

        @Override
        public String getSerializedName() {return this.name;}
    }
}
