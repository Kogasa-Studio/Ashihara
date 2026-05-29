package kogasastudio.ashihara.block;

import kogasastudio.ashihara.registry.Items;
import kogasastudio.ashihara.registry.SoundEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
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
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class TetsusenchiBlock extends Block
{
    public static final EnumProperty<Direction> FACING = HorizontalDirectionalBlock.FACING;

    public TetsusenchiBlock(Properties properties)
    {
        super(properties);
    }

    public TetsusenchiBlock()
    {
        this
        (
            Properties.of()
            .mapColor(MapColor.WOOD)
            .strength(2.0F)
            // todo tag .harvestTool(ToolType.AXE)
            .sound(SoundType.WOOD)
            .noOcclusion()
        );
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context)
    {
        return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection());
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        builder.add(FACING);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context)
    {
        return Block.box(1.0D, 0.0D, 1.0D, 15.0D, 12.45D, 15.0D);
    }

    @Override
    public InteractionResult useItemOn(ItemStack stack, BlockState state, Level worldIn, BlockPos pos, Player player, InteractionHand handIn, BlockHitResult hit)
    {
        ItemStack item = player.getItemInHand(handIn);
        if (item.getItem() == Items.DRIED_RICE_CROP.get())
        {
            if (!player.getCooldowns().isOnCooldown(item))
            {
                RandomSource rand = worldIn.getRandom();
                worldIn.playSound(player, pos, SoundEvents.UNTHRESH.get(), SoundSource.BLOCKS, 1.0F, 1.0F);
                Containers.dropItemStack(worldIn, pos.getX(), pos.getY(), pos.getZ(), new ItemStack(Items.STRAW.get()));
                Containers.dropItemStack(worldIn, pos.getX(), pos.getY(), pos.getZ(), new ItemStack(Items.PADDY.get(), rand.nextInt(2) + 1));
                player.getCooldowns().addCooldown(item, 8);
                item.shrink(1);
                return InteractionResult.SUCCESS;
            }
        }
        return super.useItemOn(stack, state, worldIn, pos, player, handIn, hit);
    }
}
