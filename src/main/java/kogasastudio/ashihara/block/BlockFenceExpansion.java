package kogasastudio.ashihara.block;

import kogasastudio.ashihara.utils.WoodTypes;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;

import static kogasastudio.ashihara.utils.AshiharaTags.ADVANCED_FENCES;

public class BlockFenceExpansion extends Block implements IVariable<WoodTypes>
{
    public BlockFenceExpansion(WoodTypes typeIn)
    {
        super
        (
            Properties.create(Material.WOOD)
            .hardnessAndResistance(0.2F)
            .sound(SoundType.WOOD)
        );
        type = typeIn;
    }

    public static WoodTypes type;

    public static final EnumProperty<Direction> FACING = BlockStateProperties.FACING;
    public static final EnumProperty<DecorationTypes> DECORATION = EnumProperty.create("decoration", DecorationTypes.class);

    @Override
    public WoodTypes getType() {return type;}

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder)
    {
        builder.add(FACING, DECORATION);
    }

    @Override
    public boolean isValidPosition(BlockState state, IWorldReader worldIn, BlockPos pos)
    {
        return worldIn.getBlockState(pos.offset(state.get(FACING).getOpposite())).getBlock().isIn(ADVANCED_FENCES);
    }

    @Override
    public BlockState updatePostPlacement(BlockState stateIn, Direction facing, BlockState facingState, IWorld worldIn, BlockPos currentPos, BlockPos facingPos)
    {
        return !this.isValidPosition(stateIn, worldIn, currentPos) ? Blocks.AIR.getDefaultState()
                : super.updatePostPlacement(stateIn, facing, facingState, worldIn, currentPos, facingPos);
    }

    @Override
    public ActionResultType onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit)
    {
        ItemStack stack = player.getHeldItem(handIn);

        if (state.get(DECORATION).equals(DecorationTypes.NONE) && stack.getItem().equals(Items.GOLD_INGOT))
        {
            worldIn.setBlockState(pos, state.with(DECORATION, DecorationTypes.GOLD));
            worldIn.playSound(player, pos, SoundEvents.BLOCK_LANTERN_PLACE, SoundCategory.BLOCKS, 1.0F, 1.0F);
            if (!player.isCreative()) player.getHeldItem(handIn).shrink(1);

            return ActionResultType.SUCCESS;
        }
        else if (!state.get(DECORATION).equals(DecorationTypes.NONE) && stack.isEmpty())
        {
            player.setHeldItem(handIn, new ItemStack(Items.GOLD_INGOT));
            worldIn.setBlockState(pos, state.with(DECORATION, DecorationTypes.NONE));

            return ActionResultType.SUCCESS;
        }

        return ActionResultType.PASS;
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context)
    {
        VoxelShape n_down =  makeCuboidShape(6.5d, 6.0d, 8.0d, 9.5d, 9.0d, 16.0d);
        VoxelShape n_up =  makeCuboidShape(6.5d, 12.0d, 8.0d, 9.5d, 15.0d, 16.0d);
        VoxelShape w_down = makeCuboidShape(8.0d, 6.0d, 6.5d, 16.0d, 9.0d, 9.5d);
        VoxelShape w_up = makeCuboidShape(8.0d, 12.0d, 6.5d, 16.0d, 15.0d, 9.5d);
        VoxelShape s_down = makeCuboidShape(6.5d, 6.0d, 0.0d, 9.5d, 9.0d, 8.0d);
        VoxelShape s_up = makeCuboidShape(6.5d, 12.0d, 0.0d, 9.5d, 15.0d, 8.0d);
        VoxelShape e_down = makeCuboidShape(0.0d, 6.0d, 6.5d, 8.0d, 9.0d, 9.5d);
        VoxelShape e_up = makeCuboidShape(0.0d, 12.0d, 6.5d, 8.0d, 15.0d, 9.5d);

        VoxelShape n = VoxelShapes.or(n_up, n_down);
        VoxelShape w = VoxelShapes.or(w_up, w_down);
        VoxelShape s = VoxelShapes.or(s_up, s_down);
        VoxelShape e = VoxelShapes.or(e_up, e_down);

        switch (state.get(FACING))
        {
            case EAST: return e;
            case SOUTH: return s;
            case WEST: return w;
            default: return n;
        }
    }

    public enum DecorationTypes implements IStringSerializable
    {
        NONE("none"),
        GOLD("gold");
//        COPPER("copper");

        private final String name;

        DecorationTypes(String name) {this.name = name;}

        @Override
        public String getString() {return this.name;}
    }
}
