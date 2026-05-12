package kogasastudio.ashihara.block;

import kogasastudio.ashihara.registry.Blocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.redstone.Orientation;
import org.jspecify.annotations.Nullable;

public class CherryVinesBlock extends Block
{
    public static final EnumProperty<LocationType> LOCATION = EnumProperty.create("location", LocationType.class);

    public CherryVinesBlock(Properties properties)
    {
        super(properties);
        this.registerDefaultState(this.defaultBlockState().setValue(LOCATION, LocationType.TOP));
    }

    public CherryVinesBlock()
    {
        this
        (
            Properties.of()
            .mapColor(DyeColor.PINK)
            .strength(0.05f)
            .sound(SoundType.PINK_PETALS)
            .noOcclusion()
            .noCollision()
            .forceSolidOn()
        );
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        builder.add(LOCATION);
    }

    @Override
    protected BlockState updateShape(BlockState state, LevelReader level, ScheduledTickAccess ticks, BlockPos pos, Direction directionToNeighbour, BlockPos neighbourPos, BlockState neighbourState, RandomSource random)
    {
        return !state.canSurvive(level, pos) ? net.minecraft.world.level.block.Blocks.AIR.defaultBlockState() : super.updateShape(state, level, ticks, pos, directionToNeighbour, neighbourPos, neighbourState, random);
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader reader, BlockPos pos)
    {
        return reader.getBlockState(pos.above()).is(Blocks.CHERRY_BLOSSOM.get()) || reader.getBlockState(pos.above()).is(this.asBlock());
    }

    @Override
    protected void neighborChanged(BlockState state, Level level, BlockPos pos, Block block, @Nullable Orientation orientation, boolean movedByPiston)
    {
        super.neighborChanged(state, level, pos, block, orientation, movedByPiston);
        BlockState updated = this.updateState(state, level, pos);
        if (!state.equals(updated)) level.setBlockAndUpdate(pos, updated);
    }

    @Override
    public long getSeed(BlockState state, BlockPos pos)
    {
        return Mth.getSeed(pos.getX(), pos.getY(), pos.getZ());
    }

    private BlockState updateState(BlockState state, Level level, BlockPos pos)
    {
        BlockState below = level.getBlockState(pos.below());
        LocationType updatedType = LocationType.TOP;
        if (below.is(this.asBlock())) updatedType = LocationType.BASE;

        return state.setValue(LOCATION, updatedType);
    }

    public enum LocationType implements StringRepresentable
    {
        BASE("base"),
        TOP("top");

        final String name;

        LocationType(String nameIn) {this.name = nameIn;}

        @Override
        public String getSerializedName() {return this.name;}
    }
}
