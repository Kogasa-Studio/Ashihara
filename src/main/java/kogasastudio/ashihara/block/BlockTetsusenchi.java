package kogasastudio.ashihara.block;

import kogasastudio.ashihara.item.ItemRegistryHandler;
import kogasastudio.ashihara.sounds.SoundEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.Random;

public class BlockTetsusenchi extends Block {
    public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;

    public BlockTetsusenchi() {
        super
                (
                        Properties.of(Material.WOOD)
                                .strength(2.0F)
                                // todo tag .harvestTool(ToolType.AXE)
                                .sound(SoundType.WOOD)
                                .noOcclusion()
                );
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection());
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
        return Block.box(1.0D, 0.0D, 1.0D, 15.0D, 12.45D, 15.0D);
    }

    @Override
    public InteractionResult use(BlockState state, Level worldIn, BlockPos pos, Player player, InteractionHand handIn, BlockHitResult hit) {
        ItemStack item = player.getItemInHand(handIn);
        if (item.getItem() == ItemRegistryHandler.RICE_CROP.get()) {
            if (!player.getCooldowns().isOnCooldown(item.getItem())) {
                Random rand = worldIn.getRandom();
                worldIn.playSound(player, pos, SoundEvents.UNTHRESH.get(), SoundSource.BLOCKS, 1.0F, 1.0F);
                Containers.dropItemStack(worldIn, pos.getX(), pos.getY(), pos.getZ(), new ItemStack(ItemRegistryHandler.STRAW.get()));
                Containers.dropItemStack(worldIn, pos.getX(), pos.getY(), pos.getZ(), new ItemStack(ItemRegistryHandler.PADDY.get(), rand.nextInt(2) + 1));
                player.getCooldowns().addCooldown(item.getItem(), 8);
                item.shrink(1);
                return InteractionResult.SUCCESS;
            } else {
                return InteractionResult.PASS;
            }
        } else {
            return InteractionResult.PASS;
        }
    }
}
