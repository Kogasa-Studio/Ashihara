package kogasastudio.ashihara.block;

import kogasastudio.ashihara.item.ItemRegistryHandler;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootContext;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import static kogasastudio.ashihara.utils.EasyBlockActionHandler.getLightValueLit;

public class BlockHangingLanternLong extends BlockLantern
{
    public BlockHangingLanternLong()
    {
        super
        (
            Properties.create(Material.WOOL)
            .hardnessAndResistance(1.0F)
            .sound(SoundType.BAMBOO_SAPLING)
            .setLightLevel(getLightValueLit(15))
        );
    }

    @Override
    public List<ItemStack> getDrops(BlockState state, LootContext.Builder builder)
    {
        List<ItemStack> list = new LinkedList<>();
        if (state.matchesBlock(BlockRegistryHandler.LANTERN_LONG_WHITE.get()))
        {
            list.add(new ItemStack(ItemRegistryHandler.LANTERN_LONG_WHITE.get()));
        }
        else if (state.matchesBlock(BlockRegistryHandler.LANTERN_LONG_RED.get()))
        {
            list.add(new ItemStack(ItemRegistryHandler.LANTERN_LONG_RED.get()));
        }
        return list;
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context)
    {
        VoxelShape shape1 = Block.makeCuboidShape(5, 0.5, 5, 11, 1.5, 11);
        VoxelShape shape2 = Block.makeCuboidShape(4, 1.5, 4, 12, 14.5, 12);
        VoxelShape shape3 = Block.makeCuboidShape(5, 14.5, 5, 11, 15.5, 11);
        return VoxelShapes.or(shape1, shape2, shape3);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void animateTick(BlockState stateIn, World worldIn, BlockPos pos, Random rand)
    {
        if (stateIn.get(LIT))
        {
            double d0 = (double)pos.getX() + 0.5D;
            double d1 = (double)pos.getY() + 0.5D;
            double d2 = (double)pos.getZ() + 0.5D;
            worldIn.addParticle(ParticleTypes.FLAME, d0, d1, d2, 0.0D, 0.0D, 0.0D);
        }
    }

    @Override
    public boolean isValidPosition(BlockState state, IWorldReader worldIn, BlockPos pos)
    {
        return worldIn.getBlockState(pos.up()).getBlock() != Blocks.AIR;
    }

    public BlockState updatePostPlacement(BlockState stateIn, Direction facing, BlockState facingState, IWorld worldIn, BlockPos currentPos, BlockPos facingPos)
    {
        return !this.isValidPosition(stateIn, worldIn, currentPos) ? Blocks.AIR.getDefaultState() : super.updatePostPlacement(stateIn, facing, facingState, worldIn, currentPos, facingPos);
    }
}
