package kogasastudio.ashihara.block;

import kogasastudio.ashihara.item.ItemRegistryHandler;
import kogasastudio.ashihara.sounds.SoundEvents;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.HorizontalBlock;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.common.ToolType;

import java.util.Random;

public class BlockTetsusenchi extends Block
{
    public BlockTetsusenchi()
    {
        super
        (
            Properties.create(Material.WOOD)
            .hardnessAndResistance(2.0F)
            .harvestTool(ToolType.AXE)
            .sound(SoundType.WOOD)
            .notSolid()
        );
    }
    public static final DirectionProperty FACING = HorizontalBlock.HORIZONTAL_FACING;

    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context)
    {
        return this.getDefaultState().with(FACING, context.getPlacementHorizontalFacing());
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder)
    {
        builder.add(FACING);
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context)
    {
        return Block.makeCuboidShape(1.0D, 0.0D, 1.0D, 15.0D, 12.45D, 15.0D);
    }

    @Override
    public ActionResultType onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit)
    {
        ItemStack item = player.getHeldItem(handIn);
        if (item.getItem() == ItemRegistryHandler.RICE_CROP.get())
        {
            if (!player.getCooldownTracker().hasCooldown(item.getItem()))
            {
                Random rand = worldIn.getRandom();
                worldIn.playSound(player, pos, SoundEvents.UNTHRESH.get(), SoundCategory.BLOCKS, 1.0F, 1.0F);
                InventoryHelper.spawnItemStack(worldIn, pos.getX(), pos.getY(), pos.getZ(), new ItemStack(ItemRegistryHandler.STRAW.get()));
                InventoryHelper.spawnItemStack(worldIn, pos.getX(), pos.getY(), pos.getZ(), new ItemStack(ItemRegistryHandler.PADDY.get(), rand.nextInt(2) + 1));
                player.getCooldownTracker().setCooldown(item.getItem(), 8);
                item.shrink(1);
                return ActionResultType.SUCCESS;
            }
            else return ActionResultType.PASS;
        }
        else return ActionResultType.PASS;
    }
}
