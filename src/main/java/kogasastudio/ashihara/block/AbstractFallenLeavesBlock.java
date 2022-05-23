package kogasastudio.ashihara.block;

import kogasastudio.ashihara.item.ItemRegistryHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class AbstractFallenLeavesBlock extends Block {
    private final boolean flammable;

    public AbstractFallenLeavesBlock() {
        this(false);
    }

    public AbstractFallenLeavesBlock(boolean flammableIn) {
        super
                (
                        Properties.of(Material.PLANT)
                                .strength(0.1F)
                                .sound(SoundType.GRASS)
                                .noOcclusion()
                                .noCollission()
                );
        this.flammable = flammableIn;
    }

    @Override
    public boolean canBeReplaced(BlockState state, BlockPlaceContext useContext) {
        return useContext.getItemInHand().getItem() != ItemRegistryHandler.FALLEN_SAKURA.get();
    }

    @Override
    public BlockState updateShape(BlockState stateIn, Direction facing, BlockState facingState, LevelAccessor worldIn, BlockPos currentPos, BlockPos facingPos) {
        return !stateIn.canSurvive(worldIn, currentPos) ? Blocks.AIR.defaultBlockState() : super.updateShape(stateIn, facing, facingState, worldIn, currentPos, facingPos);
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader worldIn, BlockPos pos) {
        return worldIn.getBlockState(pos.below()).canOcclude() || worldIn.getBlockState(pos.below()).isFaceSturdy(worldIn, pos.below(), Direction.UP);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
        return Block.box(0.0D, 0.0D, 0.0D, 16.0D, 1.0D, 16.0D);
    }

    @Override
    public int getFlammability(BlockState state, BlockGetter world, BlockPos pos, Direction face) {
        return this.flammable ? 60 : 0;
    }

    @Override
    public int getFireSpreadSpeed(BlockState state, BlockGetter world, BlockPos pos, Direction face) {
        return this.flammable ? 60 : 0;
    }

    @Override
    public boolean isFlammable(BlockState state, BlockGetter world, BlockPos pos, Direction face) {
        return this.flammable;
    }
}
