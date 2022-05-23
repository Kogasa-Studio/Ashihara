package kogasastudio.ashihara.block;

import kogasastudio.ashihara.item.ItemRegistryHandler;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;

import net.minecraft.block.AbstractBlock.Properties;

public class AbstractFallenLeavesBlock extends Block
{
    public AbstractFallenLeavesBlock()
    {
        this(false);
    }

    public AbstractFallenLeavesBlock(boolean flammableIn)
    {
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

    private final boolean flammable;

    @Override
    public boolean canBeReplaced(BlockState state, BlockItemUseContext useContext)
    {
        return useContext.getItemInHand().getItem() != ItemRegistryHandler.FALLEN_SAKURA.get();
    }

    @Override
    public BlockState updateShape(BlockState stateIn, Direction facing, BlockState facingState, IWorld worldIn, BlockPos currentPos, BlockPos facingPos)
    {
        return !stateIn.canSurvive(worldIn, currentPos) ? Blocks.AIR.defaultBlockState() : super.updateShape(stateIn, facing, facingState, worldIn, currentPos, facingPos);
    }

    @Override
    public boolean canSurvive(BlockState state, IWorldReader worldIn, BlockPos pos)
    {
        return worldIn.getBlockState(pos.below()).canOcclude() || worldIn.getBlockState(pos.below()).isFaceSturdy(worldIn, pos.below(), Direction.UP);
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context)
    {
        return Block.box(0.0D, 0.0D, 0.0D, 16.0D, 1.0D, 16.0D);
    }

    @Override
    public int getFlammability(BlockState state, IBlockReader world, BlockPos pos, Direction face) {return this.flammable ? 60 : 0;}

    @Override
    public int getFireSpreadSpeed(BlockState state, IBlockReader world, BlockPos pos, Direction face) {return this.flammable ? 60 : 0;}

    @Override
    public boolean isFlammable(BlockState state, IBlockReader world, BlockPos pos, Direction face) {return this.flammable;}
}
